package com.lxp.content.course.infra.persistence.read.entity;

public record Tag(
        Long id,
        String content,
        String color,
        String variant
) {
}
