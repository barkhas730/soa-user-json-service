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
            throw new IllegalArgumentException("Энэ хэрэглэгч аль хэдийн профайлтай байна.");
        }

        UserProfile profile = new UserProfile();
        profile.setUserId(request.getUserId());
        profile.setName(request.getName());
        profile.setEmail(request.getEmail());
        profile.setBio(request.getBio());
        profile.setPhone(request.getPhone());
        profile.setImageUrl(request.getImageUrl());
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
                .orElseThrow(() -> new IllegalArgumentException("Профайл олдсонгүй."));

        if (!profile.getUserId().equals(request.getUserId())) {
            throw new IllegalArgumentException("Энэ профайл танд хамаарахгүй.");
        }

        profile.setName(request.getName());
        profile.setEmail(request.getEmail());
        profile.setBio(request.getBio());
        profile.setPhone(request.getPhone());
        profile.setImageUrl(request.getImageUrl());
        return userProfileRepository.save(profile);
    }

    public void deleteProfile(Long id) {
        if (!userProfileRepository.existsById(id)) {
            throw new IllegalArgumentException("Профайл олдсонгүй.");
        }
        userProfileRepository.deleteById(id);
    }
}
