package com.lxp.content.progress.application.service;

import com.lxp.content.progress.application.port.in.command.CreateProgressCommand;
import com.lxp.content.progress.application.port.in.response.CourseProgressInfo;
import com.lxp.content.progress.application.port.in.usecase.CreateCourseProgressUseCase;
import com.lxp.content.progress.application.port.out.CourseInfoLoadPort;
import com.lxp.content.progress.application.port.out.LoadCourseProgressPort;
import com.lxp.content.progress.application.port.out.SaveCourseProgressPort;
import com.lxp.content.progress.application.port.out.dto.CourseLectureInfo;
import com.lxp.content.progress.domain.model.CourseProgress;
import com.lxp.content.progress.domain.model.LectureProgress;
import com.lxp.content.progress.domain.model.vo.CourseId;
import com.lxp.content.progress.domain.model.vo.LectureId;
import com.lxp.content.progress.domain.model.vo.UserId;
import com.lxp.content.progress.exception.ProgressDomainException;
import com.lxp.content.progress.exception.ProgressErrorCode;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 강좌 진행률 생성 서비스
 */
@Service
@Transactional
public class CreateCourseProgressService implements CreateCourseProgressUseCase {

    private final CourseInfoLoadPort courseInfoLoadPort;
    private final SaveCourseProgressPort saveCourseProgressPort;
    private final LoadCourseProgressPort loadCourseProgressPort;

    public CreateCourseProgressService(CourseInfoLoadPort courseInfoLoadPort, SaveCourseProgressPort saveCourseProgressPort, LoadCourseProgressPort loadCourseProgressPort) {
        this.courseInfoLoadPort = courseInfoLoadPort;
        this.saveCourseProgressPort = saveCourseProgressPort;
        this.loadCourseProgressPort = loadCourseProgressPort;
    }

    /**
     * 강좌 진행률 생성
     * @param command 생성 커맨드
     */
    @Override
    public Void execute(CreateProgressCommand command) {
        if(loadCourseProgressPort.findByUserIdAndCourseId(command.userId(), command.courseId()).isPresent()) {
            return null;
        }

        try {
            CourseLectureInfo courseInfo = courseInfoLoadPort
                    .loadLecturesByCourseId(command.courseId());

            UserId userId = new UserId(command.userId());

            List<LectureProgress> lectureProgresses = courseInfo.lectures().stream()
                    .map(lecture -> LectureProgress.create(
                            userId,
                            new LectureId(lecture.lectureId()),
                            lecture.durationInMinutes()
                    )).toList();

            CourseProgress courseProgress = CourseProgress.create(
                    userId,
                    new CourseId(command.courseId()),
                    lectureProgresses
            );

            saveCourseProgressPort.save(courseProgress);
        } catch (Exception e) {
            throw new ProgressDomainException(ProgressErrorCode.COURSE_NOT_FOUND);
        }

        return null;
    }
}
