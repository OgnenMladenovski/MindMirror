package com.mindmirror.service;

import com.mindmirror.dto.request.UpdateProfileRequest;
import com.mindmirror.dto.response.UserResponse;
import com.mindmirror.entity.User;
import com.mindmirror.exception.NotFoundException;
import com.mindmirror.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Transactional(readOnly = true)
    public User require(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found"));
    }

    @Transactional(readOnly = true)
    public UserResponse getProfile(Long userId) {
        return UserResponse.from(require(userId));
    }

    @Transactional
    public UserResponse updateProfile(Long userId, UpdateProfileRequest req) {
        User user = require(userId);
        if (req.fullName() != null) user.setFullName(req.fullName());
        if (req.gender() != null) user.setGender(req.gender());
        if (req.locale() != null) user.setLocale("mk".equalsIgnoreCase(req.locale()) ? "mk" : "en");
        if (req.dateOfBirth() != null) {
            user.setDateOfBirth(req.dateOfBirth());
            user.setAgeGroup(AuthService.ageGroupFor(req.dateOfBirth()));
        }
        return UserResponse.from(userRepository.save(user));
    }
}
