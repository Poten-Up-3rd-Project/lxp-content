package com.lxp.content.course.infra.web.internal;

import com.lxp.content.course.application.port.provider.query.CheckCourseExistsQuery;
import com.lxp.content.course.application.port.provider.query.CourseFilterQuery;
import com.lxp.content.course.application.port.provider.usecase.query.CheckCourseExistsUseCase;
import com.lxp.content.course.application.port.provider.usecase.query.CourseFilterUseCase;
import com.lxp.content.course.application.port.provider.view.CourseView;
import com.lxp.content.course.infra.web.external.dto.response.CourseResponse;
import com.lxp.content.course.infra.web.internal.dto.CourseFilterRequest;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api-v1/internal/courses")
@RequiredArgsConstructor
public class CourseInternalController {

    private final CheckCourseExistsUseCase checkCourseExistsUseCase;
    private final CourseFilterUseCase courseFilterUseCase;

    @GetMapping("/{courseId}/exists")
    public ResponseEntity<Boolean> exist(@PathVariable String courseId) {

        CheckCourseExistsQuery query =
                new CheckCourseExistsQuery(courseId);

        Boolean exists = checkCourseExistsUseCase.execute(query);

        return ResponseEntity.ok(exists);
    }


    @PostMapping("/filter")
    public ResponseEntity<List<CourseView>> filter(
            @RequestBody @Valid CourseFilterRequest request) { // RequestBody로 변경

        CourseFilterQuery query = new CourseFilterQuery(
                request.ids(),
                request.difficulties(),
                request.limit()
        );

        var result = courseFilterUseCase.execute(query);
        return ResponseEntity.ok(result);
    }
}
