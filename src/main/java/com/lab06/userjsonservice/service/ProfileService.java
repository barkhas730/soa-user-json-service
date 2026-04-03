package com.lab06.userjsonservice.service;

import java.util.Optional;

import org.springframework.stereotype.Service;

import com.lab06.userjsonservice.dto.UserProfileRequest;
import com.lab06.userjsonservice.entity.UserProfile;
import com.lab06.userjsonservice.repository.UserProfileRepository;

@Service
public class ProfileService {

    private final UserProfileRepository userProfileRepository;

    public ProfileService(UserProfileRepository userProfileRepository) {
        this.userProfileRepository = userProfileRepository;
    }

    public UserProfile createProfile(UserProfileRequest request) {
        if (userProfileRepository.findByUserId(request.getUserId()).isPresent()) {
            throw new IllegalArgumentException("Ene hereglegch ali hediin profiltoi baina.");
        }

        UserProfile profile = new UserProfile();
        profile.setUserId(request.getUserId());
        profile.setName(normalizeRequired(request.getName(), "Ner oruulna uu."));
        profile.setEmail(normalizeRequired(request.getEmail(), "Email oruulna uu."));
        profile.setBio(normalizeOptional(request.getBio()));
        profile.setPhone(normalizeOptional(request.getPhone()));
        profile.setImageUrl(normalizeOptional(request.getImageUrl()));
        return userProfileRepository.save(profile);
    }

    public Optional<UserProfile> getProfileById(Long id) {
        return userProfileRepository.findById(id);
    }

    public Optional<UserProfile> getProfileByUserId(Long userId) {
        return userProfileRepository.findByUserId(userId);
    }

    public UserProfile updateProfile(Long id, UserProfileRequest request) {
        UserProfile profile = userProfileRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Profil oldsongui."));

        if (!profile.getUserId().equals(request.getUserId())) {
            throw new IllegalArgumentException("Ene profil tand hamaarahgui.");
        }

        profile.setName(normalizeRequired(request.getName(), "Ner oruulna uu."));
        profile.setEmail(normalizeRequired(request.getEmail(), "Email oruulna uu."));
        profile.setBio(normalizeOptional(request.getBio()));
        profile.setPhone(normalizeOptional(request.getPhone()));
        profile.setImageUrl(normalizeOptional(request.getImageUrl()));
        return userProfileRepository.save(profile);
    }

    public void deleteProfile(Long id) {
        if (!userProfileRepository.existsById(id)) {
            throw new IllegalArgumentException("Profil oldsongui.");
        }
        userProfileRepository.deleteById(id);
    }

    private String normalizeRequired(String value, String message) {
        String normalized = normalizeOptional(value);
        if (normalized == null || normalized.isBlank()) {
            throw new IllegalArgumentException(message);
        }
        return normalized;
    }

    private String normalizeOptional(String value) {
        if (value == null) {
            return null;
        }

        String normalized = value.trim();
        return normalized.isEmpty() ? null : normalized;
    }
}
