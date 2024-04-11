package com.example.md05_project.security.jwt;

import com.example.md05_project.security.user_principle.UserDetailService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class JWTTokenFilter extends OncePerRequestFilter {
    private final Logger LOGGER = LoggerFactory.getLogger(JWTEntryPoint.class);
    @Autowired
    private JWTProvider jwtProvider;
    @Autowired
    private UserDetailService userDetailService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        //Phương thức doFilterInternal là một phương thức được ghi đè từ interface javax.servlet.Filter
        // và được sử dụng để thực hiện xác thực và phân quyền truy cập dựa trên mã thông báo JWT (JSON Web Token)

        String token = getTokenFromRequest(request);
        try {
            //kiểm tra xem mã thông báo không null và hợp lệ bằng cách sử dụng đối tượng jwtProvider để xác thực mã thông báo
            if (token != null && jwtProvider.validateToken(token)) {
                //lấy tên người dùng từ mã thông báo JWT sử dụng jwtProvider.

                String username = jwtProvider.getUsernameToken(token);
                //sử dụng userDetailService để tải chi tiết người dùng (bao gồm thông tin xác thực và phân quyền) dựa trên tên người dùng

                UserDetails userDetails = userDetailService.loadUserByUsername(username);
            //kiểm tra xem chi tiết người dùng có tồn tại hay không.
                if (userDetails != null) {

                    //tạo một đối tượng UsernamePasswordAuthenticationToken để đại diện cho thông tin xác thực của người dùng. Đối tượng này được khởi tạo
                    // với chi tiết người dùng, mật khẩu (ở đây là null vì không cần mật khẩu trong xác thực JWT) và quyền hạn của người dùng
                    UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());

                    //đặt chi tiết xác thực web bổ sung cho đối tượng authenticationToken, bao gồm thông tin về yêu cầu HTTP
                    authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                    //đặt đối tượng xác thực vào SecurityContextHolder, với vai trò xác thực đã được xác minh thành công
                    SecurityContextHolder.getContext().setAuthentication(authenticationToken);
                }
            }
        }catch (Exception e){
            LOGGER.error("UNAUTHENTICATED {}", e.getMessage());
        }
        //chuyển tiếp yêu cầu và phản hồi tới lớp bộ lọc tiếp theo trong chuỗi bộ lọc, cho phép yêu cầu tiếp tục xử lý
        filterChain.doFilter(request,response);
    }

    public String getTokenFromRequest(HttpServletRequest request) {
        String header = request.getHeader("Authorization");
        if (header != null && header.startsWith("Bearer ")) {
            return header.substring(7);
        }
        return null;
    }
}
