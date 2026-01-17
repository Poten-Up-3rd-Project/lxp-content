package com.lxp.content.progress.application.port.out;

import com.lxp.content.progress.application.port.out.dto.CourseLectureInfo;

/**
 * 강좌 정보 조회 포트
 */
public interface CourseInfoLoadPort {

    CourseLectureInfo loadLecturesByCourseId(String courseId);

}
