package com.lxp.content.progress.domain.policy;

import com.lxp.content.progress.domain.model.LectureProgress;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

/**
 * 진행률 계산 기본 정책
 * 강의 완료 갯수 / 전체 강의 갯수
 */
@Component
public class DefaultCalculatePolicy implements CalculatePolicy {

    @Override
    public CourseCompletionResult calculateCourseProgress(List<LectureProgress> lectureProgresses) {
        if(lectureProgresses.isEmpty()) {
            return CourseCompletionResult.withZeroProgress();
        }

        long completedCount = lectureProgresses.stream()
                .filter(LectureProgress::completed)
                .count();

        BigDecimal completed = BigDecimal.valueOf(completedCount);          // 완료 된 강의
        BigDecimal total = BigDecimal.valueOf(lectureProgresses.size());    // 전체
        BigDecimal progress = completed.divide(total, RoundingMode.HALF_UP)
                .multiply(BigDecimal.valueOf(100))
                .setScale(0, RoundingMode.FLOOR);

        float resultProgress = progress.floatValue();

        return new CourseCompletionResult(resultProgress, resultProgress >= 100.0f);
    }

}
