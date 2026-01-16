package com.lxp.content.course.infra.web.external.mapper;

import com.lxp.common.domain.pagination.Page;
import com.lxp.content.course.application.port.provider.command.CourseCreateCommand;
import com.lxp.content.course.application.port.provider.view.CourseDetailView;
import com.lxp.content.course.application.port.provider.view.CourseView;
import com.lxp.content.course.application.port.provider.view.InstructorView;
import com.lxp.content.course.application.port.provider.view.TagInfoView;
import com.lxp.content.course.domain.model.enums.Level;
import com.lxp.content.course.infra.web.external.dto.request.create.CourseCreateRequest;
import com.lxp.content.course.infra.web.external.dto.request.create.LectureCreateRequest;
import com.lxp.content.course.infra.web.external.dto.request.create.SectionCreateRequest;
import com.lxp.content.course.infra.web.external.dto.response.*;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class CourseWebMapper {

    public CourseCreateCommand toCreateCommand(String userId, CourseCreateRequest request) {
        return new CourseCreateCommand(
                userId,
                request.title(),
                request.description(),
                request.thumbnailUrl(),
                request.level(),
                request.tags(),
                request.sections().stream()
                        .map(this::toSectionCommand)
                        .toList()
        );
    }

    private CourseCreateCommand.SectionCreateCommand toSectionCommand(SectionCreateRequest sec) {
        return new CourseCreateCommand.SectionCreateCommand(
                sec.title(),
                sec.lectures().stream()
                        .map(this::toLectureCommand)
                        .toList()
        );
    }

    private CourseCreateCommand.SectionCreateCommand.LectureCreateCommand toLectureCommand(LectureCreateRequest lec) {
        return new CourseCreateCommand.SectionCreateCommand.LectureCreateCommand(lec.title(), lec.videoUrl());
    }

    public CourseDetailResponse toDetailResponse(CourseDetailView result) {
        List<SectionResponse> sections = result.sections() == null
                ? List.of()
                : result.sections().stream()
                .map(this::toSectionResponse)
                .toList();

        return new CourseDetailResponse(
                result.courseId(),
                toInstructorResponse(result.Instructor()),
                result.title(),
                result.description(),
                result.thumbnailUrl(),
                toEnumResponse(result.level()),
                toTagResponses(result.tags()),
                sections,
                result.durationOnMinutes(),
                result.createdAt(),
                result.updatedAt()
        );
    }

    public Page<CourseResponse> toPageResponse(Page<CourseView> page) {
        return page.map(this::toCourseResponse);
    }

    public CourseResponse toCourseResponse(CourseView view) {
        return new CourseResponse(
                view.courseId(),
                toInstructorResponse(view.Instructor()),
                view.title(),
                view.description(),
                view.thumbnailUrl(),
                toEnumResponse(view.level()),
                toTagResponses(view.tags()),
                view.createdAt(),
                view.updatedAt()
        );
    }

    private SectionResponse toSectionResponse(CourseDetailView.SectionView sec) {
        return new SectionResponse(
                sec.sectionId(),
                sec.title(),
                sec.durationOnSecond(),
                sec.order(),
                sec.lectures().stream()
                        .map(this::toLectureResponse)
                        .toList()
        );
    }

    private LectureResponse toLectureResponse(CourseDetailView.SectionView.LectureView lect) {
        return new LectureResponse(
                lect.lectureId(),
                lect.title(),
                lect.videoUrl(),
                lect.order(),
                lect.duration()
        );
    }

    private EnumResponse toEnumResponse(Level level) {
        return new EnumResponse(level.name(), level.description());
    }

    private InstructorResponse toInstructorResponse(InstructorView instructor) {
        return new InstructorResponse(instructor.instructorId(), instructor.name());
    }

    private List<TagResponse> toTagResponses(List<TagInfoView> tags) {
        if (tags == null) {
            return List.of();
        }
        return tags.stream()
                .map(t -> new TagResponse(t.id(), t.content(), t.color(), t.variant()))
                .toList();
    }

}
