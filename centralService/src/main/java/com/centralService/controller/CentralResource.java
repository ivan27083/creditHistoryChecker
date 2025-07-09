package com.centralService.controller;

import com.centralService.client.PostServiceRestClient;
import com.centralService.client.SubscriptionRestClient;
import com.centralService.client.UserServiceRestClient;
import com.centralService.exception.UnauthorizedException;
import com.centralService.model.ImgBBResponse;
import com.centralService.model.PostDto;
import com.centralService.model.UserDto;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Controller
@RequiredArgsConstructor
public class CentralResource {

    private final UserServiceRestClient userClient;
    private final PostServiceRestClient postClient;
    private final SubscriptionRestClient subscriptionClient;

    @GetMapping({"/", "index", "home"})
    public String index(Model model) {
        List<PostDto> posts = postClient.getAllPosts();
        model.addAttribute("posts", posts);
        return "index";
    }

    @GetMapping("/profile")
    public String profile(Model model, HttpSession session) {
        String token = (String) session.getAttribute("token");
        if (token == null) {
            return "redirect:/login";
        }

        try {
            UserDto user = userClient.getCurrentUser(token);
            List<PostDto> posts = postClient.getPostsByUserId(user.getId());
            int subscriberCount = user.getSubscriberIds().size();

            model.addAttribute("user", user);
            model.addAttribute("posts", posts);
            model.addAttribute("subscriberCount", subscriberCount);
            return "profile";
        } catch (UnauthorizedException e) {
            return "redirect:/login";
        } catch (Exception e) {
            model.addAttribute("message", e.getMessage());
            return "error";
        }
    }

    @GetMapping("/user/{id}")
    public String viewUser(@PathVariable Integer id, Model model) {
        try {
            UserDto user = userClient.getUserById(id);
            List<PostDto> posts = postClient.getPostsByUserId(id);
            int subscriberCount = user.getSubscriberIds().size();

            model.addAttribute("user", user);
            model.addAttribute("posts", posts);
            model.addAttribute("subscriberCount", subscriberCount);
            return "user-profile";
        } catch (Exception e) {
            model.addAttribute("message", e.getMessage());
            return "error";
        }
    }

    @GetMapping("/create")
    public String createPage() {
        return "create";
    }

    @PostMapping("/create")
    public String createPost(@RequestParam String title,
                             @RequestParam(required = false) String text,
                             @RequestParam(required = false) MultipartFile[] images,
                             HttpServletRequest request,
                             Model model) {
        try {
            String token = extractToken(request);
            UserDto user = userClient.getCurrentUser(token);

            List<PostDto.ImageDto> imageDtos = new ArrayList<>();
            if (images != null) {
                int count = Math.min(images.length, 9);
                for (int i = 0; i < count; i++) {
                    MultipartFile image = images[i];
                    ImgBBResponse response = postClient.uploadImage(image, token);
                    imageDtos.add(new PostDto.ImageDto(null, response.getImageUrl(), response.getDeleteUrl()));
                }
            }

            PostDto post = new PostDto(null, user.getId(), title, text, imageDtos, LocalDateTime.now());
            postClient.createPost(post, token);

            return "redirect:/";
        } catch (Exception e) {
            model.addAttribute("message", e.getMessage());
            return "error";
        }
    }

    private String extractToken(HttpServletRequest request) {
        String header = request.getHeader("Authorization");
        if (header == null || !header.startsWith("Bearer ")) {
            throw new IllegalStateException("Missing or invalid Authorization header");
        }
        return header.substring(7);
    }
}

