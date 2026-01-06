package com.lxp.content.course.infra.persistence.read.adapter;


import com.lxp.common.domain.pagination.Page;
import com.lxp.common.domain.pagination.PageRequest;
import com.lxp.common.domain.pagination.Sort;
import com.lxp.content.course.application.projection.CourseReadModel;
import com.lxp.content.course.domain.model.enums.Level;
import com.lxp.content.course.infra.persistence.read.entity.CourseReadJpaEntity;
import com.lxp.content.course.infra.persistence.read.mapper.CourseReadEntityMapper;
import com.lxp.content.course.infra.persistence.read.repository.CourseReadJpaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;


import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@EnableJpaRepositories(
        basePackages = "com.lxp.content.course.infra.persistence.read"
)
@EntityScan(
        basePackages = "com.lxp.content.course.infra.persistence.read"
)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@EnableJpaAuditing
public class CourseReadPersistenceAdapterTest {

    @Autowired
    private CourseReadJpaRepository jpaRepository;

    private final CourseReadEntityMapper mapper = new CourseReadEntityMapper();

    private CourseReadPersistenceAdapter adapter;

    @BeforeEach
    void setUp() {
        adapter = new CourseReadPersistenceAdapter(jpaRepository, mapper);
        jpaRepository.deleteAll();
    }

    @Test
    @DisplayName("CourseReadModel 저장")
    void save() {
        // given
        List<CourseReadModel.TagReadModel> tags = List.of(
                new CourseReadModel.TagReadModel(1L, "Java", "#FF0000", "solid"),
                new CourseReadModel.TagReadModel(2L, "Backend", "#00FF00", "outline")
        );
        CourseReadModel model = createReadModelWithTags("course-1", "Java 기초",tags);

        // when
        adapter.save(model);

        // then
        Optional<CourseReadJpaEntity> found = jpaRepository.findById("course-1");
        found.orElseThrow(() -> new RuntimeException("ReadCourse not found"));
        assertThat(found).isPresent();
        assertThat(found.get().getTitle()).isEqualTo("Java 기초");
    }


    @Test
    @DisplayName("전체 조회 - 페이징")
    void findAll() {
        // given
        adapter.save(createReadModel("course-1", "Java 기초"));
        adapter.save(createReadModel("course-2", "Spring 입문"));
        adapter.save(createReadModel("course-3", "JPA 마스터"));

        PageRequest pageRequest = new PageRequest(0,2,null);

        // when
        Page<CourseReadModel> result = adapter.findAll(pageRequest);

        // then
        assertThat(result.content()).hasSize(2);
        assertThat(result.totalElements()).isEqualTo(3);
        assertThat(result.totalPages()).isEqualTo(2);
    }
    @Test
    @DisplayName("키워드 검색 - 제목")
    void searchByTitle() {
        // given

        List<CourseReadModel.TagReadModel> tags1 = List.of(
                new CourseReadModel.TagReadModel(1L, "Java", "#FF0000", "solid"),
                new CourseReadModel.TagReadModel(2L, "Backend", "#00FF00", "outline")
        );
        List<CourseReadModel.TagReadModel> tags2 = List.of(
                new CourseReadModel.TagReadModel(3L, "React", "#FF0000", "solid"),
                new CourseReadModel.TagReadModel(4L, "Front", "#00FF00", "outline")
        );

        List<CourseReadModel.TagReadModel> tags3 = List.of(
                new CourseReadModel.TagReadModel(5L, "AI", "#FF0000", "solid"),
                new CourseReadModel.TagReadModel(2L, "Backend", "#00FF00", "outline")

        );

        CourseReadModel model = createReadModelWithTags("course-1", "Java 기초",tags1);
        CourseReadModel model2 = createReadModelWithTags("course-2", "React 기초",tags2);
        CourseReadModel mode3 = createReadModelWithTags("course-3", "머신러닝",tags3);

        adapter.save(model);
        adapter.save(model2);
        adapter.save(mode3);

        PageRequest pageRequest = new PageRequest(0, 10,null);

        // when
        Page<CourseReadModel> result = adapter.search("Backend", pageRequest);

        // then
        assertThat(result.content()).hasSize(2);
        assertThat(result.content())
                .extracting(CourseReadModel::title)
                .containsExactlyInAnyOrder("Java 기초", "머신러닝");
    }

    @Test
    @DisplayName("키워드 검색 - 설명")
    void searchByDescription() {
        // given
        adapter.save(createReadModel("course-1", "백엔드 개발", "Java와 Spring을 배웁니다"));
        adapter.save(createReadModel("course-2", "프론트엔드 개발", "React를 배웁니다"));

        PageRequest pageRequest = new PageRequest(0, 10, null);

        // when
        Page<CourseReadModel> result = adapter.search("Spring", pageRequest);

        // then
        assertThat(result.content()).hasSize(1);
        assertThat(result.content().get(0).title()).isEqualTo("백엔드 개발");
    }

