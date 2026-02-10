package com.lxp.content.course.application.service;

import com.lxp.content.course.application.port.provider.command.CourseDeleteCommand;
import com.lxp.content.course.application.port.required.TagQueryPort;
import com.lxp.content.course.application.port.required.UserQueryPort;
import com.lxp.content.course.application.service.command.CourseDeleteService;
import com.lxp.content.course.domain.model.Course;
import com.lxp.content.course.domain.model.Lecture;
import com.lxp.content.course.domain.model.Section;
import com.lxp.content.course.domain.model.collection.CourseSections;
import com.lxp.content.course.domain.model.collection.CourseTags;
import com.lxp.content.course.domain.model.collection.SectionLectures;
import com.lxp.content.course.domain.model.enums.Level;
import com.lxp.content.course.domain.model.id.*;
import com.lxp.content.course.domain.model.vo.duration.LectureDuration;
import com.lxp.content.course.domain.repository.CourseRepository;
import com.lxp.content.course.infra.persistence.mysql.read.entity.CourseReadJpaEntity;
import com.lxp.content.course.infra.persistence.mysql.read.entity.Tag;
import com.lxp.content.course.infra.persistence.mysql.read.repository.CourseReadJpaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.awaitility.Awaitility.await;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@SpringBootTest
public class CourseDeleteServiceTest {

    private static final String COURSE_UUID = "course-delete-test-001";
    private static final String INSTRUCTOR_UUID = "instructor-1";

    @Autowired
    private CourseDeleteService courseDeleteService;

    @MockitoBean
    private CourseRepository courseRepository;

    @MockitoBean
    private UserQueryPort userQueryPort;

    @MockitoBean
    private TagQueryPort tagQueryPort;

    @Autowired
    private CourseReadJpaRepository courseReadJpaRepository;

    @BeforeEach
    void setUp() {
        courseReadJpaRepository.deleteAll();
    }

    @Test
    @DisplayName("강좌를 삭제하면 소프트 삭제되고 ReadModel이 제거된다")
    void execute_deletesCourseAndRemovesReadModel() {
        // given
        Course course = reconstructCourse(COURSE_UUID, INSTRUCTOR_UUID, null);
        when(courseRepository.findByUUID(COURSE_UUID)).thenReturn(Optional.of(course));
        when(courseRepository.save(any(Course.class))).thenAnswer(inv -> inv.getArgument(0));

        saveReadModel(COURSE_UUID);
        assertThat(courseReadJpaRepository.existsById(COURSE_UUID)).isTrue();

        // when
        courseDeleteService.execute(new CourseDeleteCommand(INSTRUCTOR_UUID, COURSE_UUID));

        // then - ReadModel이 비동기 핸들러에 의해 삭제됨
        await().atMost(5, TimeUnit.SECONDS).untilAsserted(() ->
                assertThat(courseReadJpaRepository.existsById(COURSE_UUID)).isFalse()
        );
    }

    @Test
    @DisplayName("강좌 소유자가 아닌 사용자가 삭제하면 예외가 발생한다")
    void execute_throwsExceptionWhenNotOwner() {
        // given
        Course course = reconstructCourse(COURSE_UUID, INSTRUCTOR_UUID, null);
        when(courseRepository.findByUUID(COURSE_UUID)).thenReturn(Optional.of(course));

        // when & then
        assertThatThrownBy(() ->
                courseDeleteService.execute(new CourseDeleteCommand("other-user", COURSE_UUID))
        ).isInstanceOf(RuntimeException.class);
    }

    @Test
    @DisplayName("존재하지 않는 강좌를 삭제하면 예외가 발생한다")
    void execute_throwsExceptionWhenCourseNotFound() {
        // given
        when(courseRepository.findByUUID("non-existent-uuid")).thenReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() ->
                courseDeleteService.execute(new CourseDeleteCommand(INSTRUCTOR_UUID, "non-existent-uuid"))
        ).isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Course not found");
    }

    @Test
    @DisplayName("이미 삭제된 강좌를 다시 삭제하면 예외가 발생한다")
    void execute_throwsExceptionWhenAlreadyDeleted() {
        // given - 이미 deleted 타임스탬프가 설정된 Course
        Course deletedCourse = reconstructCourse(COURSE_UUID, INSTRUCTOR_UUID, Instant.now());
        when(courseRepository.findByUUID(COURSE_UUID)).thenReturn(Optional.of(deletedCourse));

        // when & then
        assertThatThrownBy(() ->
                courseDeleteService.execute(new CourseDeleteCommand(INSTRUCTOR_UUID, COURSE_UUID))
        ).isInstanceOf(RuntimeException.class);
    }

    // ===== Helper Methods =====

    private Course reconstructCourse(String courseUuid, String instructorUuid, Instant deletedAt) {
        Lecture lecture = Lecture.reconstruct(
                new LectureUUID("lecture-001"), 1L, "1-1. 강의 소개",
                new LectureDuration(600), 1, "http://video.com/1"
        );
        Section section = Section.reconstruct(
                1L, new SectionUUID("section-001"), "1장. 소개",
                1, new SectionLectures(List.of(lecture))
        );
        CourseSections sections = new CourseSections(List.of(section));
        CourseTags tags = CourseTags.of(List.of(new TagId(1L), new TagId(2L)));

        return Course.reconstruct(
                1L,
                new CourseUUID(courseUuid),
                new InstructorUUID(instructorUuid),
                "thumbnail.png",
                "Java 기초",
                "자바 기초 강의입니다",
                Level.JUNIOR,
                sections,
                tags,
                Instant.now(),
                Instant.now(),
                deletedAt
        );
    }

    private void saveReadModel(String courseUuid) {
        CourseReadJpaEntity readEntity = CourseReadJpaEntity.builder()
                .uuid(courseUuid)
                .instructorId(INSTRUCTOR_UUID)
                .instructorName("홍길동")
                .title("Java 기초")
                .thumbnail("thumbnail.png")
                .description("자바 기초 강의입니다")
                .difficulty("JUNIOR")
                .tags(List.of(new Tag(1L, "Java", "#FF0000", "solid")))
                .tagSearchText("Java")
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
        courseReadJpaRepository.save(readEntity);
    }
}
