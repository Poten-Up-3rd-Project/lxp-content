package com.lxp.content.progress.application.service;

import com.lxp.content.progress.application.port.in.command.UpdateProgressCommand;
import com.lxp.content.progress.application.port.in.usecase.UpdateProgressUseCase;
import com.lxp.content.progress.application.port.out.LoadCourseProgressPort;
import com.lxp.content.progress.application.port.out.SaveCourseProgressPort;
import com.lxp.content.progress.domain.model.CourseProgress;
import com.lxp.content.progress.domain.model.vo.LectureId;
import com.lxp.content.progress.domain.service.CourseProgressDomainService;
import com.lxp.content.progress.exception.ProgressDomainException;
import com.lxp.content.progress.exception.ProgressErrorCode;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class CourseProgressUpdateService implements UpdateProgressUseCase {

    private final LoadCourseProgressPort loadPort;
    private final SaveCourseProgressPort savePort;
    private final CourseProgressDomainService courseProgressDomainService;

    public CourseProgressUpdateService(
            LoadCourseProgressPort loadPort,
            SaveCourseProgressPort savePort,
            CourseProgressDomainService courseProgressDomainService
    ) {
        this.loadPort = loadPort;
        this.savePort = savePort;
        this.courseProgressDomainService = courseProgressDomainService;
    }

    @Override
    public Void execute(UpdateProgressCommand command) {
        CourseProgress progress = loadPort.findByUserIdAndCourseId(command.userId(), command.courseId())
                .orElseThrow(() -> new ProgressDomainException(ProgressErrorCode.COURSE_PROGRESS_NOT_FOUND));

        courseProgressDomainService.updateProcess(
                progress,
                new LectureId(command.lectureId()),
                command.lastPlayedTime()
        );

        savePort.save(progress);

        return null;
    }

}
