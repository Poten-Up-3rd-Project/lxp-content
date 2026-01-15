package com.lxp.content.course.application.service;

import com.lxp.content.course.application.port.provider.command.CourseCreateCommand;
import com.lxp.content.course.application.port.provider.view.CourseDetailView;
import com.lxp.content.course.application.port.required.TagQueryPort;
import com.lxp.content.course.application.port.required.UserQueryPort;
import com.lxp.content.course.application.port.required.dto.InstructorResult;
import com.lxp.content.course.application.port.required.dto.TagResult;
import com.lxp.content.course.application.service.command.CourseCreateService;
import com.lxp.content.course.domain.model.enums.Level;
import com.lxp.content.course.infra.persistence.mysql.read.repository.CourseReadJpaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.util.List;

import static org.mockito.Mockito.when;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
public class CourseCreateServiceTest {

    @Autowired
    private CourseCreateService courseCreateService;

    @MockitoBean
    private UserQueryPort userQueryPort;

    @MockitoBean
    private TagQueryPort tagQueryPort;

    @Autowired
    private CourseReadJpaRepository courseJpaRepository;


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
    public void testCreateCourse() {
        CourseCreateCommand command = createDefaultCommand();

        CourseDetailView result = courseCreateService.execute(command);

        assertThat(result).isNotNull();
        assertThat(result.title()).isEqualTo("Java 기초");
        assertThat(result.Instructor().name()).isEqualTo("홍길동");
        assertThat(result.tags()).hasSize(2);
        assertThat(result.sections()).hasSize(1);
        assertThat(result.sections().get(0).title()).isEqualTo("1장. 소개");
        assertThat(result.sections().get(0).lectures()).hasSize(1);
        assertThat(result.sections().get(0).lectures().get(0).title()).isEqualTo("1-1. 강의 소개");
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
