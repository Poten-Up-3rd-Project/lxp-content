package com.lxp.content.course.application.service;

import com.lxp.content.course.application.port.provider.query.CourseFilterQuery;
import com.lxp.content.course.application.port.provider.view.CourseView;
import com.lxp.content.course.application.service.query.internal.CourseFilterService;
import com.lxp.content.course.domain.model.enums.Level;
import com.lxp.content.course.infra.persistence.mysql.read.entity.CourseReadJpaEntity;
import com.lxp.content.course.infra.persistence.mysql.read.repository.CourseReadJpaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

@SpringBootTest
public class CourseFilterServiceTest {

    @Autowired
    private CourseFilterService courseFilterService;

    // 조회가 일어나는 레포지토리를 주입받습니다.
    @Autowired
    private CourseReadJpaRepository courseReadJpaRepository;

    @BeforeEach
    void setUp() {
        // Read용 테이블 초기화
        courseReadJpaRepository.deleteAll();

        // 테스트 데이터 생성 (CourseReadJpaEntity 저장)
        saveReadModel("uuid-1", "JUNIOR");
        saveReadModel("uuid-2", "MIDDLE");
        saveReadModel("uuid-3", "JUNIOR");
    }

    private void saveReadModel(String uuid, String difficulty) {
        CourseReadJpaEntity entity = CourseReadJpaEntity.builder()
                .uuid(uuid)
                .instructorId("instructor-1")
                .instructorName("홍길동")
                .title("강의-" + uuid)
                .difficulty(difficulty)
                .tags(List.of()) // 필요시 Tag 객체 생성하여 삽입
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        courseReadJpaRepository.save(entity);
    }

    @Test
    @DisplayName("난이도 필터링이 정상적으로 동작해야 한다")
    void should_filter_courses_by_difficulty() {
        CourseFilterQuery query = new CourseFilterQuery(null, List.of("JUNIOR"), 10);

        List<CourseView> result = courseFilterService.execute(query);

        assertThat(result).hasSize(2);
        assertThat(result).allMatch(v -> v.level() == Level.JUNIOR);
    }

    @Test
    @DisplayName("특정 UUID 리스트로 필터링이 정상적으로 동작해야 한다")
    void should_filter_courses_by_ids() {
        CourseFilterQuery query = new CourseFilterQuery(List.of("uuid-1", "uuid-2"), null, 10);

        List<CourseView> result = courseFilterService.execute(query);

        assertThat(result).hasSize(2);
        assertThat(result).extracting(CourseView::courseId)
                .containsExactlyInAnyOrder("uuid-1", "uuid-2");
    }

    @Test
    @DisplayName("limit(count) 설정이 정상적으로 적용되어야 한다")
    void should_respect_limit_count() {
        CourseFilterQuery query = new CourseFilterQuery(null, null, 1);

        List<CourseView> result = courseFilterService.execute(query);

        assertThat(result).hasSize(1);
    }
}