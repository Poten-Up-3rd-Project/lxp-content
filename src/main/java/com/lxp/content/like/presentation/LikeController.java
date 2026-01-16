package com.lxp.content.like.presentation;

import com.lxp.content.like.application.LikeCommandService;
import com.lxp.content.like.application.LikeQueryService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api-v1/likes")
public class LikeController {

    private final LikeQueryService likeQueryService;
    private final LikeCommandService likeCommandService;

    public LikeController(
            LikeQueryService likeQueryService,
            LikeCommandService likeCommandService
    ) {
        this.likeQueryService = likeQueryService;
        this.likeCommandService = likeCommandService;
    }
    
    // To Do: 내가 좋아요한 강좌 id 리스트 반환하기 보다는 course 쪽 전달할 때 포함시키는 게 나을 듯
    // course id 목록 전달되었을 때 내가 좋아요 했는 지 여부를 반환하는 서비스 메서드 개발해야 함
    // 근데 변경 소요 클 수도 있으니까 일단은 조회 api 하나 뚫어놓기는 하자
    @GetMapping
    public ResponseEntity<Map<String, Boolean>> isLikedMyMe(
            @AuthenticationPrincipal String userId,
            @RequestParam List<UUID> courseIds
    ) {
        Map<String, Boolean> result = likeQueryService.isLikedMyMe(
                UUID.fromString(userId),
                new HashSet<>(courseIds)
        );

        return ResponseEntity.ok(result);
    }

    @PostMapping
    public ResponseEntity<Void> like(
            @AuthenticationPrincipal String userId,
            @RequestParam UUID courseId
    ) {
        likeCommandService.like(UUID.fromString(userId), courseId);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping
    public ResponseEntity<Void> unlike(
            @AuthenticationPrincipal String userId,
            @RequestParam UUID courseId
    ) {
        likeCommandService.unlike(UUID.fromString(userId), courseId);
        return ResponseEntity.ok().build();
    }
}
