package com.lxp.content.course.infra.persistence.write.adapter;

import com.lxp.content.course.domain.model.Course;
import com.lxp.content.course.domain.model.Section;
import com.lxp.content.course.domain.model.collection.CourseSections;
import com.lxp.content.course.domain.model.collection.CourseTags;
import com.lxp.content.course.domain.model.enums.Level;
import com.lxp.content.course.domain.model.id.*;
import com.lxp.content.course.domain.model.vo.duration.LectureDuration;
import com.lxp.content.course.infra.persistence.write.mapper.CourseEntityMapper;
import com.lxp.content.course.infra.persistence.write.repository.CourseJpaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;


import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@EnableJpaRepositories(
        basePackages = "com.lxp.content.course.infra.persistence.write"
)
@EntityScan(
        basePackages = "com.lxp.content.course.infra.persistence.write"
)
@EnableJpaAuditing
public class CoursePersistenceAdapterTest {
    @Autowired
    private CourseJpaRepository jpaRepository;

    private final CourseEntityMapper mapper = new CourseEntityMapper();
    private CoursePersistenceAdapter adapter;

    @BeforeEach
    void setUp() {
        adapter = new CoursePersistenceAdapter(jpaRepository, mapper);
        jpaRepository.deleteAll();
    }

    @Test
    @DisplayName("Course 저장 및 조회")
    void saveAndFind() {
        // given
        Course course = createTestCourse();

        // when
        Course saved = adapter.save(course);
        Optional<Course> found = adapter.findByUUID(saved.uuid().value());

        found.orElseThrow(() -> new RuntimeException("Course not found"));
        // then
        assertThat(found.get().uuid().value()).isEqualTo(saved.uuid().value());
        assertThat(found.get().title().value()).isEqualTo(saved.title().value());
        assertThat(found.get().sections().values().size()).isEqualTo(1);
        assertThat(found.get().sections().values().get(0).title()).isEqualTo(saved.sections().values().get(0).title());
        assertThat(found.get().sections().values().get(0).lectures().values().size()).isEqualTo(1);
    }

    private Course createTestCourse() {
        return createTestCourse("course-123", "Java 기초");
    }

    private Course createTestCourse(String uuid, String title) {
        Section section = Section.create(
                "섹션 1",
                new SectionUUID("section-" + uuid),
                1
        );
        section.addLecture(
                new LectureUUID("lecture-" + uuid),
                "1-1. 소개",
                new LectureDuration(300),
                "https://video.com/1"
        );

        CourseSections sections = new CourseSections(List.of(section));
        CourseTags tags = CourseTags.of(List.of(new TagId(1L)));

        return Course.create(
                new CourseUUID(uuid),
                new InstructorUUID("instructor-456"),
                "thumbnail.png",
                title,
                "설명",
                Level.JUNIOR,
                sections,
                tags
        );
    }

    @Test
    @DisplayName("UUID로 Course 조회 - 존재하는 경우")
    void findByUUID_success() {
        // given
        Course course = createTestCourse("course-111", "Java 기초");
        Course saved = adapter.save(course);

        // when
        Optional<Course> found = adapter.findByUUID(saved.uuid().value());

        found.orElseThrow(() -> new RuntimeException("Course not found"));
        // then
        assertThat(found.get().uuid().value()).isEqualTo(saved.uuid().value());
        assertThat(found.get().title().value()).isEqualTo("Java 기초");
    }

    @Test
    @DisplayName("여러 UUID로 Course 목록 조회")
    void findAllByUUID_success() {
        // given
        Course course1 = createTestCourse("course-1", "Java");
        Course course2 = createTestCourse("course-2", "Spring");
        Course course3 = createTestCourse("course-3", "DDD");

        adapter.save(course1);
        adapter.save(course2);
        adapter.save(course3);

        // when
        List<Course> found = adapter.findAllByUUID(
                List.of("course-1", "course-3")
        );

        // then
        assertThat(found.size()).isEqualTo(2);
        assertThat(found)
                .extracting(course -> course.uuid().value())
                .containsExactlyInAnyOrder("course-1", "course-3");
    }

}
