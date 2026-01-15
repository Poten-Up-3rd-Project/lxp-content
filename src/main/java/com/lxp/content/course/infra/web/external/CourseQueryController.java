package com.lxp.content.course.infra.web.external;

import com.lxp.common.domain.pagination.Page;
import com.lxp.common.domain.pagination.PageRequest;
import com.lxp.common.domain.pagination.Sort;
import com.lxp.content.course.application.port.provider.query.CourseDetailQuery;
import com.lxp.content.course.application.port.provider.query.CourseSearchQuery;
import com.lxp.content.course.application.port.provider.usecase.query.CourseDetailUseCase;
import com.lxp.content.course.application.port.provider.usecase.query.CourseSearchUseCase;
import com.lxp.content.course.application.port.provider.view.CourseDetailView;
import com.lxp.content.course.application.port.provider.view.CourseView;
import com.lxp.content.course.infra.web.external.dto.response.CourseDetailResponse;
import com.lxp.content.course.infra.web.external.dto.response.CourseResponse;
import com.lxp.content.course.infra.web.external.mapper.CourseWebMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api-v1/courses")
@RequiredArgsConstructor
public class CourseQueryController {

    private final CourseSearchUseCase courseSearchUseCase;
    private final CourseDetailUseCase courseDetailUseCase;
    private final CourseWebMapper mapper;

    @GetMapping("/search")
    public ResponseEntity<Page<CourseResponse>> search(
            @RequestParam(value = "keyword") String keyword,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "20") int size,
            @RequestParam(value ="sort", defaultValue = "createdAt") String sort,
            @RequestParam(value = "dir", defaultValue = "DESC") String dir
    ) {
        PageRequest pageRequest = PageRequest.of(page, size, Sort.by(Sort.Direction.valueOf(dir.toUpperCase()), sort));
        CourseSearchQuery query = new CourseSearchQuery(keyword, pageRequest);
        Page<CourseView> view = courseSearchUseCase.execute(query);

        return ResponseEntity.ok(mapper.toPageResponse(view));
    }

    @GetMapping("/{courseId}")
    public ResponseEntity<CourseDetailResponse> get(
            @PathVariable("courseId") String courseId
    ){

        CourseDetailQuery query = new CourseDetailQuery(courseId);
        CourseDetailView view = courseDetailUseCase.execute(query);

        return ResponseEntity.ok(mapper.toDetailResponse(view));
    }
}
