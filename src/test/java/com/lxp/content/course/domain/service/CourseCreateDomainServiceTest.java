package com.lxp.content.course.domain.service;

import com.lxp.content.course.domain.exception.CourseException;
import com.lxp.content.course.domain.model.Course;
import com.lxp.content.course.domain.model.Section;
import com.lxp.content.course.domain.model.enums.Level;
import com.lxp.content.course.domain.service.spec.CourseCreateSpec;
import com.lxp.content.course.domain.service.spec.InstructorSpec;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class CourseCreateDomainServiceTest {
    private CourseCreateDomainService domainService;

    @BeforeEach
    void setUp() {
        domainService = new CourseCreateDomainService();
    }

    @Nested
    @DisplayName("Course 생성 성공")
    class CreateSuccess {

        @Test
        @DisplayName("정상적인 Course 생성")
        void create_Success() {
            // Given
            CourseCreateSpec command = createValidCommand();
            InstructorSpec instructor = createActiveInstructor();

            // When
            Course course = domainService.create(command, instructor);

            // Then
            assertThat(course).isNotNull();
            assertThat(course.uuid()).isNotNull();
            assertThat(course.title().value()).isEqualTo("테스트 강좌");
            assertThat(course.description().value()).isEqualTo("테스트 설명");
            assertThat(course.level()).isEqualTo(Level.JUNIOR);
        }

        @Test
        @DisplayName("Section과 Lecture가 포함된 Course 생성")
        void create_WithSectionsAndLectures() {
            // Given
            CourseCreateSpec command = createCommandWithSectionsAndLectures();
            InstructorSpec instructor = createActiveInstructor();

            // When
            Course course = domainService.create(command, instructor);

            // Then
            assertThat(course.sections().values().size()).isEqualTo(2);
        }

        @Test
        @DisplayName("Tag가 포함된 Course 생성")
        void create_WithTags() {
            // Given
            var command = createValidCommand();
            var instructor = createActiveInstructor();

            // When
            Course course = domainService.create(command, instructor);

            // Then
            assertThat(course.tags().values().size()).isEqualTo(3);
        }

        @Test
        @DisplayName("Section 순서가 올바르게 설정됨")
        void create_SectionOrderIsCorrect() {
            // Given
            var command = createCommandWithSectionsAndLectures();
            var instructor = createActiveInstructor();

            // When
            Course course = domainService.create(command, instructor);

            // Then
            List<Integer> orders = course.sections().values().stream()
                    .map(Section::order)
                    .toList();
            assertThat(orders).containsExactly(1, 2);
        }
    }

    @Nested
    @DisplayName("Course 생성 실패 - Instructor 검증")
    class CreateFailInstructor {


        @Test
        @DisplayName("Instructor 역할이 INSTRUCTOR가 아니면 예외 발생")
        void create_InstructorRoleInvalid_ThrowsException() {
            // Given
            var command = createValidCommand();
            var instructor = new InstructorSpec(
                    "instructor-uuid",
                    "ACTIVE",
                    "STUDENT"
            );

            // When & Then
            assertThatThrownBy(() -> domainService.create(command, instructor))
                    .isInstanceOf(CourseException.class);
        }
    }

    @Nested
    @DisplayName("Course 생성 실패 - Section 검증")
    class CreateFailSection {

        @Test
        @DisplayName("Section이 없으면 예외 발생")
        void create_NoSections_ThrowsException() {
            // Given
            var command = createCommandWithoutSections();
            var instructor = createActiveInstructor();

            // When & Then
            assertThatThrownBy(() -> domainService.create(command, instructor))
                    .isInstanceOf(RuntimeException.class);  // BusinessRuleException 또는 해당 예외
        }
    }

    // === Test Fixtures ===

    private CourseCreateSpec createValidCommand() {
        return new CourseCreateSpec(
                "instructor-uuid",
                "https://thumbnail.url",
                "테스트 강좌",
                "테스트 설명",
                Level.JUNIOR,
                List.of(createSectionCommand("섹션1"), createSectionCommand("섹션2")),
                List.of(1L,2L,3L)
        );
    }

    private CourseCreateSpec createCommandWithSectionsAndLectures() {
        return new CourseCreateSpec(
                "instructor-uuid",
                "https://thumbnail.url",
                "테스트 설명",
                "테스트 강좌",
                Level.JUNIOR,
                List.of(
                        createSectionCommandWithLectures("섹션1", 3),
                        createSectionCommandWithLectures("섹션2", 2)
                ),
                List.of(1L, 2L, 3L, 4L)
        );
    }

    private CourseCreateSpec createCommandWithoutSections() {
        return new CourseCreateSpec(
                "instructor-uuid",
                "https://thumbnail.url",
                "테스트 설명",
                "테스트 강좌",
                Level.JUNIOR,
                List.of(),
                List.of(1L, 2L,3L ,4L)
        );
    }

    private CourseCreateSpec.SectionCreateSpec createSectionCommand(String title) {
        return new CourseCreateSpec.SectionCreateSpec(
                title,
                List.of(createLectureCommand("강의1"))
        );
    }

    private CourseCreateSpec.SectionCreateSpec createSectionCommandWithLectures(String title, int lectureCount) {
        List<CourseCreateSpec.LectureCreateSpec> lectures = java.util.stream.IntStream.rangeClosed(1, lectureCount)
                .mapToObj(i -> createLectureCommand("강의" + i))
                .toList();

        return new CourseCreateSpec.SectionCreateSpec(title, lectures);
    }

    private CourseCreateSpec.LectureCreateSpec createLectureCommand(String title) {
        return new CourseCreateSpec.LectureCreateSpec(title, "https://video.url");
    }

    private InstructorSpec createActiveInstructor() {
        return new InstructorSpec(
                "instructor-uuid",
                "test",
                "INSTRUCTOR"
        );
    }
}
