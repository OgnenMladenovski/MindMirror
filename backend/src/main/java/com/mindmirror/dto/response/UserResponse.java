package com.mindmirror.dto.response;

import com.mindmirror.entity.User;

import java.time.LocalDate;

public record UserResponse(
        Long id,
        String username,
        String email,
        String fullName,
        LocalDate dateOfBirth,
        String gender,
        String role,
        int ageGroup,
        String locale
) {
    public static UserResponse from(User u) {
        return new UserResponse(
                u.getId(), u.getUsername(), u.getEmail(), u.getFullName(),
                u.getDateOfBirth(),
                u.getGender() == null ? null : u.getGender().name(),
                u.getRole().name(), u.getAgeGroup(), u.getLocale());
    }
}
