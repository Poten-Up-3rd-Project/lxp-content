package com.lxp.content.resource.domain.model.vo;

import com.lxp.content.resource.domain.support.ResourceGuard;

import java.util.UUID;

public record ResourceId(UUID value) {

    public ResourceId {
        ResourceGuard.requireNonNull(value, "resourceId는 null일 수 없습니다.");
    }

    public static ResourceId create() {
        return new ResourceId(UUID.randomUUID());
    }

    public static ResourceId of(UUID value) {
        return new ResourceId(value);
    }

    public static ResourceId of(String value) {
        return new ResourceId(UUID.fromString(value));
    }

    public String asString() {
        return this.value.toString();
    }

}
