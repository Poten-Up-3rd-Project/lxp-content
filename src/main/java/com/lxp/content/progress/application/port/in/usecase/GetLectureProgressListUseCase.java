package com.lxp.content.progress.application.port.in.usecase;

import com.lxp.common.application.port.in.UseCase;
import com.lxp.content.progress.application.port.in.query.GetLectureProgressListQuery;
import com.lxp.content.progress.application.port.in.response.LectureProgressInfo;

import java.util.List;

/**
 * 사용자 ID 기반 수강 중인 특정 강좌 내의 강의별 진행률 리스트 조회 유스케이스
 */
public interface GetLectureProgressListUseCase extends UseCase<GetLectureProgressListQuery, List<LectureProgressInfo>> {

    @Override
    List<LectureProgressInfo> execute(GetLectureProgressListQuery input);

}
