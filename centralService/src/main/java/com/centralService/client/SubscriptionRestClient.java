package com.centralService.client;

import com.centralService.model.UserDto;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.Set;

@Component
@RequiredArgsConstructor
public class SubscriptionRestClient {

    private final RestTemplate restTemplate;

    @Value("${user-service.url}")
    private String userServiceUrl;

    public void subscribe(Integer userId, String token) {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token);
        HttpEntity<Void> entity = new HttpEntity<>(headers);

        restTemplate.exchange(
                userServiceUrl + "/subscriptions/subscribe/" + userId,
                HttpMethod.POST,
                entity,
                Void.class
        );
    }

    public void unsubscribe(Integer userId, String token) {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token);
        HttpEntity<Void> entity = new HttpEntity<>(headers);

        restTemplate.exchange(
                userServiceUrl + "/subscriptions/unsubscribe/" + userId,
                HttpMethod.DELETE,
                entity,
                Void.class
        );
    }

    public Set<UserDto> getSubscribers(Integer userId) {
        String url = userServiceUrl + "/subscriptions/my-subscribers";
        HttpEntity<Void> request = new HttpEntity<>(createHeaders(userId));

        ResponseEntity<Set<UserDto>> response = restTemplate.exchange(
                url,
                HttpMethod.GET,
                request,
                new ParameterizedTypeReference<>() {}
        );

        return response.getBody();
    }

    public Set<UserDto> getSubscriptions(Integer userId) {
        String url = userServiceUrl + "/subscriptions/my-subscriptions";
        HttpEntity<Void> request = new HttpEntity<>(createHeaders(userId));

        ResponseEntity<Set<UserDto>> response = restTemplate.exchange(
                url,
                HttpMethod.GET,
                request,
                new ParameterizedTypeReference<>() {}
        );

        return response.getBody();
    }

    public boolean isSubscribed(Integer userId, String token) {
        String url = userServiceUrl + "/subscriptions/check/" + userId;
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token);

        HttpEntity<Void> request = new HttpEntity<>(headers);

        try {
            ResponseEntity<Boolean> response = restTemplate.exchange(
                    url,
                    HttpMethod.GET,
                    request,
                    Boolean.class
            );
            return Boolean.TRUE.equals(response.getBody());
        } catch (HttpClientErrorException.NotFound e) {
            return false;
        } catch (Exception e) {
            throw new RuntimeException("Ошибка проверки подписки: " + e.getMessage(), e);
        }
    }

    private HttpHeaders createHeaders(Integer userId) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("X-User-Id", userId.toString());
        return headers;
    }
}
