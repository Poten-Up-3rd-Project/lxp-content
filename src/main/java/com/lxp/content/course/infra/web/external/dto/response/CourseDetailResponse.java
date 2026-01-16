package com.lxp.content.course.infra.web.external.dto.response;

import lombok.Getter;

import java.time.Instant;
import java.util.List;

@Getter
public class CourseDetailResponse extends CourseResponse {
    List<SectionResponse> sections;
    long durationInHours;

    public CourseDetailResponse(String id,
                                InstructorResponse instructor,
                                String title,
                                String description,
                                String thumbnailUrl,
                                EnumResponse level,
                                List<TagResponse> tags,
                                List<SectionResponse> sections,
                                long durationInHours,
                                Instant createdAt,
                                Instant updatedAt
    ) {
        super(id, instructor, title, description, thumbnailUrl, level, tags, createdAt,updatedAt);
        this.sections = sections;
        this.durationInHours = durationInHours;
    }

}
