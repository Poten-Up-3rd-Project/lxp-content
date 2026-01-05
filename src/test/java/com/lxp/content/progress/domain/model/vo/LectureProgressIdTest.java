package com.lxp.content.progress.domain.model.vo;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.regex.Pattern;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("LectureProgressId 단위 테스트")
class LectureProgressIdTest {

    /*
     * UUID 정규식 패턴
     */
    private final static Pattern UUID_REGEX =
            Pattern.compile("^[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}$");

    @Test
    @DisplayName("TC-LPI-001: LectureProgressId 생성 시 올바른 UUID 값을 가진다")
    void shouldCorrectUUIDValue_WhenCreatingLectureProgressId() {
        // given, when
        LectureProgressId lectureProgressId = LectureProgressId.create();

        // then
        assertTrue(UUID_REGEX.matcher(lectureProgressId.value()).matches(),
                "LectureProgressId의 값은 올바른 UUID 형식이어야 합니다.");
    }

}