package com.centralService.controller;

import com.centralService.client.PostServiceRestClient;
import com.centralService.client.SubscriptionRestClient;
import com.centralService.client.UserServiceRestClient;
import com.centralService.exception.UnauthorizedException;
import com.centralService.model.ImgBBResponse;
import com.centralService.model.PostDto;
import com.centralService.model.UserDto;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Controller
@RequiredArgsConstructor
public class CentralResource {
    private final UserServiceRestClient userClient;
    private final PostServiceRestClient postClient;
    private final SubscriptionRestClient subscriptionClient;

    @GetMapping({"/", "/index", "/home"})
    public String index(Model model, HttpSession session) {
        try {
            String token = getSessionToken(session);

            List<PostDto> posts = postClient.getAllPaged(0, 20).getContent();
            for (PostDto post : posts) {
                if (post.getUsername() == null) {
                    UserDto author = userClient.getUserById(post.getUserId(), token);
                    post.setUsername(author.getUsername());
                }
            }
            model.addAttribute("posts", posts);
            return "index";
        }
        catch (HttpClientErrorException.Unauthorized | UnauthorizedException e) {
            return "redirect:/login";
        }
        catch (Exception e) {
            model.addAttribute("error", "Ошибка загрузки данных. Попробуйте позже.");
            return "error";
        }
    }

    @GetMapping("/profile")
    public String profile(Model model, HttpSession session) {
        try {
            String token = getSessionToken(session);
            UserDto user = userClient.getCurrentUser(token);
            List<PostDto> posts = postClient.getPostsByUserId(user.getId());

            model.addAttribute("user", user);
            model.addAttribute("subscriberCount", user.getSubscriberIds().size());
            model.addAttribute("posts", posts);
            return "profile";
        }
        catch (HttpClientErrorException.Unauthorized | UnauthorizedException e) {
            return "redirect:/login";
        }
        catch (Exception e) {
            model.addAttribute("error", "Ошибка загрузки профиля: " + e.getMessage());
            return "error";
        }
    }

    @GetMapping("/user/{userId}")
    public String viewUser(@PathVariable Integer userId, Model model, HttpSession session) {
        try {
            String token = getSessionToken(session);
            UserDto currentUser = userClient.getCurrentUser(token);

            if (currentUser.getId().equals(userId)) {
                return "redirect:/profile";
            }

            UserDto user = userClient.getUserById(userId, token);
            List<PostDto> posts = postClient.getPostsByUserId(userId);
            boolean subscribed = subscriptionClient.isSubscribed(userId, token);

            model.addAttribute("user", user);
            model.addAttribute("subscriberCount", user.getSubscriberIds().size());
            model.addAttribute("posts", posts);
            model.addAttribute("subscribed", subscribed);
            return "user-profile";
        }
        catch (HttpClientErrorException.Unauthorized | UnauthorizedException e) {
            return "redirect:/login";
        }
        catch (Exception e) {
            model.addAttribute("error", "Ошибка загрузки профиля пользователя: " + e.getMessage());
            return "error";
        }
    }

    @GetMapping("/create")
    public String createPage(HttpSession session) {
        if (session.getAttribute("token") == null) {
            return "redirect:/login";
        }
        return "create";
    }

    @PostMapping(value = "/create", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public String createPost(@RequestParam String title,
                             @RequestParam(required = false) String text,
                             @RequestParam(required = false) MultipartFile[] images,
                             HttpSession session,
                             Model model) {
        try {
            String token = getSessionToken(session);
            UserDto user = userClient.getCurrentUser(token);

            PostDto postDto = new PostDto();
            postDto.setTitle(title);
            postDto.setText(text);
            postDto.setUserId(user.getId());

            postClient.createPostWithImages(postDto, images != null ? List.of(images) : Collections.emptyList());

            return "redirect:/profile";
        } catch (HttpClientErrorException.Unauthorized | UnauthorizedException e) {
            return "redirect:/login";
        } catch (Exception e) {
            model.addAttribute("error", "Ошибка создания поста: " + e.getMessage());
            return "error";
        }
    }

    @GetMapping("/post/edit/{postId}")
    public String editPostForm(@PathVariable Integer postId,
                               Model model) {
        PostDto post = postClient.getOne(postId);
        model.addAttribute("post", post);
        return "create";
    }

    @PostMapping(value = "/post/edit/{postId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public String editPost(@PathVariable Integer postId,
                           @RequestParam String title,
                           @RequestParam(required = false) String text,
                           @RequestParam(required = false) MultipartFile[] newImages,
                           HttpSession session,
                           Model model) {
        try {
            String token = getSessionToken(session);

            PostDto postDto = new PostDto();
            postDto.setId(postId);
            postDto.setTitle(title);
            postDto.setText(text);

            postClient.updatePostWithImages(postDto, newImages != null ? List.of(newImages) : Collections.emptyList(), token);

            return "redirect:/profile";
        } catch (Exception e) {
            model.addAttribute("error", "Ошибка редактирования поста: " + e.getMessage());
            return "error";
        }
    }

    @PostMapping("/posts/{id}/delete")
    public String deletePost(@PathVariable Integer id, HttpSession session, Model model) {
        try {
            String token = getSessionToken(session);
            postClient.deletePost(id);
            return "redirect:/profile";
        }
        catch (HttpClientErrorException.Unauthorized | UnauthorizedException e) {
            return "redirect:/login";
        }
        catch (Exception e) {
            model.addAttribute("error", "Ошибка при удалении поста: " + e.getMessage());
            return "error";
        }
    }

    private String getSessionToken(HttpSession session) {
        String token = (String) session.getAttribute("token");
        if (token == null) {
            throw new UnauthorizedException("Требуется авторизация");
        }
        return token;
    }

    @PostMapping("/subscribe/{userId}")
    public String subscribe(@PathVariable Integer userId, HttpSession session) {
        try {
            String token = getSessionToken(session);
            subscriptionClient.subscribe(userId, token);
            return "redirect:/user/" + userId;
        } catch (HttpClientErrorException.Unauthorized | UnauthorizedException e) {
            return "redirect:/login";
        } catch (Exception e) {
            return "redirect:/user/" + userId + "?error=sub";
        }
    }

    @PostMapping("/unsubscribe/{userId}")
    public String unsubscribe(@PathVariable Integer userId, HttpSession session) {
        try {
            String token = getSessionToken(session);
            subscriptionClient.unsubscribe(userId, token);
            return "redirect:/user/" + userId;
        } catch (HttpClientErrorException.Unauthorized | UnauthorizedException e) {
            return "redirect:/login";
        } catch (Exception e) {
            return "redirect:/user/" + userId + "?error=unsub";
        }
    }
}

