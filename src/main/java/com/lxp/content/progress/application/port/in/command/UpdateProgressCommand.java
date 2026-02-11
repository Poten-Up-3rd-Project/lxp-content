package com.lxp.content.progress.application.port.in.command;

/**
 * 강의 진행 시간 업데이트 커맨드
 * @param userId 사용자 ID
 * @param courseId 강좌 ID
 * @param lectureId 강의 ID
 * @param lastPlayedTime 마지막 재생 시간
 */
public record UpdateProgressCommand(
        String userId,
        String courseId,
        String lectureId,
        Integer lastPlayedTime
) {
}
