package com.centralService.controller;

import com.centralService.client.UserServiceRestClient;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequiredArgsConstructor
@RequestMapping("/auth")
public class AuthController {

    private final UserServiceRestClient userClient;

    @PostMapping("/login")
    public String login(@RequestParam String email,
                        @RequestParam String password,
                        HttpSession session,
                        RedirectAttributes redirectAttributes) {
        try {
            String token = userClient.login(email, password);
            session.setAttribute("token", token);
            return "redirect:/profile";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Неверный логин или пароль");
            return "redirect:/login";
        }
    }

    @PostMapping("/register")
    public String register(@RequestParam String username,
                           @RequestParam String email,
                           @RequestParam String password,
                           HttpSession session,
                           RedirectAttributes redirectAttributes) {
        try {
            userClient.register(username, email, password);

            String token = userClient.login(email, password);
            session.setAttribute("token", token);

            return "redirect:/profile";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Регистрация не удалась: " + e.getMessage());
            return "redirect:/register";
        }
    }
}