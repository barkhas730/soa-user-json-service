package com.lab06.userjsonservice.controller;

import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.lab06.userjsonservice.dto.AuthRequest;
import com.lab06.userjsonservice.service.SoapAuthClient;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final SoapAuthClient soapAuthClient;

    public AuthController(SoapAuthClient soapAuthClient) {
        this.soapAuthClient = soapAuthClient;
    }

    @PostMapping("/register")
    public ResponseEntity<Map<String, Object>> register(@Valid @RequestBody AuthRequest request) {
        String message = soapAuthClient.registerUser(request.getUsername(), request.getPassword());
        return ResponseEntity.ok(Map.of("message", message));
    }

    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> login(@Valid @RequestBody AuthRequest request) {
        SoapAuthClient.LoginResult result = soapAuthClient.loginUser(request.getUsername(), request.getPassword());
        if (result.token() != null && result.userId() != null) {
            return ResponseEntity.ok(Map.of(
                    "message", result.message(),
                    "token", result.token(),
                    "userId", result.userId()
            ));
        }

        return ResponseEntity.ok(Map.of("message", result.message()));
    }

    @GetMapping("/validate")
    public ResponseEntity<Map<String, Object>> validate(@RequestHeader("Authorization") String authorization) {
        if (authorization == null || !authorization.startsWith("Bearer ")) {
            throw new IllegalArgumentException("Authorization header baihgui baina.");
        }

        String token = authorization.substring(7);
        boolean valid = soapAuthClient.validateToken(token);
        return ResponseEntity.ok(Map.of("valid", valid));
    }
}
