package com.example.client_app.controller;

import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.annotation.RegisteredOAuth2AuthorizedClient;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.context.SecurityContextHolder;

@Controller
public class HomeController {



    @GetMapping("/")
    public String index() {
        return "redirect:/home";
    }

    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @GetMapping("/home")
    public String home(@RegisteredOAuth2AuthorizedClient("demo-client") OAuth2AuthorizedClient authorizedClient,
                       @AuthenticationPrincipal OAuth2User oauth2User,
                       Model model) {

        String accessToken = authorizedClient.getAccessToken().getTokenValue();
        assert authorizedClient.getRefreshToken() != null;
        String refreshToken = authorizedClient.getRefreshToken().getTokenValue();
        String email = oauth2User.getAttribute("email");
        String username = oauth2User.getAttribute("username");
        String role = oauth2User.getAttribute("role");

        // if roles is array, extract it from ID Token
//        Object rolesClaim = oauth2User.getAttribute("roles");
//
//        if (rolesClaim instanceof java.util.List<?>) {
//            List<?> roles = (List<?>) rolesClaim;
//            if (!roles.isEmpty()) {
//                role =  roles.getFirst().toString();
//            }
//        }

        model.addAttribute("accessToken", accessToken);
        model.addAttribute("refreshToken", refreshToken);
        model.addAttribute("email", email);
        model.addAttribute("username", username);
        model.addAttribute("role", role);
        model.addAttribute("isAdmin", role.equals("ADMIN"));
        model.addAttribute("isUser", role.equals("USER"));

        return "home";
    }
}


