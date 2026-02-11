package com.lxp.content.resource.infrastructure.web.dto;

import jakarta.validation.constraints.NotBlank;

public record UploadUrlRequest(@NotBlank String uploadType, @NotBlank String contentType) {
}
