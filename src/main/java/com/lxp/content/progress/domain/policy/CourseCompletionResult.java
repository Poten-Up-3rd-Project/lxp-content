package com.lxp.content.progress.domain.policy;

/**
 * 강좌 완료 진행도, 완료 여부 DTO
 * @param totalProgress
 * @param isCompleted
 */
public record CourseCompletionResult (
        float totalProgress,
        boolean isCompleted
) {

    /**
     * 수강 전 강의 진행률 결과
     * @return 수강 전 강의 진행률 레코드
     */
    public static CourseCompletionResult withZeroProgress() {
        return new CourseCompletionResult(0.0f, false);
    }
}
