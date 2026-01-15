package com.lxp.content.course.infra.web.internal;

import com.lxp.content.course.application.port.provider.query.CheckCourseExistsQuery;
import com.lxp.content.course.application.port.provider.query.CourseFilterQuery;
import com.lxp.content.course.application.port.provider.usecase.query.CheckCourseExistsUseCase;
import com.lxp.content.course.application.port.provider.usecase.query.CourseFilterUseCase;
import com.lxp.content.course.application.port.provider.view.CourseView;
import com.lxp.content.course.infra.web.external.dto.response.CourseResponse;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/internal/api-v1/courses")
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


    //TODO(필드 늘어나면 객체로 관리)
    @GetMapping("/get-by-filters")
    public ResponseEntity<List<CourseView>> filter(
            @RequestParam(value= "ids", required = false) List<String> ids,
            @RequestParam(value = "difficulties", required = false) List<String> levels,
            @RequestParam(value = "limit", defaultValue = "10") @Min(1) @Max(100) int count    ) {

        CourseFilterQuery query =
                new CourseFilterQuery(ids, levels, count);

        var result = courseFilterUseCase.execute(query);
        return ResponseEntity.ok(result);
    }
}
