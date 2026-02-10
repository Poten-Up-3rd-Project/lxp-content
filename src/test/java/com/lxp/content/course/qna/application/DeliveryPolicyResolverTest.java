package com.lxp.content.course.qna.application;

import com.lxp.content.course.application.event.policy.DeliveryPolicy;
import com.lxp.content.course.application.event.policy.DeliveryPolicyResolver;
import com.lxp.content.course.qna.domain.event.QnaCreatedEvent;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class DeliveryPolicyResolverTest {

    @Test
    void qna_created_is_outbox_required() {
        DeliveryPolicyResolver resolver = new DeliveryPolicyResolver();
        DeliveryPolicy policy = resolver.resolve(new QnaCreatedEvent(
            "q", "c", "s", "l", "a", "t", "b"
        ));
        assertThat(policy).isEqualTo(DeliveryPolicy.OUTBOX_REQUIRED);
    }
}
