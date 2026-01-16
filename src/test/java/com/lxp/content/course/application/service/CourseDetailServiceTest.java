package com.lxp.content.course.application.service;

import com.lxp.content.course.application.port.provider.query.CourseDetailQuery;
import com.lxp.content.course.application.port.provider.view.CourseDetailView;
import com.lxp.content.course.application.port.required.TagQueryPort;
import com.lxp.content.course.application.port.required.UserQueryPort;
import com.lxp.content.course.application.port.required.dto.InstructorResult;
import com.lxp.content.course.application.port.required.dto.TagResult;
import com.lxp.content.course.application.service.query.external.CourseDetailService;
import com.lxp.content.course.domain.model.enums.Level;
import com.lxp.content.course.domain.repository.CourseRepository;
import com.lxp.content.course.infra.persistence.mysql.read.entity.CourseReadJpaEntity;
import com.lxp.content.course.infra.persistence.mysql.read.repository.CourseReadJpaRepository;
import com.lxp.content.course.infra.persistence.mysql.write.entity.CourseJpaEntity;
import com.lxp.content.course.infra.persistence.mysql.write.repository.CourseJpaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.util.List;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.when;

@SpringBootTest
public class CourseDetailServiceTest {
    @Autowired
    private CourseDetailService courseDetailService;

    @Autowired
    private CourseJpaRepository courseRepository;

    @MockitoBean
    private UserQueryPort userQueryPort;

    @MockitoBean
    private TagQueryPort tagQueryPort;


    private final String COURSE_UUID = "test-course-uuid";
    private final String INSTRUCTOR_UUID = "instructor-1";
    private final List<Long> TAG_IDS = List.of(1L, 2L);


    @BeforeEach
    void setUp() {
        courseRepository.deleteAll();

        // 1. Mock 설정 (외부 서비스 호출 시뮬레이션)
        when(userQueryPort.getInstructorInfo(INSTRUCTOR_UUID))
                .thenReturn(new InstructorResult(
                        INSTRUCTOR_UUID,
                        "홍길동",
                        "INSTRUCTOR"
                ));

        when(tagQueryPort.findTagByIds(TAG_IDS))
                .thenReturn(List.of(
                        new TagResult(1L, "Java", "#FF0000", "solid"),
                        new TagResult(2L, "Backend", "#00FF00", "outline")
                ));


        CourseJpaEntity entity = CourseJpaEntity.builder()
                .uuid(COURSE_UUID)
                .instructorUUID(INSTRUCTOR_UUID)
                .title("테스트 강의")
                .description("설명")
                .difficulty(Level.JUNIOR)
                .tags(TAG_IDS)
                .build();

        courseRepository.save(entity);
    }

    @Test
    @DisplayName("코스 상세 정보가 정상적으로 조회되어야 한다")
    void should_return_course_detail_view_successfully() {
        // Given
        CourseDetailQuery query = new CourseDetailQuery(COURSE_UUID);

        // When
        CourseDetailView result = courseDetailService.execute(query);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.title()).isEqualTo("테스트 강의");

        assertThat(result.Instructor()).isNotNull();
        assertThat(result.Instructor().name()).isEqualTo("홍길동");
        assertThat(result.Instructor().instructorId()).isEqualTo(INSTRUCTOR_UUID);

        assertThat(result.tags()).isNotNull();
        assertThat(result.tags().size()).isEqualTo(2);

        Mockito.verify(userQueryPort, times(1)).getInstructorInfo(INSTRUCTOR_UUID);
        Mockito.verify(tagQueryPort, times(1)).findTagByIds(TAG_IDS);
    }
}
