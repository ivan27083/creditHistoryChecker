package com.centralService.client;

import lombok.RequiredArgsConstructor;
import org.springframework.web.client.RestClient;
@RequiredArgsConstructor
public class userServiceRestClient {
    private final RestClient restClient;

}
