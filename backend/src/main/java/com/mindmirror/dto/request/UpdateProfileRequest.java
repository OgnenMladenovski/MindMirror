package com.mindmirror.dto.request;

import com.mindmirror.entity.enums.Gender;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

public record UpdateProfileRequest(
        @Size(max = 160) String fullName,
        LocalDate dateOfBirth,
        Gender gender,
        @Size(max = 5) String locale
) { }
