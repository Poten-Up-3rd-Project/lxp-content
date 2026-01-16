package com.lxp.content.like.application;

import com.lxp.content.course.application.port.provider.query.CheckCourseExistsQuery;
import com.lxp.content.course.application.port.provider.usecase.query.CheckCourseExistsUseCase;
import com.lxp.content.like.domain.exception.LikeErrorCode;
import com.lxp.content.like.domain.exception.LikeException;
import com.lxp.content.like.domain.model.Like;
import com.lxp.content.like.repository.LikeRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.UUID;

@Service
@Transactional
public class LikeCommandService {

    private LikeRepository likeRepository;
    private CheckCourseExistsUseCase checkCourseExistsUseCase;

    public LikeCommandService(
            LikeRepository likeRepository,
            CheckCourseExistsUseCase checkCourseExistsUseCase
    ) {
        this.likeRepository = likeRepository;
        this.checkCourseExistsUseCase = checkCourseExistsUseCase;
    }

    public void like(UUID userId, UUID courseId) {
        Like like = new Like(userId, courseId);

        CheckCourseExistsQuery q = new CheckCourseExistsQuery(courseId.toString());
        boolean courseNotExists = !checkCourseExistsUseCase.execute(q);

        if (courseNotExists) {
            throw new LikeException(LikeErrorCode.COURSE_NOT_FOUND);
        }

        likeRepository.saveIdempotently(like.userId(), like.courseId(), Instant.now());
    }

    public void unlike(UUID userId, UUID courseId) {
        // 언팔로우 시에는 따로 강좌 존재 여부 검증 안 해도 됨
        // DB에 삭제 대상이 없어도 별도 예외가 발생하지 않으므로 멱등성이 보장됨
        likeRepository.deleteByUserIdAndCourseId(userId, courseId);
    }
}
