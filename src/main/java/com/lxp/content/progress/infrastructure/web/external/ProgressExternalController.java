package com.lxp.content.progress.infrastructure.web.external;

import com.lxp.common.infrastructure.exception.ApiResponse;
import com.lxp.content.progress.application.mapper.ProgressWebMapper;
import com.lxp.content.progress.application.port.in.usecase.UpdateProgressUseCase;
import com.lxp.content.progress.infrastructure.web.external.dto.UpdateProgressRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * 외부용 강좌 진행률 컨트롤러
 */
@RestController
@RequestMapping("/api-v1/progress")
public class ProgressExternalController {

    private final UpdateProgressUseCase updateProgressUseCase;

    private final ProgressWebMapper progressWebMapper;

    public ProgressExternalController(UpdateProgressUseCase updateProgressUseCase, ProgressWebMapper progressWebMapper) {
        this.updateProgressUseCase = updateProgressUseCase;
        this.progressWebMapper = progressWebMapper;
    }

    /**
     * 진행률 업데이트
     * @param userId 사용자 ID
     * @param courseId 강좌 ID
     * @param lectureId 강의 ID
     * @param request 요청 본문
     * @return 성공 실패 응답 여부
     */
    @PatchMapping("/{courseId}/{lectureId}")
    public ResponseEntity<ApiResponse<Void>> updateProgress(
            @RequestHeader("X-Passport") String userId, //TODO 인증 정보에서 사용자 ID 가져오기
            @PathVariable String courseId,
            @PathVariable String lectureId,
            @RequestBody UpdateProgressRequest request
    ) {
        updateProgressUseCase.execute(progressWebMapper.toCommand(
                userId,
                courseId,
                lectureId,
                request
        ));

        return ResponseEntity.ok(ApiResponse.success());
    }

}
