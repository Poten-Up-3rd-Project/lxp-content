package com.lxp.content.course.infra.persistence.redis;

import jakarta.persistence.Id;
import lombok.Getter;
import org.springframework.data.redis.core.RedisHash;

@Getter
@RedisHash("tag")
public class TagRedisEntity {
    @Id
    long tagId;
    String name;
    String state; // ACTIVE, INACTIVE
    String color;
    String variant;


    public TagRedisEntity(long id, String name,String state,String color,String variant) {
        this.tagId = id;
        this.name = name;
        this.state = state;
        this.color = color;
        this.variant = variant;
    }

}
