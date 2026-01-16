package com.lxp.content.progress.application.mapper;

import com.lxp.content.progress.application.port.in.command.UpdateProgressCommand;
import com.lxp.content.progress.infrastructure.web.external.dto.UpdateProgressRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("ProgressWebMapper 테스트")
class ProgressWebMapperTest {

    private ProgressWebMapper mapper;

    private String userId;
    private String courseId;
    private String lectureId;

    @BeforeEach
    void setUp() {
        mapper = new ProgressWebMapper();
        userId = UUID.randomUUID().toString();
        courseId = UUID.randomUUID().toString();
        lectureId = UUID.randomUUID().toString();
    }

    @Test
    @DisplayName("TC-PWM-001: Web 요청 DTO가 Application Command로 누락 없이 변환되어야 한다")
    void toCommand_success() {
        // given
        Integer lastPlayedTime = 120;
        UpdateProgressRequest request = new UpdateProgressRequest(lastPlayedTime);

        // when
        UpdateProgressCommand command = mapper.toCommand(userId, courseId, lectureId, request);

        // then
        assertNotNull(command, "변환된 Command는 null이 아니어야 한다");
        assertEquals(userId, command.userId(), "userId가 올바르게 매핑되어야 한다");
        assertEquals(courseId, command.courseId(), "courseId가 올바르게 매핑되어야 한다");
        assertEquals(lectureId, command.lectureId(), "lectureId가 올바르게 매핑되어야 한다");
        assertEquals(lastPlayedTime, command.lastPlayedTime(), "lastPlayedTime이 올바르게 매핑되어야 한다");
    }

}