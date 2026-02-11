package com.lxp.content.course.qna.domain;

import com.lxp.content.course.domain.event.CrudEvent;
import com.lxp.content.course.qna.domain.event.QnaCreatedEvent;
import com.lxp.content.course.qna.domain.model.Qna;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class QnaDomainTest {

    @Test
    void create_emits_QnaCreatedEvent() {
        // when
        Qna.Result result = Qna.create(
            "course-uuid",
            "section-uuid",
            "lecture-uuid",
            "author-1",
            "title",
            "content"
        );

        // then
        assertThat(result.qna()).isNotNull();
        assertThat(result.qna().getId()).isNotBlank();
        CrudEvent event = result.domainEvent();
        assertThat(event).isInstanceOf(QnaCreatedEvent.class);
        QnaCreatedEvent e = (QnaCreatedEvent) event;
        assertThat(e.getAggregateId()).isEqualTo(result.qna().getId());
        assertThat(e.getCourseUuid()).isEqualTo("course-uuid");
        assertThat(e.getSectionUuid()).isEqualTo("section-uuid");
        assertThat(e.getLectureUuid()).isEqualTo("lecture-uuid");
        assertThat(e.getAuthorId()).isEqualTo("author-1");
        assertThat(e.getTitle()).isEqualTo("title");
        assertThat(e.getContent()).isEqualTo("content");
        assertThat(e.getEventId()).isNotBlank();
        assertThat(e.getOccurredAt()).isNotNull();
    }
}
