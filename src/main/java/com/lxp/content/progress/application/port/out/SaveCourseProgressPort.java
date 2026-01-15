package com.lxp.content.progress.application.port.out;

import com.lxp.content.progress.domain.model.CourseProgress;

/**
 * 강좌 진행률 저장 포트
 */
public interface SaveCourseProgressPort {

    /**
     * 강좌 진행률 저장
     * @param domain 저장할 강좌 진행률 도메인 객체
     */
    void save(CourseProgress domain);

}
