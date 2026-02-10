package com.lxp.content.course.qna.domain.event;

import com.lxp.common.domain.event.BaseDomainEvent;
import com.lxp.content.course.domain.event.CrudEvent;
import lombok.Getter;

@Getter
public class QnaCreatedEvent extends BaseDomainEvent implements CrudEvent {

    private final String courseUuid;
    private final String sectionUuid;
    private final String lectureUuid;
    private final String authorId;
    private final String title;
    private final String content;

    public QnaCreatedEvent(String qnaId, String courseUuid, String sectionUuid, String lectureUuid, String authorId, String title, String content) {
        super(qnaId);
        this.courseUuid = courseUuid;
        this.sectionUuid = sectionUuid;
        this.lectureUuid = lectureUuid;
        this.authorId = authorId;
        this.title = title;
        this.content = content;
    }

    @Override
    public CrudType getCrudType() {
        return CrudType.CREATED;
    }
}
