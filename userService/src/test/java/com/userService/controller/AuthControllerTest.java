package com.userService.controller;

import com.userService.service.JwtUtil;
import com.userService.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Test class for the {@link AuthController}
 */
@SpringBootTest
@AutoConfigureMockMvc
@Import(AuthControllerTest.TestConfig.class)
@Transactional
public class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserService usersService;

    @Autowired
    private JwtUtil jwtUtil;

    @TestConfiguration
    static class TestConfig {
        @Bean
        public UserService usersService() {
            return Mockito.mock(UserService.class);
        }

        @Bean
        public JwtUtil jwtUtil() {
            return Mockito.mock(JwtUtil.class);
        }
    }

    @BeforeEach
    public void setup() {
    }

    @Test
    public void login_ValidCredentials_ReturnsToken() throws Exception {
        String email = "admin@mail.ru";
        String password = "admin";
        List<String> roles = List.of("ROLE_USER","ROLE_MANAGER");
        String token = "mocked.jwt.token";

        when(usersService.checkCredentials(eq(email), eq(password)))
                .thenReturn(true);
        when(usersService.getUserRoles(eq(email)))
                .thenReturn(roles);
        when(jwtUtil.generateToken(eq(email), eq(roles)))
                .thenReturn(token);

        String loginRequest = """
            {
                "email": "admin@mail.ru",
                "password": "admin"
            }
        """;

        mockMvc.perform(post("/auth/login")
                        .content(loginRequest)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value(token))
                .andDo(print());
    }

    @Test
    public void login_InvalidCredentials_Returns401() throws Exception {
        when(usersService.checkCredentials(anyString(), anyString()))
                .thenReturn(false);

        String loginRequest = """
            {
                "email": "wrong@mail.ru",
                "password": "wrongpass"
            }
        """;

        mockMvc.perform(post("/auth/login")
                        .content(loginRequest)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.error").value("Invalid username or password"))
                .andDo(print());
    }

    @Test
    public void register_ValidRequest_ReturnsOk() throws Exception {
        String request = """
            {
                "username": "newuser",
                "email": "test@mail.ru",
                "password": "12345"
            }
        """;

        doNothing().when(usersService).saveUser(any());

        mockMvc.perform(post("/auth/register")
                        .content(request)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(print());
    }

    @Test
    public void removeUserByUserServiceId_ValidId_ReturnsOk() throws Exception {
        int testId = 123;

        doNothing().when(usersService).removeUserById(eq(testId));

        mockMvc.perform(delete("/auth/remove/{id}", testId))
                .andExpect(status().isOk())
                .andDo(print());
    }
}
