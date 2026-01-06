package com.lxp.content.course.application.mapper;

import com.lxp.common.domain.pagination.Page;
import com.lxp.content.course.application.port.provider.view.CourseDetailView;
import com.lxp.content.course.application.port.provider.view.CourseView;
import com.lxp.content.course.application.port.provider.view.InstructorView;
import com.lxp.content.course.application.port.provider.view.TagInfoView;
import com.lxp.content.course.application.port.required.dto.InstructorResult;
import com.lxp.content.course.application.port.required.dto.TagResult;
import com.lxp.content.course.application.projection.CourseReadModel;
import com.lxp.content.course.domain.model.Course;
import com.lxp.content.course.domain.model.Lecture;
import com.lxp.content.course.domain.model.Section;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;

@Component
public class CourseViewMapper {

    public Page<CourseView> toPageView(Page<CourseReadModel> courses) {
        return courses.map(this::toListView);
    }


    public CourseView toListView(CourseReadModel model) {
        return new CourseView(
                model.uuid(),
                model.title(),
                model.description(),
                new InstructorView(
                        model.instructorId(),
                        model.instructorName()
                ),
                model.thumbnailUrl(),
                model.difficulty(),
                toInstant(model.createdAt()),
                toInstant(model.updatedAt()),
                model.tags().stream().map(
                        tag -> new TagInfoView(
                                tag.id(),
                                tag.content(),
                                tag.color(),
                                tag.variant()
                        )).toList()
        );
    }

    private Instant toInstant(LocalDateTime localDateTime) {
        if (localDateTime == null) {
            return null;
        }
        return localDateTime.atZone(ZoneId.systemDefault()).toInstant();
    }


    public CourseDetailView toCourseDetailView(Course course,
                                               List<TagResult> tagResult,
                                               InstructorResult instructorResult
    ) {
        return new CourseDetailView(
                course.uuid().value(),
                course.title().value(),
                course.description().value(),
                new InstructorView(
                        instructorResult.userId(),
                        instructorResult.name()),
                course.thumbnailUrl(),
                course.difficulty(),
                course.sections().values().stream().map(this::toSectionView).toList(),
                toTagInfoViewList(tagResult),
                course.createdAt(),
                course.updatedAt(),
                course.totalDuration().toMinutes()
        );
    }

    private List<TagInfoView> toTagInfoViewList(List<TagResult> tagResult) {
        return tagResult.stream()
                .map(tag -> new TagInfoView(tag.id(), tag.content(), tag.color(), tag.variant()))
                .toList();
    }

    private CourseDetailView.SectionView toSectionView(Section section) {
        return new CourseDetailView.SectionView(
                section.uuid().value(),
                section.title(),
                section.totalDuration().seconds(),
                section.order(),
                section.lectures().values().stream()
                        .map(this::toLectureView)
                        .toList()
        );
    }

    private CourseDetailView.SectionView.LectureView toLectureView(Lecture lecture) {
        return new CourseDetailView.SectionView.LectureView(
                lecture.uuid().value(),
                lecture.title(),
                lecture.videoUrl(),
                lecture.order(),
                lecture.duration().seconds()
        );
    }
}