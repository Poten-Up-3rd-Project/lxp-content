package com.lxp.content.progress.application.port.out;

import com.lxp.content.progress.domain.model.CourseProgress;

import java.util.List;
import java.util.Optional;

/**
 * 강좌 진행률 조회 포트
 */
public interface LoadCourseProgressPort {

    /**
     * 사용자 ID와 강좌 ID로 강좌 진행률 조회
     * @param userId 사용자 ID
     * @param courseId 강좌 ID
     * @return 조회된 강좌 진행률
     */
    Optional<CourseProgress> findByUserIdAndCourseId(String userId, String courseId);

    /**
     * 사용자 ID로 강좌 진행률 리스트 조회
     * @param userId 사용자 ID
     * @return 조회된 강좌 진행률 리스트
     */
    List<CourseProgress> findByUserId(String userId);

}
