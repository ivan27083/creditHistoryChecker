package com.userService.controller;

import com.userService.model.User;
import com.userService.model.UserDto;
import com.userService.repository.UserRepository;
import com.userService.service.SubscriptionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping("/subscriptions")
@RequiredArgsConstructor
public class SubscriptionController {

    private final SubscriptionService subscriptionService;
    private final UserRepository userRepository;

    public Integer getCurrentUserId() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.getName() != null) {
            String email = auth.getName();
            User user = userRepository.findByEmail(email)
                    .orElseThrow(() -> new UsernameNotFoundException("User not found"));
            return user.getId();
        }
        throw new IllegalStateException("User is not authenticated");
    }

    @PostMapping("/subscribe/{subscribedToId}")
    public ResponseEntity<?> subscribe(@PathVariable Integer subscribedToId) {
        Integer subscriberId = getCurrentUserId();
        subscriptionService.subscribe(subscriberId, subscribedToId);
        return ResponseEntity.ok(Map.of("message", "Subscribed successfully"));
    }

    @DeleteMapping("/unsubscribe/{subscribedToId}")
    public ResponseEntity<?> unsubscribe(@PathVariable Integer subscribedToId) {
        Integer subscriberId = getCurrentUserId();
        subscriptionService.unsubscribe(subscriberId, subscribedToId);
        return ResponseEntity.ok(Map.of("message", "Unsubscribed successfully"));
    }

    @GetMapping("/my-subscriptions")
    public ResponseEntity<Set<UserDto>> getMySubscriptions() {
        Integer userId = getCurrentUserId();
        Set<User> subscriptions = subscriptionService.getSubscriptions(userId);
        Set<UserDto> dtos = subscriptions.stream().map(UserDto::from).collect(Collectors.toSet());
        return ResponseEntity.ok(dtos);
    }

    @GetMapping("/my-subscribers")
    public ResponseEntity<Set<UserDto>> getMySubscribers() {
        Integer userId = getCurrentUserId();
        Set<User> subscribers = subscriptionService.getSubscribers(userId);
        Set<UserDto> dtos = subscribers.stream().map(UserDto::from).collect(Collectors.toSet());
        return ResponseEntity.ok(dtos);
    }

    @GetMapping("/check/{userId}")
    public ResponseEntity<Boolean> isSubscribed(
            @PathVariable Integer userId,
            Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            log.info("Unauthorized access attempt to check subscription for userId {}", userId);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        String currentUsername = authentication.getName();
        log.info("Checking subscription status: currentUser='{}', targetUserId={}", currentUsername, userId);

        try {
            boolean subscribed = subscriptionService.isSubscribed(currentUsername, userId);
            return ResponseEntity.ok(subscribed);
        } catch (Exception e) {
            log.error("Error checking subscription for user '{}', target userId {}: {}", currentUsername, userId, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
