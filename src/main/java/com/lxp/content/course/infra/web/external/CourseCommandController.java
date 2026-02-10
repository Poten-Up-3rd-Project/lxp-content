package com.lxp.content.course.infra.web.external;

import com.lxp.content.course.application.port.provider.command.CourseCreateCommand;
import com.lxp.content.course.application.port.provider.command.CourseDeleteCommand;
import com.lxp.content.course.application.port.provider.usecase.command.CourseCreateUseCase;
import com.lxp.content.course.application.port.provider.usecase.command.CourseDeleteUseCase;
import com.lxp.content.course.application.port.provider.view.CourseDetailView;
import com.lxp.content.course.infra.web.external.dto.request.create.CourseCreateRequest;
import com.lxp.content.course.infra.web.external.dto.response.CourseDetailResponse;
import com.lxp.content.course.infra.web.external.mapper.CourseWebMapper;
import com.lxp.passport.authorization.annotation.CurrentUserId;
import com.lxp.passport.authorization.annotation.RequireRole;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api-v1/courses")
@RequiredArgsConstructor
public class CourseCommandController {
    private final CourseWebMapper mapper;
    private final CourseCreateUseCase createUseCase;
    private final CourseDeleteUseCase deleteUseCase;
    // TODO : 인증 적용 후 userId 주입

    @RequireRole("ROLE_INSTRUCTOR")
    @PostMapping
    public ResponseEntity<CourseDetailResponse> create(@CurrentUserId String userId,
                                                       @RequestBody CourseCreateRequest request) {
        CourseCreateCommand command = mapper.toCreateCommand(userId, request);
        CourseDetailView view = createUseCase.execute(command);
        return ResponseEntity.ok(mapper.toDetailResponse(view));
    }

    @RequireRole("ROLE_INSTRUCTOR")
    @PostMapping("/{courseId}/delete")
    public ResponseEntity<Void> delete(@CurrentUserId String userId,
                                       @PathVariable("courseId") String courseId) {

        CourseDeleteCommand command = mapper.toDeleteCommand(userId, courseId);
        deleteUseCase.execute(command);
        return ResponseEntity.ok().build();
    }

}
