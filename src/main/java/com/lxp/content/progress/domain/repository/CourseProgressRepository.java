package com.lxp.content.progress.domain.repository;

import com.lxp.content.progress.domain.model.CourseProgress;
import com.lxp.content.progress.domain.model.vo.CourseId;
import com.lxp.content.progress.domain.model.vo.UserId;

import java.util.List;
import java.util.Optional;

/**
 * 강좌 진행 도메인 인터페이스
 */
public interface CourseProgressRepository {

    /**
     * 사용자 ID로 강좌 진행 리스트 조회
     * @param id 사용자 ID
     * @return 강좌 진행 리스트
     */
    List<CourseProgress> findByUserId(UserId id);

    /**
     * 도메인 모델을 이용한 저장
     * @param domain 강좌 진행률 도메인 모델
     */
    void save(CourseProgress domain);

    /**
     * 사용자 ID, 강좌 ID로 강좌 진행 조회
     * @param userId 사용자 ID
     * @param courseId 강좌 ID
     * @return 강좌 진행(Optional)
     */
    Optional<CourseProgress> findByUserIdAndCourseId(UserId userId, CourseId courseId);

}
