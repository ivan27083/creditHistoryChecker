package com.userService.service;

import com.userService.model.RegisterRequest;
import com.userService.model.User;
import com.userService.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class UserService{
    @Autowired
    UserRepository userRepository;
    @Autowired
    PasswordEncoder encoder;
    public void delete(Integer id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));
        new ArrayList<>(user.getSubscribedTo()).forEach(user::unsubscribeFrom);
        new ArrayList<>(user.getSubscribers()).forEach(u -> u.unsubscribeFrom(user));
        userRepository.delete(user);
    }

    public List<User> findAll() {
        return userRepository.findAll();
    }

    public User findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    public Optional<User> findById(Integer id) {
        return userRepository.findById(id);
    }

    public User save(RegisterRequest request) {
        User entity = new User();
        if (request != null) {
            entity.setEmail(request.getEmail());
            if (request.getPassword() != null) {
                if (!request.getPassword().isEmpty()) {
                    entity.setPassword(encoder.encode(request.getPassword()));
                }
            }
        }
        return userRepository.save(entity);
    }
}
