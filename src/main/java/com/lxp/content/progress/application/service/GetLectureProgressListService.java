package com.lxp.content.progress.application.service;

import com.lxp.content.progress.application.mapper.ProgressWebMapper;
import com.lxp.content.progress.application.port.in.query.GetLectureProgressListQuery;
import com.lxp.content.progress.application.port.in.response.LectureProgressInfo;
import com.lxp.content.progress.application.port.in.usecase.GetLectureProgressListUseCase;
import com.lxp.content.progress.application.port.out.LoadCourseProgressPort;
import com.lxp.content.progress.exception.ProgressDomainException;
import com.lxp.content.progress.exception.ProgressErrorCode;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 강의 진행률 리스트 조회 서비스
 */
@Service
@Transactional(readOnly = true)
public class GetLectureProgressListService implements GetLectureProgressListUseCase {

    private final LoadCourseProgressPort loadCourseProgressPort;

    private final ProgressWebMapper mapper;

    public GetLectureProgressListService(LoadCourseProgressPort loadCourseProgressPort, ProgressWebMapper mapper) {
        this.loadCourseProgressPort = loadCourseProgressPort;
        this.mapper = mapper;
    }

    @Override
    public List<LectureProgressInfo> execute(GetLectureProgressListQuery query) {
        return mapper.toResponseListAsLectureProgressInfo(
            loadCourseProgressPort.findByUserIdAndCourseId(query.userId(), query.courseId())
                .orElseThrow(() -> new ProgressDomainException(ProgressErrorCode.COURSE_PROGRESS_NOT_FOUND))
                .lectureProgresses()
        );
    }

}
