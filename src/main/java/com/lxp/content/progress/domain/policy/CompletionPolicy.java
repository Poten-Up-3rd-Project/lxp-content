package com.lxp.content.progress.domain.policy;

import com.lxp.content.progress.domain.model.CourseProgress;
import com.lxp.content.progress.domain.model.LectureProgress;

/**
 * 강좌, 강의 완료 판정 정책 인터페이스
 */
public interface CompletionPolicy {

    /**
     * 강좌 완료 여부 정책
     * @param courseProgress 강좌 진행 도메인
     * @return 강의 완료 여부
     */
    boolean isSatisfiedBy(CourseProgress courseProgress);

    /**
     * 강의 완료 여부 정책
     * @param lectureProgress 강의 진행 도메인
     * @return 강의 완료 여부
     */
    boolean isSatisfiedBy(LectureProgress lectureProgress);
}
