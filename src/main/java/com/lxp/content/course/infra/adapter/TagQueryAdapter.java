package com.lxp.content.course.infra.adapter;

import com.lxp.content.course.application.port.required.TagQueryPort;
import com.lxp.content.course.application.port.required.dto.TagResult;
import com.lxp.content.course.infra.persistence.redis.TagRedisRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class TagQueryAdapter implements TagQueryPort {

    private final TagRedisRepository tagRedisRepository;

    @Override
    public Long findTagByName(String name) {
        return tagRedisRepository.findTagsIdsByName(name)
                .orElseThrow(() -> new IllegalArgumentException("Tag not found: " + name));
    }

    @Override
    public List<TagResult> findTagByIds(List<Long> tagId) {
        return tagRedisRepository.findByIds(tagId)
                .stream().map(it ->
                        new TagResult(it.getTagId(), it.getName(), it.getColor(), it.getVariant())
                ).toList();

    }

    @Override
    public List<Long> findTagsIdsByNameIn(String tagName) {
        return tagRedisRepository.searchIdsByNameContaining(tagName);
    }
}
