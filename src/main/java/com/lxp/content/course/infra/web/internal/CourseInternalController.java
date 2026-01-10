package com.lxp.content.course.infra.web.internal;

import com.lxp.content.course.application.port.provider.query.interanl.CheckCourseExistsQuery;
import com.lxp.content.course.application.port.provider.usecase.query.internal.CheckCourseExistsUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController("/api-v1/internal/courses")
@RequiredArgsConstructor
public class CourseInternalController {

    private final CheckCourseExistsUseCase checkCourseExistsUseCase;

    @GetMapping("{courseId}/exists")
    public ResponseEntity<Boolean> exist(@PathVariable String courseId) {

        CheckCourseExistsQuery query =
                new CheckCourseExistsQuery(courseId);

        Boolean exists = checkCourseExistsUseCase.execute(query);

        return ResponseEntity.ok(exists);
    }
}
