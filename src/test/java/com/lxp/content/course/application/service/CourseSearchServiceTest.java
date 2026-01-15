package com.lxp.content.course.application.service;

import com.lxp.common.domain.pagination.Page;
import com.lxp.common.domain.pagination.PageRequest;
import com.lxp.content.course.application.port.provider.query.CourseSearchQuery;
import com.lxp.content.course.application.port.provider.usecase.query.CourseSearchUseCase;
import com.lxp.content.course.application.port.provider.view.CourseView;
import com.lxp.content.course.infra.persistence.mysql.read.entity.CourseReadJpaEntity;
import com.lxp.content.course.infra.persistence.mysql.read.repository.CourseReadJpaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;


@SpringBootTest
class CourseSearchServiceTest {

    @Autowired
    private CourseSearchUseCase courseSearchUseCase;

    @Autowired
    private CourseReadJpaRepository courseReadRepository;

    @BeforeEach
    void setUp() {
        courseReadRepository.deleteAll();

        // CourseReadJpaEntity의 필드명에 맞춰 데이터 생성
        List<CourseReadJpaEntity> entities = List.of(
                createEntity("course-1", "Spring Boot 마스터", "스프링 부트 완전 정복", "JUNIOR"),
                createEntity("course-2", "Spring Security 입문", "보안 기초부터", "JUNIOR"),
                createEntity("course-3", "JPA 심화", "JPA 성능 최적화", "MIDDLE"),
                createEntity("course-4", "Kotlin 기초", "코틀린 시작하기", "JUNIOR"),
                createEntity("course-5", "React 입문", "리액트 기초", "JUNIOR")
        );

        courseReadRepository.saveAll(entities);
        courseReadRepository.flush();
    }

    @Nested
    @DisplayName("키워드 검색")
    class KeywordSearch {

        @Test
        @DisplayName("키워드 'Spring'으로 검색 시 관련 강좌 2개가 반환되어야 한다")
        void searchByKeyword() {
            // given
            PageRequest pageRequest = PageRequest.of(0, 10);
            CourseSearchQuery query = new CourseSearchQuery("Spring", pageRequest);

            // when
            Page<CourseView> result = courseSearchUseCase.execute(query);

            // then
            assertThat(result.content()).hasSize(2);
            assertThat(result.content())
                    .extracting(CourseView::title)
                    .allMatch(title -> title.contains("Spring"));
        }
    }

    private CourseReadJpaEntity createEntity(String id, String title, String description, String level) {
        return CourseReadJpaEntity.builder()
                .uuid(id)                           // @Id 필드
                .instructorId("instructor-1")
                .instructorName("김강사")
                .title(title)
                .description(description)
                .difficulty(level)                  // 필드명: difficulty
                .thumbnail("thumbnail.jpg")         // 필드명: thumbnail
                .tags(List.of())                    // JSON 필드 (빈 리스트)
                .tagSearchText("")                  // 필드명: tagSearchText
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }
}