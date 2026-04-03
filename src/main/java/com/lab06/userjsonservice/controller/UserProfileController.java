package com.lab06.userjsonservice.controller;

import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.lab06.userjsonservice.dto.UserProfileRequest;
import com.lab06.userjsonservice.entity.UserProfile;
import com.lab06.userjsonservice.exception.UnauthorizedException;
import com.lab06.userjsonservice.service.ProfileService;
import com.lab06.userjsonservice.service.SoapAuthClient;

import jakarta.validation.Valid;

@Validated
@RestController
@RequestMapping("/users")
public class UserProfileController {

    private final ProfileService profileService;
    private final SoapAuthClient soapAuthClient;

    public UserProfileController(ProfileService profileService, SoapAuthClient soapAuthClient) {
        this.profileService = profileService;
        this.soapAuthClient = soapAuthClient;
    }

    @PostMapping
    public ResponseEntity<?> createProfile(@RequestHeader("Authorization") String authorization,
                                           @Valid @RequestBody UserProfileRequest request) {
        Long tokenUserId = getUserIdFromAuthorization(authorization);
        if (!tokenUserId.equals(request.getUserId())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Map.of("message", "Ta zuvhun uuriin profil-iig uusgeh bolomjtoi."));
        }

        try {
            UserProfile profile = profileService.createProfile(request);
            return ResponseEntity.status(HttpStatus.CREATED).body(profile);
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().body(Map.of("message", ex.getMessage()));
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getProfileById(@RequestHeader("Authorization") String authorization,
                                            @PathVariable Long id) {
        Long tokenUserId = getUserIdFromAuthorization(authorization);
        return profileService.getProfileById(id)
                .map(profile -> {
                    if (!profile.getUserId().equals(tokenUserId)) {
                        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                                .body(Map.of("message", "Ene profil tand hamaarahgui."));
                    }
                    return ResponseEntity.ok(profile);
                })
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.of("message", "Profil oldsongui.")));
    }

    @GetMapping("/by-user")
    public ResponseEntity<?> getProfileByUserId(@RequestHeader("Authorization") String authorization,
                                                @RequestParam Long userId) {
        Long tokenUserId = getUserIdFromAuthorization(authorization);
        if (!tokenUserId.equals(userId)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Map.of("message", "Ene profil tand hamaarahgui."));
        }

        UserProfile profile = profileService.getProfileByUserId(userId).orElse(null);
        if (profile == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("message", "Profil oldsongui."));
        }

        return ResponseEntity.ok(profile);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateProfile(@RequestHeader("Authorization") String authorization,
                                           @PathVariable Long id,
                                           @Valid @RequestBody UserProfileRequest request) {
        Long tokenUserId = getUserIdFromAuthorization(authorization);
        if (!tokenUserId.equals(request.getUserId())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Map.of("message", "Ta zuvhun uuriin profil-iig zasah bolomjtoi."));
        }

        try {
            UserProfile updatedProfile = profileService.updateProfile(id, request);
            return ResponseEntity.ok(updatedProfile);
        } catch (IllegalArgumentException ex) {
            HttpStatus status = "Profil oldsongui.".equals(ex.getMessage()) ? HttpStatus.NOT_FOUND : HttpStatus.BAD_REQUEST;
            return ResponseEntity.status(status).body(Map.of("message", ex.getMessage()));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteProfile(@RequestHeader("Authorization") String authorization,
                                           @PathVariable Long id) {
        Long tokenUserId = getUserIdFromAuthorization(authorization);

        UserProfile profile = profileService.getProfileById(id).orElse(null);
        if (profile == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("message", "Profil oldsongui."));
        }

        if (!profile.getUserId().equals(tokenUserId)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Map.of("message", "Ene profil tand hamaarahgui."));
        }

        profileService.deleteProfile(id);
        return ResponseEntity.ok(Map.of("message", "Profil amjilttai ustgagdlaa."));
    }

    private Long getUserIdFromAuthorization(String authorization) {
        if (authorization == null || !authorization.startsWith("Bearer ")) {
            throw new UnauthorizedException("Authorization header baihgui baina.");
        }

        String token = authorization.substring(7);
        boolean valid = soapAuthClient.validateToken(token);
        if (!valid) {
            throw new UnauthorizedException("Token buruu esvel huchingui baina.");
        }

        Long userId = soapAuthClient.getUserIdByToken(token);
        if (userId == null) {
            throw new UnauthorizedException("Ene token-toi hereglegch oldsongui.");
        }

        return userId;
    }
}
