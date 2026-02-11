package com.lxp.content.course.qna.domain.model;

import com.lxp.content.course.domain.event.CrudEvent;
import com.lxp.content.course.qna.domain.event.QnaCreatedEvent;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Qna {

    private String id; // business id (UUID string)
    private String courseUuid;
    private String sectionUuid;
    private String lectureUuid;
    private String authorId;
    private String title;
    private String content;
    private LocalDateTime createdAt;

    private Qna(String id, String courseUuid, String sectionUuid, String lectureUuid, String authorId, String title, String content, LocalDateTime createdAt) {
        this.id = id;
        this.courseUuid = courseUuid;
        this.sectionUuid = sectionUuid;
        this.lectureUuid = lectureUuid;
        this.authorId = authorId;
        this.title = title;
        this.content = content;
        this.createdAt = createdAt;
    }

    public static Result create(String courseUuid, String sectionUuid, String lectureUuid, String authorId, String title, String content) {
        Objects.requireNonNull(courseUuid, "courseUuid is required");
        Objects.requireNonNull(sectionUuid, "sectionUuid is required");
        Objects.requireNonNull(lectureUuid, "lectureUuid is required");
        Objects.requireNonNull(authorId, "authorId is required");
        if (title == null || title.isBlank()) throw new IllegalArgumentException("title is required");
        if (content == null || content.isBlank()) throw new IllegalArgumentException("content is required");

        String id = UUID.randomUUID().toString();
        LocalDateTime now = LocalDateTime.now();
        Qna qna = new Qna(id, courseUuid, sectionUuid, lectureUuid, authorId, title, content, now);

        QnaCreatedEvent event = new QnaCreatedEvent(
                id,
                courseUuid,
                sectionUuid,
                lectureUuid,
                authorId,
                title,
                content
        );
        return new Result(qna, event);
    }

    public record Result(Qna qna, CrudEvent domainEvent) {}
}