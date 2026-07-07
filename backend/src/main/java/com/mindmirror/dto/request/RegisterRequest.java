package com.mindmirror.dto.request;

import com.mindmirror.entity.enums.Gender;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

public record RegisterRequest(
        @NotBlank @Size(min = 3, max = 60) String username,
        @NotBlank @Email String email,
        @NotBlank @Size(min = 8, max = 100) String password,
        @Size(max = 160) String fullName,
        LocalDate dateOfBirth,
        Gender gender,
        @Size(max = 5) String locale
) { }
