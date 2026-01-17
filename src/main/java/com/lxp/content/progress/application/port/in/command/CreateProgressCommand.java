package com.lxp.content.progress.application.port.in.command;

/**
 * 강좌 진행률 생성 커맨드
 * @param userId 사용자 ID
 * @param courseId 강좌 ID
 */
public record CreateProgressCommand(
    String userId,
    String courseId
) {
}
