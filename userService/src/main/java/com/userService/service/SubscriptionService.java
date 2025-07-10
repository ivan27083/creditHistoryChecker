package com.userService.service;

import com.userService.model.User;
import com.userService.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;

@Service
@RequiredArgsConstructor
public class SubscriptionService {

    private final UserRepository userRepository;

    @Transactional
    public void subscribe(Integer subscriberId, Integer subscribedToId) {
        if (subscriberId.equals(subscribedToId)) {
            throw new IllegalArgumentException("Нельзя подписаться на самого себя");
        }

        User subscriber = userRepository.findById(subscriberId)
                .orElseThrow(() -> new EntityNotFoundException("Subscriber not found"));
        User subscribedTo = userRepository.findById(subscribedToId)
                .orElseThrow(() -> new EntityNotFoundException("User to subscribe not found"));

        subscriber.subscribe(subscribedTo);
        userRepository.save(subscriber);
        userRepository.save(subscribedTo);
    }

    @Transactional
    public void unsubscribe(Integer subscriberId, Integer subscribedToId) {
        User subscriber = userRepository.findById(subscriberId)
                .orElseThrow(() -> new EntityNotFoundException("Subscriber not found"));
        User subscribedTo = userRepository.findById(subscribedToId)
                .orElseThrow(() -> new EntityNotFoundException("User to unsubscribe not found"));

        subscriber.unsubscribe(subscribedTo);
        userRepository.save(subscriber);
        userRepository.save(subscribedTo);
    }

    @Transactional(readOnly = true)
    public Set<User> getSubscriptions(Integer userId) {
        return userRepository.findById(userId)
                .map(User::getSubscribedTo)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));
    }

    @Transactional(readOnly = true)
    public Set<User> getSubscribers(Integer userId) {
        return userRepository.findById(userId)
                .map(User::getSubscribers)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));
    }

    public boolean isSubscribed(String currentUserEmail, Integer targetUserId) {
        User currentUser = userRepository.findByEmail(currentUserEmail)
                .orElseThrow(() -> new UsernameNotFoundException("Пользователь не найден"));
        User targetUser = userRepository.findById(targetUserId)
                .orElseThrow(() -> new EntityNotFoundException("Пользователь не найден"));

        return userRepository.existsSubscription(currentUser.getId(), targetUser.getId());
    }
}
