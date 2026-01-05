package com.lxp.content.progress.domain.policy;

import com.lxp.content.progress.domain.model.LectureProgress;

import java.util.List;

/**
 * 강좌 완료 판정 정책 인터페이스
 */
public interface CourseCompletionPolicy {

    /**
     * 강의 완료 여부 정책
     * @param totalProgress 강좌 진행률
     * @param lectureProgresses 강의 진행 리스트
     * @return 강의 완료 여부
     */
    boolean isSatisfiedBy(float totalProgress, List<LectureProgress> lectureProgresses);

}
