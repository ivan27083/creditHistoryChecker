package com.centralService.client;

import com.centralService.exception.UnauthorizedException;
import com.centralService.model.UserDto;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
@Component
public class UserServiceRestClient {

    private final RestTemplate restTemplate;
    @Value("${user-service.url}")
    private String userServiceUrl;

    public UserDto getUserById(Integer id, String token) {
        HttpHeaders headers = createAuthHeaders(token);
        HttpEntity<Void> request = new HttpEntity<>(headers);

        return restTemplate.exchange(
                userServiceUrl + "/users/{id}",
                HttpMethod.GET,
                request,
                UserDto.class,
                id
        ).getBody();
    }

    public List<UserDto> getAllUsers() {
        ResponseEntity<List<UserDto>> response = restTemplate.exchange(
                userServiceUrl + "/users",
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<List<UserDto>>() {}
        );
        return response.getBody();
    }

    public void deleteUser(Integer id) {
        restTemplate.delete(userServiceUrl + "/users/" + id);
    }

    public UserDto getByEmail(String email) {
        return restTemplate.getForObject(userServiceUrl + "/users/by-email?email=" + email, UserDto.class);
    }

    public String login(String email, String password) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        Map<String, String> body = Map.of("email", email, "password", password);
        HttpEntity<Map<String, String>> request = new HttpEntity<>(body, headers);

        ResponseEntity<Map<String, String>> response = restTemplate.exchange(
                userServiceUrl + "/auth/login",
                HttpMethod.POST,
                request,
                new ParameterizedTypeReference<Map<String, String>>() {}
        );

        // Получаем токен из ответа по ключу "token"
        return response.getBody().get("token");
    }

    public void register(String username, String email, String password) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        Map<String, String> body = Map.of(
                "username", username,
                "email", email,
                "password", password
        );
        HttpEntity<Map<String, String>> request = new HttpEntity<>(body, headers);

        restTemplate.exchange(
                userServiceUrl + "/auth/register",
                HttpMethod.POST,
                request,
                Void.class
        );
    }

    public UserDto getCurrentUser(String token) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setBearerAuth(token);
            HttpEntity<Void> entity = new HttpEntity<>(headers);

            ResponseEntity<UserDto> response = restTemplate.exchange(
                    userServiceUrl + "/users/me",
                    HttpMethod.GET,
                    entity,
                    UserDto.class
            );
            return response.getBody();
        } catch (HttpClientErrorException.Unauthorized e) {
            throw new UnauthorizedException("User not authorized");
        }
    }

    private HttpHeaders createAuthHeaders(String token) {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token);
        return headers;
    }
}
