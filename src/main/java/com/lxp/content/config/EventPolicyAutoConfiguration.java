package com.lxp.content.config;

import com.lxp.common.application.event.policy.EventPolicyContributor;
import com.lxp.common.application.event.policy.EventPolicyRegistry;
import com.lxp.common.application.event.policy.EventPublishPolicy;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class EventPolicyAutoConfiguration {

    @Bean
    public EventPolicyRegistry eventPolicyRegistry(
            List<EventPolicyContributor> contributors
    ) {
        EventPolicyRegistry.Builder builder = EventPolicyRegistry.builder();

        for (EventPolicyContributor contributor : contributors) {
            contributor.contribute(builder);
        }

        return builder
                .defaultPolicy(EventPublishPolicy.bestEffort())
                .build();
    }
}