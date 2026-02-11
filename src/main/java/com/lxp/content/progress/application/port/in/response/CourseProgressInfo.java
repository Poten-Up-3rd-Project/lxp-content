package com.lxp.content.progress.application.port.in.response;

/**
 * 강좌 진행률 정보 DTO
 * @param courseId 강좌 ID
 * @param totalProgress 총 진행률
 * @param isCompleted 완료 여부
 */
public record CourseProgressInfo(
        String courseId,
        float totalProgress,
        boolean isCompleted
) {}
