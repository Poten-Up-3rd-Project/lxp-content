package com.lxp.content.progress.domain.repository;

import com.lxp.content.progress.domain.model.CourseProgress;
import com.lxp.content.progress.domain.model.vo.CourseId;
import com.lxp.content.progress.domain.model.vo.LectureId;

import java.util.Optional;

/**
 * 강좌 진행 도메인 인터페이스
 */
public interface CourseProgressRepository {

    /**
     * 강좌 진행률 업데이트
     * @param id 강좌 ID
     * @param lastPlayedTimeInSeconds 마지막 재생 시간(초)
     */
    void updateCourseProgress(LectureId id, Integer lastPlayedTimeInSeconds);

    /**
     * 강좌 ID로 강좌 진행 조회
     * @param id 강의 ID
     * @return 강의 진행(Optional)
     */
    Optional<CourseProgress> findByCourseId(CourseId id);

}
