package com.lxp.content.course.infra.adapter;

import com.lxp.content.course.application.port.required.TagQueryPort;
import com.lxp.content.course.application.port.required.dto.TagResult;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class TagQueryAdapter implements TagQueryPort {

    @Override
    public Long findTagByName(String name) {
        return 0L;
    }

    @Override
    public List<TagResult> findTagByIds(List<Long> tagId) {
        return List.of();
    }

    @Override
    public List<Long> findTagsIdsByNameIn(String tagName) {
        return List.of();
    }
}
