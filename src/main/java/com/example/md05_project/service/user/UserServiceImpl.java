package com.example.md05_project.service.user;

import com.example.md05_project.exception.BookException;
import com.example.md05_project.exception.CustomException;
import com.example.md05_project.exception.UserNotFoundException;
import com.example.md05_project.model.dto.request.userRequest.UserRequestSignInDTO;
import com.example.md05_project.model.dto.request.userRequest.UserRequestSignUpDTO;
import com.example.md05_project.model.dto.request.userRequest.UserRequestUpdateDTO;
import com.example.md05_project.model.dto.response.userResponse.UserResponseDTO;
import com.example.md05_project.model.dto.response.userResponse.UserResponseSignInDTO;
import com.example.md05_project.model.entity.BorrowedCart;
import com.example.md05_project.model.entity.BorrowedCartStatus;
import com.example.md05_project.model.entity.Role;
import com.example.md05_project.model.entity.User;
import com.example.md05_project.repository.BorrowedCartRepository;
import com.example.md05_project.repository.CartRepository;
import com.example.md05_project.repository.UserRepository;
import com.example.md05_project.repository.WaitingListRepository;
import com.example.md05_project.security.jwt.JWTProvider;
import com.example.md05_project.security.jwt.JWTTokenFilter;
import com.example.md05_project.security.user_principle.UserPrinciple;
import com.example.md05_project.service.role.RoleService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements UserService {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private AuthenticationProvider authenticationProvider;
    @Autowired
    private JWTProvider jwtProvider;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private RoleService roleService;
    @Autowired
    private JWTTokenFilter jwtTokenFilter;

    @Autowired
    private BorrowedCartRepository borrowedCartRepository;

    @Autowired
    private WaitingListRepository waitingListRepository;

    @Autowired
    private CartRepository cartRepository;

    @Override
    public UserResponseDTO findByUsername(String username) throws UserNotFoundException {
        User user = userRepository.findByUsername(username);
        if (user == null) {
            throw new UserNotFoundException("User has not been existed!");
        }
        return UserResponseDTO.builder()
                .id(user.getId()).fullName(user.getFullName())
                .username(user.getUsername()).email(user.getEmail())
                .status(user.isStatus()).phone(user.getPhone())
                .address(user.getAddress()).roles(user.getRoles())
                .build();
    }

    @Override
    public UserResponseDTO findById(Long id) throws UserNotFoundException, BookException, CustomException {
        User user = userRepository.findById(id).orElseThrow(() ->
                new UserNotFoundException("User is not found with this id " + id));

        blockUserWhenReturnBooksExpire(user);
        return new UserResponseDTO(user);
    }


    @Override
    public UserResponseDTO update(Long id) throws UserNotFoundException {
        User user = userRepository.findById(id).orElseThrow(() ->
                new UserNotFoundException("User is not found with this id " + id));

        return new UserResponseDTO(userRepository.save(user));
    }

    @Override
    public UserResponseDTO updateAccount(UserRequestUpdateDTO userRequestUpdateDTO, HttpServletRequest request, String password) throws UserNotFoundException {
        UserResponseDTO userResponseDTO = getAccount(request);
        User user = userRepository.findById(userResponseDTO.getId()).orElse(null);

        assert user != null;
        User updatedUser = userRepository.save(User.builder()
                .id(userResponseDTO.getId())
                .fullName(userRequestUpdateDTO.getFullName())
                .username(userResponseDTO.getUsername())
                .email(userRequestUpdateDTO.getEmail())
                .phone(userRequestUpdateDTO.getPhone())
                .address(userRequestUpdateDTO.getAddress())
                .status(userResponseDTO.isStatus())
                .roles(userResponseDTO.getRoles())
                .password(user.getPassword())
                .build());

//kiem tra mat khau nhap vao co khop khong
        if (passwordEncoder.matches(password, user.getPassword())) {
            return new UserResponseDTO(updatedUser);
        } else {
            throw new UserNotFoundException("Password doesn't match");
        }
    }


    @Override
    public Page<UserResponseDTO> findAll(Pageable pageable) throws BookException, CustomException {
        Page<User> userPage = userRepository.findAll(pageable);
        for (User user : userPage) {
            blockUserWhenReturnBooksExpire(user);
        }
        return userPage.map(UserResponseDTO::new);
    }

    @Override
    public Page<UserResponseDTO> searchByUsernameWithPaginationAndSort(Pageable pageable, String name) throws BookException, CustomException {
        Page<User> userPage = userRepository.findAllByUsernameContainingIgnoreCase(pageable, name);
        for (User user : userPage) {
            blockUserWhenReturnBooksExpire(user);
        }
        return userPage.map(UserResponseDTO::new);
    }

    @Override
    public UserResponseDTO register(UserRequestSignUpDTO user) throws UserNotFoundException {
        //check trung username
        if (userRepository.existsByUsername(user.getUsername())) {
            throw new UserNotFoundException("Username existed!");
        }
        //ma hoa mat khau
        user.setPassword(passwordEncoder.encode(user.getPassword()));

        //role
        Set<Role> roles = new HashSet<>();
        //neu khong dang ky role thi mac dinh role=USER
        if (user.getRoles() == null || user.getRoles().isEmpty()) {
            roles.add(roleService.findRoleByName("USER"));
        } else if (user.getRoles().stream().anyMatch(role -> role.equalsIgnoreCase("ADMIN"))) {
            roles.add(roleService.findRoleByName("USER"));
        } else {
            user.getRoles().forEach(role -> {
                roles.add(roleService.findRoleByName(role));
            });
        }
        User newUser = userRepository.save(User.builder()
                .fullName(user.getFullName()).username(user.getUsername())
                .email(user.getEmail()).status(user.isStatus()).password(user.getPassword())
                .roles(roles)
                .build());
        return UserResponseDTO.builder()
                .id(newUser.getId())
                .fullName(newUser.getFullName()).username(newUser.getUsername())
                .email(newUser.getEmail()).status(newUser.isStatus())
                .roles(newUser.getRoles())
                .build();
    }

    @Override
    public UserResponseSignInDTO login(UserRequestSignInDTO user) throws UserNotFoundException {
        Authentication authentication;

        // Xác thực người dùng bằng cách sử dụng thông tin đăng nhập (username và password)
        authentication = authenticationProvider.authenticate(new UsernamePasswordAuthenticationToken(user.getUsername(), user.getPassword()));

        // Lấy thông tin người dùng đã được xác thực từ đối tượng Authentication
        UserPrinciple userPrinciple = (UserPrinciple) authentication.getPrincipal();

        //kiem tra status cua nugoi dung
        if (!userPrinciple.getUser().isStatus()) {
            throw new UserNotFoundException("Account has been blocked!");
        }

        // Tạo và trả về đối tượng UserResponseDTO chứa thông tin về người dùng đã đăng nhập thành công
        return UserResponseSignInDTO.builder()
                .token(jwtProvider.generateToken(userPrinciple))
                .username(userPrinciple.getUsername())
                .roles(userPrinciple.getAuthorities().stream().map(GrantedAuthority::getAuthority).collect(Collectors.toSet()))
                .build();
    }


    @Override
    public UserResponseDTO changeStatus(Long id) throws UserNotFoundException, BookException, CustomException {
        UserResponseDTO user = findById(id);
        if (user.getRoles().stream().anyMatch(role -> role.getName().equalsIgnoreCase("ADMIN"))) {
            throw new UserNotFoundException("Can't change ADMIN' status");
        }
        userRepository.changeStatus(id);
        return user;
    }

    @Override
    public UserResponseDTO addRole(String role, Long id) throws UserNotFoundException, BookException, CustomException {
        UserResponseDTO userResponseDTO = findById(id);
        if (userResponseDTO.getRoles().stream().anyMatch(user -> role.equalsIgnoreCase(user.getName()))) {
            throw new UserNotFoundException("User's role existed");
        }
        userResponseDTO.getRoles().add(roleService.findRoleByName(role));

        return update(id);
    }

    @Override
    public UserResponseDTO deleteRole(String role, Long id) throws UserNotFoundException, BookException, CustomException {
        UserResponseDTO user = findById(id);
        if (user.getId() == 7 && user.getRoles().stream().anyMatch(userRole -> userRole.getName().equalsIgnoreCase("ADMIN"))) {
            throw new UserNotFoundException("Can't change ADMIN' roles");
        }
        if (user.getRoles().stream().noneMatch(userRole -> userRole.getName().equalsIgnoreCase(role))) {
            throw new UserNotFoundException("User's role has not been existed");
        }
        user.getRoles().remove(roleService.findRoleByName(role));

        return update(id);
    }

    @Override
    public void blockUserWhenReturnBooksExpire(User user) throws BookException, CustomException {
        List<BorrowedCart> list = borrowedCartRepository.findAllByUser_Id(user.getId());
        if (user.getRoles().stream().anyMatch(role -> role.equals("ADMIN"))) {
            throw new CustomException("Can't block admin");
        }
        for (BorrowedCart borrowedCart : list) {
            if (borrowedCart.getUser().getId().equals(user.getId()) &&
                    borrowedCart.getBorrowedCartStatus().equals(BorrowedCartStatus.BORROWED) &&
                    user.isStatus() && user.getRoles().stream().noneMatch(role -> role.equals("ADMIN"))) {
                userRepository.blockUser(user.getId());
            }
        }
    }

    @Override
    public UserResponseDTO getAccount(HttpServletRequest request) throws UserNotFoundException {
        String token = jwtTokenFilter.getTokenFromRequest(request);
        if (token != null) {
            String username = jwtProvider.getUsernameToken(token);
            return findByUsername(username);
        }
        throw new UserNotFoundException("Please login!");
    }

    @Override
    public void changePassword(String oldPassword, String newPassword, String confirmPassword, HttpServletRequest request) throws UserNotFoundException, BookException, CustomException, ServletException {
        UserResponseDTO userResponseDTO = getAccount(request);
        User user = userRepository.findById(userResponseDTO.getId()).orElse(null);

        assert user != null;
        if (!passwordEncoder.matches(oldPassword, user.getPassword())) {
            throw new UserNotFoundException("Old password doesn't match");
        }

        if (newPassword.length() < 4 || newPassword.length() > 8) {
            throw new UserNotFoundException("New password's length must be between 4 and 8");
        }

        if (passwordEncoder.matches(newPassword, user.getPassword())) {
            throw new UserNotFoundException("New password must be different from the old password");
        }

        if (confirmPassword.length() < 4 || confirmPassword.length() > 8) {
            throw new UserNotFoundException("Password's length must be between 4 and 8");
        }

        if (!newPassword.equals(confirmPassword)) {
            throw new UserNotFoundException("Password doesn't match");
        }

        //ma hoa mat khau
        user.setPassword(passwordEncoder.encode(newPassword));

        userRepository.save(user);
        logout(request);
    }

    @Override
    public void logout(HttpServletRequest request) throws ServletException, UserNotFoundException {
       UserResponseDTO userResponseDTO= getAccount(request);

        //xoa waitingList
        waitingListRepository.deleteByUserId(userResponseDTO.getId());

        //xoa cart
        cartRepository.deleteByUserId(userResponseDTO.getId());

        request.logout();
        SecurityContextHolder.clearContext();

        //xoa token khi dang nhap
        //cach 1: su ly ben front-end
        //cach 2: luu token vao database, khi logout thi xoa token di
    }


}
