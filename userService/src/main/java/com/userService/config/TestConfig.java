package com.userService.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

//@Configuration
//public class TestConfig {
//    @Bean
//    @Primary
//    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
//        http
//                .csrf(csrf -> csrf.disable()) // Отключаем CSRF
//                .authorizeHttpRequests(auth -> auth
//                        .anyRequest().permitAll() // Разрешаем все запросы
//                );
//        return http.build();
//    }
//
//    @Bean
//    public PasswordEncoder passwordEncoder() {
//        return new BCryptPasswordEncoder();
//    }
//}
