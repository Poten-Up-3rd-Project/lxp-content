package com.lxp.content.progress.domain.policy;

import com.lxp.content.progress.domain.model.CourseProgress;
import com.lxp.content.progress.domain.model.LectureProgress;
import org.springframework.stereotype.Component;

import java.util.Objects;

/**
 * 완료 기본 정책
 * - 강좌 완료도가 100이 되면 완료로 처리
 * - 강의 마지막 재생시간이 총 재생시간과 같으면 완료 처리
 */
@Component
public class DefaultCompletionPolicy implements CompletionPolicy {

    private static final float COMPLETION_THRESHOLD = 100.0f;

    @Override
    public boolean isSatisfiedBy(CourseProgress courseProgress) {
        return courseProgress.totalProgress() >= COMPLETION_THRESHOLD;
    }

    @Override
    public boolean isSatisfiedBy(LectureProgress lectureProgress) {
        return Objects.equals(
                lectureProgress.lastPlayedTimeInSeconds(),
                lectureProgress.totalDurationInSeconds()
        );
    }
}
