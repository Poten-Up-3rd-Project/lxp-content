package com.lxp.content.progress.domain.repository;

import com.lxp.content.progress.domain.model.LectureProgress;
import com.lxp.content.progress.domain.model.vo.LectureId;

import java.util.List;
import java.util.Optional;

/**
 * 강의 진행 도메인 인터페이스
 */
public interface LectureProgressRepository {

    /**
     * 강의 진행 마지막 플레이 시간 저장
     * @param lastPlayedTimeInSeconds 마지막 재생 시간(초)
     * @return LectureProgress 강의 진행 도메인 객체
     */
    Optional<LectureProgress> updateLastPlayedTime(LectureId id, Integer lastPlayedTimeInSeconds);

    /**
     * 강의 진행 목록 조회
     * @return List<LectureProgress> 강의 진행 목록
     */
    List<LectureProgress> findAll();

}
