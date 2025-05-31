package com.example.casdoor.controller;

import com.example.casdoor.service.JwtDecoder;
import io.jsonwebtoken.Claims;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
public class UserInfoController {

    @Autowired
    private JwtDecoder decoder;

    @GetMapping("/userinfo")
    public ResponseEntity<?> getUser(@CookieValue("access_token") String token) {
        try {
            Claims claims = decoder.decodeToken(token);
            Map<String, Object> map = new HashMap<>();
            map.put("userId", claims.getSubject());
            map.put("username", claims.get("name"));
            return ResponseEntity.ok(map);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid or expired token");
        }
    }
}

