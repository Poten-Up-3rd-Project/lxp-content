package com.lxp.content.course.application.projection.repository;

import com.lxp.common.application.port.out.read.ReadModelRepository;
import com.lxp.common.domain.pagination.Page;
import com.lxp.common.domain.pagination.PageRequest;
import com.lxp.content.course.application.projection.CourseReadModel;

import java.util.List;

public interface CourseReadRepository extends ReadModelRepository<CourseReadModel, String> {

    Page<CourseReadModel> search(String keyword, PageRequest pageable);
    void save(CourseReadModel course);
    Boolean existsById(String courseId);

    List<CourseReadModel> filter(List<String> strings, List<String> difficulties, int count);
}
