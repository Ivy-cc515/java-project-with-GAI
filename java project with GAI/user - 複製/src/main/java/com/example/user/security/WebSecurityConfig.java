package com.example.user.security;

// 主要使用 Spring Security 設置應用程序的安全性，包括密碼編碼、用戶管理和 HTTP 請求的授權配置
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity; // HttpSecurity：配置 HTTP 請求的安全性
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder; // PasswordEncoder 和 BCryptPasswordEncoder：用於密碼編碼和驗證
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain; // SecurityFilterChain：定義安全過濾鏈
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService; // UserDetailsService 和 InMemoryUserDetailsManager：用於管理應用程序的用戶詳細信息
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.config.http.SessionCreationPolicy; // SessionCreationPolicy：配置session管理策略

@Configuration
public class WebSecurityConfig {

    // 定義一個 PasswordEncoder Bean
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();// 使用 BCrypt 加密密碼
    }

    // 定義一個 SecurityFilterChain Bean 來配置 HTTP 安全性
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable())  // 禁用 CSRF 保護
            .authorizeHttpRequests(auth -> auth // 配置請求授權
                .requestMatchers("/present", "/qrcode").authenticated()  // 只有經過認證的用戶才能訪問 /present 和 /qrcode 路徑
                .anyRequest().permitAll())  // 其他所有請求都允許
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))  // 設置session為無狀態
            .httpBasic(httpBasic -> httpBasic.realmName("YourApp"));  // 啟用 HTTP 基本認證，並設置領域名
        return http.build(); 
    }

    // 定義一個 UserDetailsService Bean
    @Bean
    public UserDetailsService userDetailsService(PasswordEncoder passwordEncoder) {
        // 使用指定的密碼編碼器來編碼密碼
        User.UserBuilder users = User.builder().passwordEncoder(passwordEncoder::encode);
        // 創建一個用戶 admin，密碼為 123，角色為 ADMIN
        UserDetails user = users
            .username("admin")
            .password("123")
            .roles("ADMIN")
            .build();
        return new InMemoryUserDetailsManager(user);
    }
}


