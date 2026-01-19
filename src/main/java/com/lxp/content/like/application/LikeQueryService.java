package com.lxp.content.like.application;

import com.lxp.content.like.domain.model.Like;
import com.lxp.content.like.repository.LikeRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class LikeQueryService {

    private LikeRepository likeRepository;

    public LikeQueryService(LikeRepository likeRepository) {
        this.likeRepository = likeRepository;
    }

    public Map<String, Boolean> isLikedMyMe(UUID userId, Set<UUID> courseIds) {

        if (courseIds == null || courseIds.isEmpty()) {
            return java.util.Collections.emptyMap();
        }

        List<Like> myLikes = likeRepository.findAllByUserId(userId);
        Set<UUID> courseIdsLikedByMe = myLikes.stream()
                .map(Like::courseId)
                .collect(Collectors.toSet());

        return courseIds.stream()
                .collect(Collectors.toMap(
                        UUID::toString,
                        courseIdsLikedByMe::contains
                ));
    }
}
