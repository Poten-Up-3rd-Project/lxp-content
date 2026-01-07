package com.lxp.content.course.application.port.provider.view;

public record TagInfoView(
        Long id,
        String content,
        String color,
        String variant
) {
}