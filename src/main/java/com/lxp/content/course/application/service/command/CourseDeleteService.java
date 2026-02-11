package com.lxp.content.course.application.service.command;


import com.lxp.common.application.port.out.DomainEventPublisher;
import com.lxp.content.course.application.port.provider.command.CourseDeleteCommand;
import com.lxp.content.course.application.port.provider.usecase.command.CourseDeleteUseCase;
import com.lxp.content.course.domain.model.Course;
import com.lxp.content.course.domain.model.id.InstructorUUID;
import com.lxp.content.course.domain.repository.CourseRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class CourseDeleteService implements CourseDeleteUseCase {
    private final CourseRepository courseRepository;
    private final DomainEventPublisher domainEventPublisher;
    @Override
    public Void execute(CourseDeleteCommand command) {

        Course course = courseRepository.findByUUID(command.courseId())
                .orElseThrow(() -> new IllegalArgumentException("Course not found: " + command.courseId()));

        course.delete(new InstructorUUID(command.userId()));
        courseRepository.save(course);
        publishEvents(course);
        return null;
    }

    private void publishEvents(Course course) {
        course.getDomainEvents().forEach(domainEventPublisher::publish);
        course.clearDomainEvents();
    }
}
