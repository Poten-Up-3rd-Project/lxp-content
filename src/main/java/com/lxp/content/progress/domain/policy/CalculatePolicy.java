package com.lxp.content.progress.domain.policy;

import com.lxp.content.progress.domain.model.LectureProgress;
import com.lxp.content.progress.domain.model.vo.CourseCompletionResult;

import java.util.List;

public interface CalculatePolicy {

    /**
     * 강좌 진행률 계산 정책
     * @param lectureProgresses 강의 진행률 리스트
     * @return 강좌 진행률 결과 DTO
     */
    CourseCompletionResult calculateCourseProgress(List<LectureProgress> lectureProgresses);

}
