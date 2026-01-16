package com.lxp.content.course.infra.persistence.redis;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.*;

@Slf4j
@Component
@RequiredArgsConstructor
public class TagRedisRepository{
    private static final String TAG_ID_KEY = "tag:id:";
    private static final String TAG_NAME_KEY = "tag:name:";


    private final ObjectMapper objectMapper;
    private final RedisTemplate<String, String> redisTemplate;


    public List<Long> searchIdsByNameContaining(String keyword) {
        Set<String> nameKeys = redisTemplate.keys(TAG_NAME_KEY + "*" + keyword.toLowerCase() + "*");
        if (nameKeys.isEmpty()) {
            return List.of();
        }
        List<String> ids = redisTemplate.opsForValue().multiGet(nameKeys);

        assert ids != null;
        return ids.stream()
                .filter(Objects::nonNull)
                .map(Long::valueOf)
                .toList();
    }


    public List<TagRedisEntity> findByIds(Collection<Long> ids) {
        if (ids == null || ids.isEmpty()) {
            return List.of();
        }

        List<String> keys = ids.stream()
                .map(id -> TAG_ID_KEY + id)
                .toList();

        List<String> cached = redisTemplate.opsForValue().multiGet(keys);

        if (cached == null) {
            return List.of();
        }

        return cached.stream()
                .filter(Objects::nonNull)
                .map(this::toTagRedisEntity)
                .filter(Objects::nonNull)
                .toList();
    }


    public Optional<Long> findTagsIdsByName(String tagName) {
        String key = TAG_NAME_KEY + tagName.toLowerCase();
        String value = redisTemplate.opsForValue().get(key);
        return Optional.ofNullable(value).map(Long::parseLong);
    }

    private TagRedisEntity toTagRedisEntity(String json) {
        try {
            return objectMapper.readValue(json, TagRedisEntity.class);
        } catch (JsonProcessingException e) {
            log.error("[TagCache] 역직렬화 실패 - json={}", json, e);
            return null;
        }
    }
}
