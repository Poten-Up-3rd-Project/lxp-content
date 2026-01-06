package com.lxp.content.config;

import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@Configuration
@EnableJpaAuditing
@EnableJpaRepositories(basePackages = {
        "com.lxp.content",
        "com.lxp.common.infrastructure.persistence"
})
@EntityScan(basePackages = {
        "com.lxp.content",
        "com.lxp.common.infrastructure.persistence"
})
public class JpaConfig {
}
