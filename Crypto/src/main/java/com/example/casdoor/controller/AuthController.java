package com.example.casdoor.controller;

import com.example.casdoor.config.OpenIdConnectProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Controller;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.client.RestTemplate;

import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.Map;

@Controller
public class AuthController {

    @Autowired
    private OpenIdConnectProperties properties;

    @GetMapping("/login")
    public void login(HttpServletResponse response) throws IOException {
        String url = properties.getEndpoint() + "/login/oauth/authorize?" +
                "client_id=" + properties.getClientId() +
                "&response_type=code" +
                "&redirect_uri=" + properties.getRedirectUri() +
                "&scope=openid%20profile%20email";
        response.sendRedirect(url);
    }

    @GetMapping("/callback")
    public void callback(@RequestParam String code, HttpServletResponse response) throws IOException {
        RestTemplate rest = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> form = new LinkedMultiValueMap<>();
        form.add("grant_type", "authorization_code");
        form.add("code", code);
        form.add("client_id", properties.getClientId());
        form.add("client_secret", properties.getClientSecret());
        form.add("redirect_uri", properties.getRedirectUri());

        HttpEntity<?> req = new HttpEntity<>(form, headers);
        ResponseEntity<Map> res = rest.postForEntity(
                properties.getEndpoint() + "/api/login/oauth/access_token",
                req,
                Map.class
        );

        String accessToken = (String) res.getBody().get("access_token");
        response.addHeader("Set-Cookie", "access_token=" + accessToken + "; Path=/; HttpOnly");
        response.sendRedirect("/");
    }
}

