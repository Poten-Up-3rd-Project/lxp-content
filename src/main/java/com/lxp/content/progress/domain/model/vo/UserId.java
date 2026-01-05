package com.lxp.content.progress.domain.model.vo;

/**
 * 사용자 ID(임시로 여기 넣어둠)
 * @param value 사용자 ID 값
 */
public record UserId(String value) {
    public UserId {
        if (value != null && value.isEmpty()) {
            throw new IllegalArgumentException("UserId must be not empty");
        }
    }
}
