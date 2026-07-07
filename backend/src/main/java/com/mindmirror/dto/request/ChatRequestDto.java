package com.mindmirror.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record ChatRequestDto(
        @NotBlank @Size(max = 500) String message,
        @Size(max = 5) String lang
) { }
