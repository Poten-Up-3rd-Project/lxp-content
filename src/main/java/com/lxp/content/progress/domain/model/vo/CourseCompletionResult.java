package com.lxp.content.progress.domain.model.vo;

/**
 * 강좌 완료 진행도, 완료 여부 DTO
 * @param totalProgress
 * @param isCompleted
 */
public record CourseCompletionResult (
        float totalProgress,
        boolean isCompleted
) {
}
