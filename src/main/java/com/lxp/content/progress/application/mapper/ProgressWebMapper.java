package com.lxp.content.progress.application.mapper;

import com.lxp.content.progress.application.port.in.command.UpdateProgressCommand;
import com.lxp.content.progress.application.port.in.query.GetActiveCourseProgressQuery;
import com.lxp.content.progress.application.port.in.query.GetLectureProgressListQuery;
import com.lxp.content.progress.application.port.in.response.CourseProgressInfo;
import com.lxp.content.progress.application.port.in.response.LectureProgressInfo;
import com.lxp.content.progress.domain.model.CourseProgress;
import com.lxp.content.progress.domain.model.LectureProgress;
import com.lxp.content.progress.infrastructure.web.external.dto.UpdateProgressRequest;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ProgressWebMapper {

    /**
     * Web Request -> Command 변환
     * @param userId 사용자 ID
     * @param courseId 강좌 ID
     * @param lectureId 강의 ID
     * @param request 요청 본문
     * @return 진행률 업데이트 커맨드
     */
    public UpdateProgressCommand toCommand(String userId, String courseId, String lectureId, UpdateProgressRequest request) {
        return new UpdateProgressCommand(userId, courseId, lectureId, request.lastPlayedTime());
    }

    /**
     * Web Request -> Query 변환(강좌 진행률 조회 리스트)
     * @param userId 사용자 ID
     * @return 수강중인 강좌 진행률 조회 쿼리
     */
    public GetActiveCourseProgressQuery toGetActiveCourseProgressQuery(String userId) {
        return new GetActiveCourseProgressQuery(userId);
    }

    /**
     * Web Request -> Query 변환(강의 진행률 조회 리스트)
     * @param userId 사용자 ID
     * @param courseId 강좌 ID
     * @return 수강중인 강좌 내 강의 진행률 조회 쿼리
     */
    public GetLectureProgressListQuery toGetLectureProgressListQuery(String userId, String courseId) {
        return new GetLectureProgressListQuery(userId, courseId);
    }

    /**
     * Domain -> Response 변환(CourseProgressInfo)
     * @param courseProgressList 강좌 진행률 리스트
     * @return 강좌 진행률 정보 리스트
     */
    public List<CourseProgressInfo> toResponseListAsCourseProgressInfo(List<CourseProgress> courseProgressList) {
        System.out.println(courseProgressList.size());
        List<CourseProgressInfo> responseList = courseProgressList.stream()
                .map(cp -> new CourseProgressInfo(
                        cp.courseId().value(),
                        cp.totalProgress(),
                        cp.isCompleted()
                )).toList();
        return responseList;
    }

    /**
     * Domain -> Response 변환(LectureProgressInfo)
     * @param lectureProgresses 강의 진행률 리스트
     * @return 강의 진행률 정보 리스트
     */
    public List<LectureProgressInfo> toResponseListAsLectureProgressInfo(List<LectureProgress> lectureProgresses) {
        return lectureProgresses.stream()
                .map(lp -> new LectureProgressInfo(
                        lp.lectureId().value(),
                        lp.lastPlayedTimeInSeconds(),
                        lp.totalDurationInSeconds(),
                        lp.completed()
                )).toList();
    }
}
