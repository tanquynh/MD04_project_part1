package com.example.md05_project.security.user_principle;

import com.example.md05_project.model.entity.User;
import com.example.md05_project.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.stream.Collectors;

@Service
public class UserDetailService implements UserDetailsService {
    @Autowired
    private UserRepository userRepository;
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user=userRepository.findByUsername(username);
        return UserPrinciple.builder()
                .user(user)
                .authorities(user.getRoles().stream().map(item->new SimpleGrantedAuthority(item.getName())).collect(Collectors.toSet()))
                .build();
    }
}
