package com.lxp.content.resource.infrastructure.web.dto;

import jakarta.validation.constraints.NotBlank;

public record ConfirmUploadRequest(@NotBlank String key) {
}
