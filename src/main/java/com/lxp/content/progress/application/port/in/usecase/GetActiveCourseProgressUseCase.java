package com.lxp.content.progress.application.port.in.usecase;

import com.lxp.common.application.port.in.UseCase;
import com.lxp.content.progress.application.port.in.query.GetActiveCourseProgressQuery;
import com.lxp.content.progress.application.port.in.response.CourseProgressInfo;

import java.util.List;

/**
 * 사용자 ID 기반 수강중인 강좌 진행률 리스트 조회
 */
public interface GetActiveCourseProgressUseCase extends UseCase<GetActiveCourseProgressQuery, List<CourseProgressInfo>> {

    @Override
    List<CourseProgressInfo> execute(GetActiveCourseProgressQuery query);

}
