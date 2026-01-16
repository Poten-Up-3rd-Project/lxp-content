package com.lxp.content.progress.infrastructure.web.internal;

import com.lxp.content.progress.application.mapper.ProgressWebMapper;
import com.lxp.content.progress.application.port.in.response.CourseProgressInfo;
import com.lxp.content.progress.application.port.in.response.LectureProgressInfo;
import com.lxp.content.progress.application.port.in.usecase.GetActiveCourseProgressUseCase;
import com.lxp.content.progress.application.port.in.usecase.GetLectureProgressListUseCase;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api-v1/internal/progress")
public class ProgressInternalController {

    private final GetActiveCourseProgressUseCase getActiveCourseProgressUseCase;
    private final GetLectureProgressListUseCase getLectureProgressListUseCase;

    private final ProgressWebMapper mapper;

    public ProgressInternalController(
            GetActiveCourseProgressUseCase getActiveCourseProgressUseCase,
            GetLectureProgressListUseCase getLectureProgressListUseCase,
            ProgressWebMapper mapper
    ) {
        this.getActiveCourseProgressUseCase = getActiveCourseProgressUseCase;
        this.getLectureProgressListUseCase = getLectureProgressListUseCase;
        this.mapper = mapper;
    }

    /**
     * 특정 유저의 수강 중인 강좌별 진행률 리스트 조회
     * @return 강좌별 진행률 정보 리스트
     */
    @GetMapping("/users/courses")
    public ResponseEntity<List<CourseProgressInfo>> getActiveCourseProgressList(@RequestHeader("X-Passport") String userId) {
        return ResponseEntity.ok(getActiveCourseProgressUseCase.execute(mapper.toGetActiveCourseProgressQuery(userId)));
    }

    /**
     * 특정 유저가 수강 중인 특정 강좌 내의 강의별 진행률 리스트 조회
     * @param courseId 강좌 ID
     * @return 강의별 진행률 정보 리스트
     */
    @GetMapping("/users/courses/{courseId}")
    public ResponseEntity<List<LectureProgressInfo>> getLessonProgressList(
            @RequestHeader("X-Passport") String userId,
            @PathVariable String courseId) {
        return ResponseEntity.ok(getLectureProgressListUseCase.execute(
                mapper.toGetLectureProgressListQuery(userId, courseId)
        ));
    }

}
