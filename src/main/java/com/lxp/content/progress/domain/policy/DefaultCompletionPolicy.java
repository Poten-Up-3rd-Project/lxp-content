package com.lxp.content.progress.domain.policy;

import com.lxp.content.progress.domain.model.LectureProgress;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 강좌 완료 기본 정책
 * - 강좌 완료도가 100이 되면 종료로 처리
 */
@Component
public class DefaultCompletionPolicy implements CourseCompletionPolicy{

    private static final float COMPLETION_THRESHOLD = 100.0f;

    @Override
    public boolean isSatisfiedBy(float totalProgress, List<LectureProgress> lectureProgresses) {
        return totalProgress >= COMPLETION_THRESHOLD;
    }
}
