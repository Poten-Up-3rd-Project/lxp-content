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
            return new CourseCompletionResult(0, false);
        }

        long completedCount = lectureProgresses.stream()
                .filter(LectureProgress::completed)
                .count();

        float progress = ((float) completedCount / lectureProgresses.size()) * 100;
        progress = BigDecimal.valueOf(progress).setScale(0, RoundingMode.FLOOR).floatValue();

        return new CourseCompletionResult(progress, progress >= 100.0f);
    }

}
