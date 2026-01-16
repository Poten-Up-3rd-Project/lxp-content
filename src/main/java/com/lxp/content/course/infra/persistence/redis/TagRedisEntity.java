package com.lxp.content.course.infra.persistence.redis;

import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.redis.core.RedisHash;

@Getter
@RedisHash("tag")
@NoArgsConstructor
@AllArgsConstructor
public class TagRedisEntity {
    @Id
    long tagId;
    String name;
    String state; // ACTIVE, INACTIVE
    String color;
    String variant;
    private String category;
    private String subCategory;


    public TagRedisEntity(long id, String name,String state,String color,String variant) {
        this.tagId = id;
        this.name = name;
        this.state = state;
        this.color = color;
        this.variant = variant;
    }

}
