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
import org.springframework.web.client.RestTemplate;

import java.util.Set;

@Component
@RequiredArgsConstructor
public class SubscriptionRestClient {

    private final RestTemplate restTemplate;

    @Value("${user-service.url}")
    private String userServiceUrl;

    public void subscribe(Integer subscriberId, Integer subscribedToId) {
        String url = userServiceUrl + "/subscriptions/subscribe/" + subscribedToId;
        HttpEntity<Void> request = new HttpEntity<>(createHeaders(subscriberId));
        restTemplate.exchange(url, HttpMethod.POST, request, Void.class);
    }

    public void unsubscribe(Integer subscriberId, Integer subscribedToId) {
        String url = userServiceUrl + "/subscriptions/unsubscribe/" + subscribedToId;
        HttpEntity<Void> request = new HttpEntity<>(createHeaders(subscriberId));
        restTemplate.exchange(url, HttpMethod.DELETE, request, Void.class);
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

    private HttpHeaders createHeaders(Integer userId) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("X-User-Id", userId.toString());
        return headers;
    }
}
