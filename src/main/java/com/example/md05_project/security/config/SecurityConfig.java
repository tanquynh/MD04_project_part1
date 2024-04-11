package com.example.md05_project.security.config;

import com.example.md05_project.security.jwt.AccessDenied;
import com.example.md05_project.security.jwt.JWTEntryPoint;
import com.example.md05_project.security.jwt.JWTTokenFilter;
import com.example.md05_project.security.user_principle.UserDetailService;
import jakarta.servlet.ServletException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {
    @Autowired
   private UserDetailService userDetailService;

    @Autowired
    private JWTEntryPoint jwtEntryPoint;

    @Autowired
    private JWTTokenFilter jwtTokenFilter;
    @Autowired
    private AccessDenied accessDenied;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity httpSecurity) throws Exception {
        //Phương thức này tạo và cấu hình SecurityFilterChain, là một chuỗi bộ lọc bảo mật trong Spring Security
        return httpSecurity.csrf(AbstractHttpConfigurer::disable).authenticationProvider(authenticationProvider())
                //.csrf(AbstractHttpConfigurer::disable): Vô hiệu hóa CSRF (Cross-Site Request Forgery)
                // để cho phép gửi yêu cầu từ các nguồn không đáng tin cậy.

                //Cấu hình xác thực và phân quyền truy cập
                .authorizeHttpRequests(auth->{
                    auth.requestMatchers("/api.myservice.com/v1/*","/api.myservice.com/v1/auth/**",
                                    "/api.myservice.com/v1/books","/api.myservice.com/v1/books/**",
                                    "/api.myservice.com/v1/genres","/api.myservice.com/v1/genres/**").permitAll()
                            .requestMatchers("/api.myservice.com/v1/user/**").hasAuthority("USER")
                            .requestMatchers("/api.myservice.com/v1/admin/**").hasAuthority("ADMIN")
                            .anyRequest().authenticated();})

                //Xử lý ngoại lệ
                .exceptionHandling(auth->
                        auth.authenticationEntryPoint(jwtEntryPoint)
                                .accessDeniedHandler(accessDenied))

//                //logout
//                .logout(logout -> {
//                    logout.logoutUrl("/api.myservice.com/v1/user/logout")
//                            .addLogoutHandler((request, response, authentication) -> {
//                                try {
//                                    request.logout();
//                                } catch (ServletException e) {
//                                    throw new RuntimeException(e);
//                                }
//                            })
//                            .logoutSuccessUrl("/api.myservice.com/v1/books");
//
//                    // Thêm đường dẫn logout cho giao diện admin
//                    logout.logoutUrl("/api.myservice.com/v1/admin/logout")
//                            .addLogoutHandler((request, response, authentication) -> {
//                                try {
//                                    request.logout();
//                                } catch (ServletException e) {
//                                    throw new RuntimeException(e);
//                                }
//                            })
//                            .logoutSuccessUrl("/api.myservice.com/v1/auth/login");
//                    // Đường dẫn chuyển hướng sau khi đăng xuất thành công cho giao diện admin
//                })

                //cấu hình quyền quản lý phiên làm việc trong một ứng dụng web (phi trang thai)
                .sessionManagement(auth->auth.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .addFilterBefore(jwtTokenFilter, UsernamePasswordAuthenticationFilter.class)
                .build();
    }

    @Bean
    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationProvider authenticationProvider(){
        DaoAuthenticationProvider authenticationProvider=new DaoAuthenticationProvider();
        authenticationProvider.setUserDetailsService(userDetailService);
        authenticationProvider.setPasswordEncoder(passwordEncoder());
        return authenticationProvider;
    }
}
