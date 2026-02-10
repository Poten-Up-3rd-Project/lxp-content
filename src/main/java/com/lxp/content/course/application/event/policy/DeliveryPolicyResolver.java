package com.lxp.content.course.application.event.policy;

import com.lxp.content.course.domain.event.CrudEvent;
import com.lxp.content.course.qna.domain.event.QnaCreatedEvent;
import org.springframework.stereotype.Component;

@Component
public class DeliveryPolicyResolver {

    public DeliveryPolicy resolve(CrudEvent event) {
        if (event instanceof QnaCreatedEvent) {
            return DeliveryPolicy.OUTBOX_REQUIRED;
        }
        return DeliveryPolicy.FIRE_AND_FORGET;
    }
}
