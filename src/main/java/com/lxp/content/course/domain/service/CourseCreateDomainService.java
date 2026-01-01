package com.lxp.content.course.domain.service;

import com.github.f4b6a3.uuid.UuidCreator;
import com.lxp.common.domain.annotation.DomainService;
import com.lxp.content.course.domain.model.Course;
import com.lxp.content.course.domain.model.Lecture;
import com.lxp.content.course.domain.model.Section;
import com.lxp.content.course.domain.model.collection.CourseSections;
import com.lxp.content.course.domain.model.collection.CourseTags;
import com.lxp.content.course.domain.model.collection.SectionLectures;
import com.lxp.content.course.domain.model.id.*;
import com.lxp.content.course.domain.model.vo.duration.LectureDuration;
import com.lxp.content.course.domain.policy.CourseCreationPolicy;
import com.lxp.content.course.domain.service.spec.CourseCreateSpec;
import com.lxp.content.course.domain.service.spec.InstructorSpec;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@DomainService
public class CourseCreateDomainService {

    private final CourseCreationPolicy creationPolicy =
            new CourseCreationPolicy();

    public Course create(CourseCreateSpec spec, InstructorSpec instructor) {
        creationPolicy.validate(spec, instructor);

        return  Course.create(
                new CourseUUID(UuidCreator.getTimeOrderedEpoch().toString()),
                new InstructorUUID(spec.instructorId()),
                spec.thumbnailUrl(),
                spec.title(),
                spec.description(),
                spec.level(),
                createSections(spec.sections()),
                createTags(spec.tags())
        );

    }

    private static CourseSections createSections(List<CourseCreateSpec.SectionCreateSpec> specs) {
        if (specs == null) {
            return CourseSections.empty();
        }

        AtomicInteger order = new AtomicInteger(1);

        List<Section> sections = specs.stream().map(spec ->
                Section.create(
                        spec.title(),
                        new SectionUUID(UuidCreator.getTimeOrderedEpoch().toString()),
                        order.getAndIncrement(),
                        createLectures(spec.lectures())
                )
        ).collect(Collectors.toList());


        return CourseSections.of(sections);
    }

    private static SectionLectures createLectures(List<CourseCreateSpec.LectureCreateSpec> specs) {
        AtomicInteger order = new AtomicInteger(1);

        List<Lecture> lectures = specs.stream().map(spec -> Lecture.create(
                        spec.title(),
                        new LectureUUID(UuidCreator.getTimeOrderedEpoch().toString()),
                        LectureDuration.randomUnder20Minutes(),
                        order.getAndIncrement(),
                        spec.videoUrl()
                )
        ).collect(Collectors.toList());

        return SectionLectures.of(lectures);
    }


    private static CourseTags createTags(List<Long> tagIds) {
        return CourseTags.of(
                tagIds.stream()
                        .map(TagId::new)
                        .toList()
        );
    }

}
