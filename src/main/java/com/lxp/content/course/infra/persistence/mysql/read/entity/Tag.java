package com.lxp.content.course.infra.persistence.mysql.read.entity;

public record Tag(
        Long id,
        String content,
        String color,
        String variant
) {
}
