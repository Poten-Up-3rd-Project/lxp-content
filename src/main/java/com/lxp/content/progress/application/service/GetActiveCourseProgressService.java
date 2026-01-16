package com.lxp.content.progress.application.service;

import com.lxp.content.progress.application.mapper.ProgressWebMapper;
import com.lxp.content.progress.application.port.in.query.GetActiveCourseProgressQuery;
import com.lxp.content.progress.application.port.in.response.CourseProgressInfo;
import com.lxp.content.progress.application.port.in.usecase.GetActiveCourseProgressUseCase;
import com.lxp.content.progress.application.port.out.LoadCourseProgressPort;
import com.lxp.content.progress.domain.model.CourseProgress;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 사용자 ID 기반 수강중인 강좌 진행률 리스트 조회 서비스
 */
@Service
@Transactional(readOnly = true)
public class GetActiveCourseProgressService implements GetActiveCourseProgressUseCase {

    private final LoadCourseProgressPort loadCourseProgressPort;

    private final ProgressWebMapper mapper;

    public GetActiveCourseProgressService(LoadCourseProgressPort loadCourseProgressPort, ProgressWebMapper mapper) {
        this.mapper = mapper;
        this.loadCourseProgressPort = loadCourseProgressPort;
    }

    @Override
    public List<CourseProgressInfo> execute(GetActiveCourseProgressQuery query) {
        List<CourseProgress> courseProgressList = loadCourseProgressPort.findByUserId(query.userId());
        List<CourseProgressInfo> responseList = mapper.toResponseListAsCourseProgressInfo(courseProgressList);
        return responseList;
    }

}
