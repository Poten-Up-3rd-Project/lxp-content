package com.lxp.content.course.application.event.policy;

import com.lxp.content.course.domain.event.CourseCreatedEvent;
import com.lxp.content.course.domain.event.CrudEvent;
import org.springframework.stereotype.Component;

@Component
public class DeliveryPolicyResolver {

    public DeliveryPolicy resolve(CrudEvent event) {
        if (event instanceof CourseCreatedEvent) {
            return DeliveryPolicy.OUTBOX_REQUIRED;
        }

        return DeliveryPolicy.FIRE_AND_FORGET;
    }
}