package com.lxp.content.progress.infrastructure.adapter.course;

import com.lxp.content.course.application.port.provider.query.CourseDetailQuery;
import com.lxp.content.course.application.port.provider.usecase.query.CourseDetailUseCase;
import com.lxp.content.progress.application.port.out.CourseInfoLoadPort;
import com.lxp.content.progress.application.port.out.dto.CourseLectureInfo;
import org.springframework.stereotype.Component;

/**
 * 강좌 서비스 어댑터
 */
@Component
public class CourseServiceAdapter implements CourseInfoLoadPort {

    private final CourseDetailUseCase courseDetailUseCase;

    public CourseServiceAdapter(CourseDetailUseCase courseDetailUseCase) {
        this.courseDetailUseCase = courseDetailUseCase;
    }

    /**
     * 강좌 ID로 강좌의 모든 강의 정보 조회
     * @param courseId 강좌 ID
     * @return 조회된 강좌의 강의 정보 리스트
     */
    @Override
    public CourseLectureInfo loadLecturesByCourseId(String courseId) {
        var courseDetail = courseDetailUseCase.execute(new CourseDetailQuery(courseId));

        return new CourseLectureInfo(
                courseDetail.courseId(),
                courseDetail.sections().stream()
                        .flatMap(section -> section.lectures().stream())
                        .map(lecture -> new CourseLectureInfo.LectureInfo(
                                lecture.lectureId(),
                                lecture.duration().intValue()
                        )).toList()
        );
    }
}
