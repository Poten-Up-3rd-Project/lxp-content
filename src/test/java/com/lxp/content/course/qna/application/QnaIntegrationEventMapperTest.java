package com.lxp.content.course.qna.application;

import com.lxp.common.application.event.IntegrationEvent;
import com.lxp.content.course.qna.application.event.integration.QnaCreatedIntegrationEvent;
import com.lxp.content.course.qna.application.event.mapper.QnaIntegrationEventMapper;
import com.lxp.content.course.qna.application.port.out.ReadCourseStructurePort;
import com.lxp.content.course.qna.domain.event.QnaCreatedEvent;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class QnaIntegrationEventMapperTest {

    @Test
    void maps_QnaCreatedEvent_to_QnaCreatedIntegrationEvent_with_titles() {
        // given
        ReadCourseStructurePort read = mock(ReadCourseStructurePort.class);
        when(read.titlesOf("c", "s", "l")).thenReturn(new ReadCourseStructurePort.Titles("CT", "ST", "LT"));
        QnaIntegrationEventMapper mapper = new QnaIntegrationEventMapper(read);
        QnaCreatedEvent domainEvent = new QnaCreatedEvent("q", "c", "s", "l", "a", "T", "B");

        // when
        IntegrationEvent integration = mapper.toIntegrationEvent(domainEvent);

        // then
        assertThat(integration).isInstanceOf(QnaCreatedIntegrationEvent.class);
        QnaCreatedIntegrationEvent e = (QnaCreatedIntegrationEvent) integration;
        assertThat(e.getEventType()).isEqualTo("qna.created");
        assertThat(e.getSource()).isEqualTo("lxp.course.qna.service");
        assertThat(e.getPayload().course().title()).isEqualTo("CT");
        assertThat(e.getPayload().section().title()).isEqualTo("ST");
        assertThat(e.getPayload().lecture().title()).isEqualTo("LT");
        assertThat(e.getPayload().qna().id()).isEqualTo("q");
    }
}
