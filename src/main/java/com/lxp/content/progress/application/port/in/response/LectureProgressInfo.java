package com.lxp.content.progress.application.port.in.response;

/**
 * 강의 진행률 정보 DTO
 * @param lectureId 강의 ID
 * @param lastPlayedTime 마지막 재생 시간(초)
 * @param totalDuration 강의 총 길이(초)
 * @param isCompleted 완료 여부
 */
public record LectureProgressInfo(
        String lectureId,
        Integer lastPlayedTime,
        Integer totalDuration,
        boolean isCompleted
) {}