    @Test
    @DisplayName("키워드 검색 - 대소문자 구분 없음")
    void searchCaseInsensitive() {
        // given
        adapter.save(createReadModel("course-1", "JAVA 기초"));
        adapter.save(createReadModel("course-2", "java 심화"));

        PageRequest pageRequest = new PageRequest(0, 10,null);

        // when
        Page<CourseReadModel> result = adapter.search("java", pageRequest);

        // then
        assertThat(result.content()).hasSize(2);
    }

    @Test
    @DisplayName("키워드 검색 - 결과 없음")
    void searchNoResult() {
        // given
        adapter.save(createReadModel("course-1", "Java 기초"));

        PageRequest pageRequest = new PageRequest(0, 10, null);

        // when
        Page<CourseReadModel> result = adapter.search("Python", pageRequest);

        // then
        assertThat(result.content()).isEmpty();
        assertThat(result.totalElements()).isEqualTo(0);
    }


    @Test
    @DisplayName("키워드 검색 - tag 포함")
    void searchTag() {
        // given
        adapter.save(createReadModel("course-1", "JAVA 기초"));
        adapter.save(createReadModel("course-2", "java 심화"));

        PageRequest pageRequest = new PageRequest(0, 10,null);

        // when
        Page<CourseReadModel> result = adapter.search("java", pageRequest);

        // then
        assertThat(result.content()).hasSize(2);
    }

    @Test
    @DisplayName("전체 조회 - createdAt 내림차순 정렬")
    void findAllSortedByCreatedAtDesc() {
        // given
        LocalDateTime now = LocalDateTime.now();
        adapter.save(createReadModelWithCreatedAt("course-1", "Java 기초", now.minusDays(2)));
        adapter.save(createReadModelWithCreatedAt("course-2", "Spring 입문", now.minusDays(1)));
        adapter.save(createReadModelWithCreatedAt("course-3", "JPA 마스터", now));

        Sort sort = Sort.by(Sort.Direction.DESC, "createdAt");
        PageRequest pageRequest = PageRequest.of(0, 10, sort);

        // when
        Page<CourseReadModel> result = adapter.findAll(pageRequest);

        // then
        assertThat(result.content()).hasSize(3);
        assertThat(result.content())
                .extracting(CourseReadModel::title)
                .containsExactly("JPA 마스터", "Spring 입문", "Java 기초");
    }



    @Test
    @DisplayName("전체 조회 - createdAt 오름차순 정렬")
    void findAllSortedByCreatedAtAsc() {
        // given
        LocalDateTime now = LocalDateTime.now();
        adapter.save(createReadModelWithCreatedAt("course-1", "Java 기초", now.minusDays(2)));
        adapter.save(createReadModelWithCreatedAt("course-2", "Spring 입문", now.minusDays(1)));
        adapter.save(createReadModelWithCreatedAt("course-3", "JPA 마스터", now));

        Sort sort = Sort.by(Sort.Direction.ASC, "createdAt");
        PageRequest pageRequest = PageRequest.of(0, 10, sort);

        // when
        Page<CourseReadModel> result = adapter.findAll(pageRequest);

        // then
        assertThat(result.content()).hasSize(3);
        assertThat(result.content())
                .extracting(CourseReadModel::title)
                .containsExactly("Java 기초", "Spring 입문", "JPA 마스터");
    }

    private CourseReadModel createReadModel(String uuid, String title) {
        return createReadModel(uuid, title, "설명입니다");
    }
    private CourseReadModel createReadModel(String uuid, String title, String description) {
        return new CourseReadModel(
                uuid,
                "instructor-1",
                "홍길동",
                "thumbnail.png",
                title,
                description,
                Level.JUNIOR,
                List.of(),
                LocalDateTime.now(),
                LocalDateTime.now()
        );
    }


    private CourseReadModel createReadModelWithTags(String uuid, String title, List<CourseReadModel.TagReadModel> tags) {
        return new CourseReadModel(
                uuid,
                "instructor-1",
                "홍길동",
                "thumbnail.png",
                title,
                "설명입니다",
                Level.JUNIOR,
                tags,
                LocalDateTime.now(),
                LocalDateTime.now()
        );
    }

    private CourseReadModel createReadModelWithCreatedAt(String uuid, String title, LocalDateTime createdAt) {
        return new CourseReadModel(
                uuid,
                "instructor-1",
                "홍길동",
                "thumbnail.png",
                title,
                "설명입니다",
                Level.JUNIOR,
                List.of(),
                createdAt,
                LocalDateTime.now()
        );
    }



}
