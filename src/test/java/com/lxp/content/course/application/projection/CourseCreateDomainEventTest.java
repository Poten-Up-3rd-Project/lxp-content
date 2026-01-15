package com.lxp.content.course.application.projection;

import com.lxp.common.domain.pagination.Page;
import com.lxp.common.domain.pagination.PageRequest;
import com.lxp.content.course.application.port.provider.command.CourseCreateCommand;
import com.lxp.content.course.application.port.provider.usecase.command.CourseCreateUseCase;
import com.lxp.content.course.application.port.provider.view.CourseDetailView;
import com.lxp.content.course.application.port.required.TagQueryPort;
import com.lxp.content.course.application.port.required.UserQueryPort;
import com.lxp.content.course.application.port.required.dto.InstructorResult;
import com.lxp.content.course.application.port.required.dto.TagResult;
import com.lxp.content.course.application.projection.repository.CourseReadRepository;
import com.lxp.content.course.domain.model.enums.Level;
import com.lxp.content.course.application.port.required.EventProducer;
import com.lxp.content.course.infra.persistence.mysql.read.repository.CourseReadJpaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;
import static org.mockito.Mockito.when;

@SpringBootTest
public class CourseCreateDomainEventTest {
    @Autowired
    private CourseCreateUseCase courseCreateUseCase;

    @Autowired
    private CourseReadRepository courseReadRepository;

    @Autowired
    private CourseReadJpaRepository courseJpaRepository;

    @MockitoBean
    private UserQueryPort userQueryPort;

    @MockitoBean
    private TagQueryPort tagQueryPort;


    @MockitoBean
    private EventProducer eventProducer;

    @BeforeEach
    void setUp() {

        // Mock 설정
        when(userQueryPort.getInstructorInfo("instructor-1"))
                .thenReturn(new InstructorResult(
                        "instructor-1",
                        "홍길동",
                        "INSTRUCTOR"
                ));

        when(tagQueryPort.findTagByIds(List.of(1L, 2L)))
                .thenReturn(List.of(
                        new TagResult(1L, "Java", "#FF0000", "solid"),
                        new TagResult(2L, "Backend", "#00FF00", "outline")
                ));

        courseJpaRepository.deleteAll();
    }

    @Test
    @DisplayName("Course 생성 시 ReadModel이 생성된다")
    void createCourse_createsReadModel() {
        // given
        CourseCreateCommand command = createDefaultCommand();
        courseCreateUseCase.execute(command);

        // then - 이벤트 발행 후 ReadModel 생성 확인
        await().atMost(3, TimeUnit.SECONDS).untilAsserted(() -> {
            Page<CourseReadModel> readModels = courseReadRepository.findAll(PageRequest.of(0, 10));

            assertThat(readModels.content()).hasSize(1);

            CourseReadModel readModel = readModels.content().get(0);
            assertThat(readModel.title()).isEqualTo("Java 기초");
            assertThat(readModel.instructorName()).isEqualTo("홍길동");
            assertThat(readModel.tags()).hasSize(2);
        });
    }


    @Test
    @DisplayName("Course 생성 시 반환된 View와 ReadModel이 일치한다")
    void createCourse_viewMatchesReadModel() {
        // given
        CourseCreateCommand command = createDefaultCommand();

        // when
        CourseDetailView result = courseCreateUseCase.execute(command);

        // then
        await().atMost(5, TimeUnit.SECONDS).untilAsserted(() -> {
            Optional<CourseReadModel> readModel = courseReadRepository.search(
                    "Java",
                    PageRequest.of(0, 10)
            ).content().stream().findFirst();

            assertThat(readModel).isPresent();
            assertThat(readModel.get().uuid()).isEqualTo(result.courseId());
            assertThat(readModel.get().title()).isEqualTo(result.title());
        });
    }

    private CourseCreateCommand createDefaultCommand() {
        return new CourseCreateCommand(
                "instructor-1",
                "Java 기초",
                "자바 기초 강의입니다",
                "thumbnail.png",
                Level.JUNIOR,
                List.of(1L, 2L),
                List.of(
                        new CourseCreateCommand.SectionCreateCommand(
                                "1장. 소개",
                                List.of(
                                        new CourseCreateCommand.SectionCreateCommand.LectureCreateCommand(
                                                "1-1. 강의 소개",
                                                "http://video.com/1"
                                        )
                                )
                        )
                )
        );
    }
}
