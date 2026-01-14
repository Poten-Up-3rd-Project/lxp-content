package com.lxp.content.course.infra.web.external;

import com.lxp.content.course.application.port.provider.command.CourseCreateCommand;
import com.lxp.content.course.application.port.provider.usecase.command.CourseCreateUseCase;
import com.lxp.content.course.application.port.provider.view.CourseDetailView;
import com.lxp.content.course.infra.web.external.dto.request.create.CourseCreateRequest;
import com.lxp.content.course.infra.web.external.dto.response.CourseDetailResponse;
import com.lxp.content.course.infra.web.external.mapper.CourseWebMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api-v1/courses")
@RequiredArgsConstructor
public class CourseCommandController {
    private final CourseWebMapper mapper;
    private final CourseCreateUseCase createUseCase;

    // TODO : 인증 적용 후 userId 주입
    @GetMapping
    public ResponseEntity<CourseDetailResponse> create(String userId, @RequestBody CourseCreateRequest request) {
        CourseCreateCommand command = mapper.toCreateCommand(userId,request);
        CourseDetailView view = createUseCase.execute(command);
        return ResponseEntity.ok(mapper.toDetailResponse(view));
    }

}
