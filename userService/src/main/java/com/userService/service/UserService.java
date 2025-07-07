package com.userService.service;

import com.userService.model.RegisterRequest;
import com.userService.model.Role;
import com.userService.model.User;
import com.userService.repository.RoleRepository;
import com.userService.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService {

    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;

    public boolean checkCredentials(String email, String password) {
        Optional<User> userOpt = userRepository.findByEmail(email);
        return userOpt.map(user -> passwordEncoder.matches(password, user.getPassword()))
                .orElse(false);
    }

    public List<String> getUserRoles(String email) {
        Optional<User> userOpt = userRepository.findByEmail(email);
        if (userOpt.isEmpty()) {
            return Collections.emptyList();
        }
        User user = userOpt.get();
        List<Role> roles = roleRepository.findByUserId(user.getId());
        return roles.stream()
                .map(Role::getName)
                .collect(Collectors.toList());
    }

    @Transactional
    public void saveUser(RegisterRequest request) {
        User user = new User();
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setUsername(request.getUsername());

        User savedUser = userRepository.save(user);
        Role userRole = roleRepository.findRoleByName("ROLE_USER")
                .orElseThrow(() -> new RuntimeException("Роль ROLE_USER не найдена"));

        roleRepository.insertIntoUserRoles(savedUser.getId(), userRole.getId());
    }

    @Transactional
    public void removeUserById(int userId) {
        Optional<User> user = userRepository.findById(userId);
        roleRepository.removeAllByUserId(userId);
        userRepository.deleteById(userId);
    }
}
