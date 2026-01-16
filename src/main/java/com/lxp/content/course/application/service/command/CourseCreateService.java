package com.lxp.content.course.application.service.command;

import com.lxp.common.application.port.out.DomainEventPublisher;
import com.lxp.content.course.application.mapper.CourseViewMapper;
import com.lxp.content.course.application.port.provider.command.CourseCreateCommand;
import com.lxp.content.course.application.port.provider.view.CourseDetailView;
import com.lxp.content.course.application.port.provider.usecase.command.CourseCreateUseCase;
import com.lxp.content.course.application.port.required.TagQueryPort;
import com.lxp.content.course.application.port.required.UserQueryPort;
import com.lxp.content.course.application.port.required.dto.InstructorResult;
import com.lxp.content.course.application.port.required.dto.TagResult;
import com.lxp.content.course.domain.model.Course;
import com.lxp.content.course.domain.repository.CourseRepository;
import com.lxp.content.course.domain.service.CourseCreateDomainService;
import com.lxp.content.course.domain.service.spec.CourseCreateSpec;
import com.lxp.content.course.domain.service.spec.InstructorSpec;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class CourseCreateService implements CourseCreateUseCase {
    private final CourseRepository courseRepository;
    private final CourseCreateDomainService courseCreateDomainService;
    private final CourseViewMapper viewMapper;
    private final DomainEventPublisher domainEventPublisher;
    private final UserQueryPort userQueryPort;
    private final TagQueryPort tagQueryPort;

    @Override
    public CourseDetailView execute(CourseCreateCommand input) {
        InstructorResult instructorInfo = userQueryPort.getInstructorInfo(input.instructorId());

        Course course = courseCreateDomainService.create(
                toSpec(input),
                toSpec(instructorInfo)
        );
        courseRepository.save(course);

        publishEvents(course);

        List<TagResult> tagResults = tagQueryPort.findTagByIds(input.tags());
        return viewMapper.toCourseDetailView(course, tagResults, instructorInfo);
    }


    private CourseCreateSpec toSpec(CourseCreateCommand input) {
        return new CourseCreateSpec(
                input.instructorId(),
                input.thumbnailUrl(),
                input.title(),
                input.description(),
                input.level(),
                toSectionSpecs(input.sections()),
                input.tags()
                );
    }

    private InstructorSpec toSpec(InstructorResult input) {
        return new InstructorSpec(
                input.userId(),
                input.name(),
                input.role()
                );
    }

    private List<CourseCreateSpec.SectionCreateSpec> toSectionSpecs(List<CourseCreateCommand.SectionCreateCommand> sections) {
        return sections == null ? List.of() : sections.stream()
                .map(s -> new CourseCreateSpec.SectionCreateSpec(s.title(), toLectureSpecs(s.lectures())))
                .toList();
    }

    private List<CourseCreateSpec.LectureCreateSpec> toLectureSpecs(List<CourseCreateCommand.SectionCreateCommand.LectureCreateCommand> lectures) {
        return lectures == null ? List.of() : lectures.stream()
                .map(l -> new CourseCreateSpec.LectureCreateSpec(l.title(), l.videoUrl()))
                .toList();
    }

    private void publishEvents(Course course) {
        course.getDomainEvents().forEach(domainEventPublisher::publish);
        course.clearDomainEvents();
    }
}
