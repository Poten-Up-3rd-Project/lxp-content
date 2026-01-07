package com.lxp.content.progress.domain.service;

import com.lxp.content.progress.domain.model.CourseProgress;
import com.lxp.content.progress.domain.model.vo.CourseCompletionResult;
import com.lxp.content.progress.domain.model.vo.LectureId;
import com.lxp.content.progress.domain.policy.CalculatePolicy;
import com.lxp.content.progress.domain.policy.CompletionPolicy;
import org.springframework.stereotype.Component;

/**
 * 강좌 진행도 도메인 서비스
 */
@Component
public class CourseProgressDomainService {

    private final CalculatePolicy calculatePolicy;
    private final CompletionPolicy completionPolicy;

    public CourseProgressDomainService(CalculatePolicy calculatePolicy, CompletionPolicy completionPolicy) {
        this.calculatePolicy = calculatePolicy;
        this.completionPolicy = completionPolicy;
    }

    /**
     * 강좌 진행도 업데이트
     * @param courseProgress 강좌 진행
     * @param lectureId 강의 ID
     * @param lastPlayedTime 마지막 재생 시간(s)
     */
    public void updateProcess(CourseProgress courseProgress, LectureId lectureId, Integer lastPlayedTime) {
        courseProgress.updateLectureProgress(lectureId, lastPlayedTime, completionPolicy);

        CourseCompletionResult result = calculatePolicy.calculateCourseProgress(courseProgress.lectureProgresses());

        courseProgress.reflectCalculation(result);
    }

}
