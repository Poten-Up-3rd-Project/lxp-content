package com.lxp.content.progress.domain.model;

import com.lxp.common.domain.model.BaseEntity;
import com.lxp.content.progress.domain.model.enums.LectureProgressStatus;
import com.lxp.content.progress.domain.model.vo.LectureId;
import com.lxp.content.progress.domain.model.vo.LectureProgressId;
import com.lxp.content.progress.domain.model.vo.UserId;
import com.lxp.content.progress.domain.policy.CompletionPolicy;
import com.lxp.content.progress.exception.ProgressDomainException;
import com.lxp.content.progress.exception.ProgressErrorCode;

import java.util.Objects;

/**
 * 강의 진행률 도메인
 */
public class LectureProgress extends BaseEntity<LectureProgressId> {

    private LectureProgressId lectureProgressId;
    private UserId userId;
    private LectureId lectureId;
    private LectureProgressStatus lectureProgressStatus;
    private Integer lastPlayedTimeInSeconds;
    private Integer totalDurationInSeconds;

    /**
     * 강의 진행률 생성
     * @param userId 사용자 ID
     * @param lectureId 강의 ID
     * @return 생성 된 강의 진행률
     */
    public static LectureProgress create(UserId userId, LectureId lectureId, Integer totalDurationInSeconds) {
        if(totalDurationInSeconds == null) {
            throw new NullPointerException("totalDurationInSeconds는 null일 수 없습니다.");
        } else if(totalDurationInSeconds <= 0) {
            throw new IllegalArgumentException("totalDurationInSeconds는 0보다 커야 합니다.");
        }

        return new LectureProgress(
                Objects.requireNonNull(userId, "UserId는 null일 수 없습니다."),
                Objects.requireNonNull(lectureId, "lectureId는 null일 수 없습니다."),
                LectureProgressStatus.NOT_STARTED,
                0,
                totalDurationInSeconds
        );
    }

    public static LectureProgress create(LectureProgressId lectureProgressId, UserId userId, LectureId lectureId, LectureProgressStatus lectureProgressStatus, Integer lastPlayedTimeInSeconds, Integer totalDurationInSeconds) {
        return new LectureProgress(
                lectureProgressId, userId, lectureId, lectureProgressStatus, lastPlayedTimeInSeconds, totalDurationInSeconds
        );
    }

    /**
     * 강의 진행률 기록
     */
    public void updateLastPlayedTime(Integer lastPlayedTimeInSeconds, CompletionPolicy policy) {
        if(this.lectureProgressStatus == LectureProgressStatus.COMPLETED) {
            throw new IllegalStateException("완료 상태의 강의는 진도를 업데이트 할 수 없습니다.");
        }

        if(lastPlayedTimeInSeconds > this.totalDurationInSeconds) {
            throw new ProgressDomainException(ProgressErrorCode.INVALID_LAST_PLAYED_TIME_VALUE);
        }

        this.lastPlayedTimeInSeconds = lastPlayedTimeInSeconds;

        if(policy.isSatisfiedBy(this)) {
            this.lectureProgressStatus = LectureProgressStatus.COMPLETED;
        } else if(this.lastPlayedTimeInSeconds > 0) {
            this.lectureProgressStatus = LectureProgressStatus.IN_PROGRESS;
        }
    }

    /**
     * 강의 진행률 완료 여부
     * @return 진행 완료 여부
     */
    public boolean completed() {
        return (this.lectureProgressStatus == LectureProgressStatus.COMPLETED)
                && (Objects.equals(this.lastPlayedTimeInSeconds, this.totalDurationInSeconds));
    }

    /**
     * 강의 기록 진행 상태로 변경
     */
    private void changeInProgress() {
        if(this.lectureProgressStatus == LectureProgressStatus.COMPLETED) {
            throw new IllegalStateException("완료 상태의 강의는 진행 상태로 변경할 수 없습니다.");
        }

        this.lectureProgressStatus = LectureProgressStatus.IN_PROGRESS;
    }

    /**
     * 강의 기록 완료 상태로 변경
     */
    private void changeCompleted(CompletionPolicy policy) {
        if(this.lectureProgressStatus == LectureProgressStatus.COMPLETED) {
            throw new IllegalStateException("완료 상태의 강의는 진행 상태로 변경할 수 없습니다.");
        }

        if(policy.isSatisfiedBy(this)) {
            this.lectureProgressStatus = LectureProgressStatus.COMPLETED;
        }
    }

    public LectureProgressId getId() { return lectureProgressId; }

    public UserId userId() { return userId;}

    public LectureId lectureId() { return lectureId;}

    public LectureProgressStatus lectureProgressStatus() { return lectureProgressStatus;}

    public Integer lastPlayedTimeInSeconds() { return lastPlayedTimeInSeconds; }

    public Integer totalDurationInSeconds() { return totalDurationInSeconds;}

    private LectureProgress(UserId userId, LectureId lectureId, LectureProgressStatus lectureProgressStatus, Integer lastPlayedTimeInSeconds, Integer totalDurationInSeconds) {
        this.lectureProgressId = null;
        this.userId = userId;
        this.lectureId = lectureId;
        this.lectureProgressStatus = lectureProgressStatus;
        this.lastPlayedTimeInSeconds = lastPlayedTimeInSeconds;
        this.totalDurationInSeconds = totalDurationInSeconds;
    }

    private LectureProgress(LectureProgressId lectureProgressId, UserId userId, LectureId lectureId, LectureProgressStatus lectureProgressStatus, Integer lastPlayedTimeInSeconds, Integer totalDurationInSeconds) {
        this.lectureProgressId = lectureProgressId;
        this.userId = userId;
        this.lectureId = lectureId;
        this.lectureProgressStatus = lectureProgressStatus;
        this.lastPlayedTimeInSeconds = lastPlayedTimeInSeconds;
        this.totalDurationInSeconds = totalDurationInSeconds;
    }
}
