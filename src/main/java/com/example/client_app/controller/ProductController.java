package com.example.client_app.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.annotation.RegisteredOAuth2AuthorizedClient;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class ProductController {

    @Autowired
    private RestTemplate restTemplate;

    private static final String RESOURCE_SERVER_URL = "http://localhost:8082/api/products";

    @GetMapping("/products")
    public String products(@RegisteredOAuth2AuthorizedClient OAuth2AuthorizedClient authorizedClient,
                           Model model) {
        try {
            String accessToken = authorizedClient.getAccessToken().getTokenValue();

            HttpHeaders headers = new HttpHeaders();
            headers.setBearerAuth(accessToken);
            HttpEntity<String> entity = new HttpEntity<>(headers);

            ResponseEntity<List> response = restTemplate.exchange(
                    RESOURCE_SERVER_URL,
                    HttpMethod.GET,
                    entity,
                    List.class
            );

            model.addAttribute("products", response.getBody());
        } catch (Exception e) {
            model.addAttribute("error", "Failed to fetch products: " + e.getMessage());
        }

        return "products";
    }

    @GetMapping("/create-product")
    public String createProductPage() {
        return "create-product";
    }

    @PostMapping("/create-product")
    public String createProduct(@RegisteredOAuth2AuthorizedClient OAuth2AuthorizedClient authorizedClient,
                                @RequestParam String name,
                                @RequestParam Double price,
                                Model model,
                                RedirectAttributes redirectAttributes) {
        try {
            String accessToken = authorizedClient.getAccessToken().getTokenValue();

            HttpHeaders headers = new HttpHeaders();
            headers.setBearerAuth(accessToken);
            headers.setContentType(MediaType.APPLICATION_JSON);

            Map<String, Object> productData = new HashMap<>();
            productData.put("name", name);
            productData.put("price", price);

            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(productData, headers);

            restTemplate.exchange(
                    RESOURCE_SERVER_URL,
                    HttpMethod.POST,
                    entity,
                    Void.class
            );

            redirectAttributes.addFlashAttribute("success", "Product created successfully!");
            return "redirect:/products";
        } catch (Exception e) {
            model.addAttribute("error", "Failed to create product: " + e.getMessage());
            return "create-product";
        }
    }
}
