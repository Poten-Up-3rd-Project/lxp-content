package com.lxp.content.course.qna.application.event.integration.payload;

import java.time.LocalDateTime;

public record QnaCreatedPayload(
    Course course,
    Section section,
    Lecture lecture,
    Qna qna
) {
    public record Course(String uuid, String title) {
    }

    public record Section(String uuid, String title) {
    }

    public record Lecture(String uuid, String title) {
    }

    public record Qna(String id, String authorId, String title, String content, LocalDateTime createdAt) {
    }
}
