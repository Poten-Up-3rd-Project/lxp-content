package com.lxp.content.course.application.event;

import com.lxp.common.application.event.policy.EventPolicyContributor;
import com.lxp.common.application.event.policy.EventPolicyRegistry;
import com.lxp.common.application.event.policy.EventPublishPolicy;
import com.lxp.common.application.event.policy.delivery.AtLeastOnce;
import com.lxp.common.application.event.policy.failure.RetryThenDlq;
import com.lxp.common.application.event.policy.ordering.Parallel;
import com.lxp.common.application.event.policy.priority.NormalPriority;
import com.lxp.content.course.domain.event.CourseCreatedEvent;
import org.springframework.stereotype.Component;

import java.time.Duration;

@Component
public class CourseEventPolicyContributor implements EventPolicyContributor {
    @Override
    public void contribute(EventPolicyRegistry.Builder builder) {
        builder
                .register(CourseCreatedEvent.class, new EventPublishPolicy(
                        new AtLeastOnce(),
                        new NormalPriority(),
                        new Parallel(),
                        new RetryThenDlq(3, Duration.ofSeconds(5))
                        )
                );
    }
}
