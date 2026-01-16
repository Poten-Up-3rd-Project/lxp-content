package com.lxp.content.progress.application.port.in.query;

/**
 * 사용자 ID 기반 수강 중인 특정 강좌 내의 강의별 진행률 리스트 조회 쿼리
 * @param userId 유저 ID
 * @param courseId 강좌 ID
 */
public record GetLectureProgressListQuery(
        String userId,
        String courseId
) {
}
