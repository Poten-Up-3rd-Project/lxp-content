package com.lxp.content.progress.domain.model;

import com.lxp.common.domain.event.AggregateRoot;
import com.lxp.content.progress.domain.model.enums.CourseProgressStatus;
import com.lxp.content.progress.domain.model.vo.CourseId;
import com.lxp.content.progress.domain.model.vo.CourseProgressId;
import com.lxp.content.progress.domain.model.vo.LectureId;
import com.lxp.content.progress.domain.model.vo.UserId;
import com.lxp.content.progress.domain.policy.CompletionPolicy;
import com.lxp.content.progress.domain.policy.CourseCompletionResult;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

/**
 * 강좌 진행률 도메인
 */
public class CourseProgress extends AggregateRoot<CourseProgressId> {

    private CourseProgressId courseProgressId;
    private UserId userId;
    private CourseId courseId;
    private float totalProgress;
    private CourseProgressStatus courseProgressStatus;
    private LocalDateTime completedAt;

    private final List<LectureProgress> lectureProgresses;

    /**
     * 강좌 진행률 생성
     * @param userId 학습자 ID
     * @param courseId 강좌 ID
     * @param lectureProgresses 강의 진행률 리스트
     * @return 생성된 강좌 진행률
     */
    public static CourseProgress create(UserId userId, CourseId courseId, List<LectureProgress> lectureProgresses) {
        return new CourseProgress(
                Objects.requireNonNull(userId, "UserId는 null일 수 없습니다."),
                Objects.requireNonNull(courseId, "CourseId는 null일 수 없습니다."),
                CourseProgressStatus.IN_PROGRESS,
                0.0f,
                Objects.requireNonNull(lectureProgresses, "LectureProgresses는 null일 수 없습니다."),
                null
        );
    }

    public static CourseProgress create(
            CourseProgressId courseProgressId,
            UserId userId,
            CourseId courseId,
            float totalProgress,
            CourseProgressStatus courseProgressStatus,
            LocalDateTime completedAt,
            List<LectureProgress> lectureProgresses) {
        return new CourseProgress(
                courseProgressId,
                userId,
                courseId,
                totalProgress,
                courseProgressStatus,
                completedAt,
                lectureProgresses
        );
    }

    /**
     * 강좌 내 강의 진행 상태 업데이트
     * @param id 강의 ID
     * @param lastPlayedTimeInSeconds 마지막 재생 시간
     * @param policy 완료 정책
     */
    public void updateLectureProgress(LectureId id,
                                      Integer lastPlayedTimeInSeconds,
                                      CompletionPolicy policy) {
        LectureProgress lectureProgress = findLectureProgress(id);
        if(lectureProgress.completed()) return;

        lectureProgress.updateLastPlayedTime(lastPlayedTimeInSeconds, policy);
    }

    public void reflectCalculation(CourseCompletionResult result) {
        this.totalProgress = result.totalProgress();

        if(result.isCompleted()) {
            complete();
        }
    }

    /**
     * 강좌 진행률 완료 여부
     * @return 완료 여부
     */
    public boolean isCompleted() {
        return (this.courseProgressStatus == CourseProgressStatus.COMPLETED && this.totalProgress == 100.0f);
    }

    private void complete() {
        this.courseProgressStatus = CourseProgressStatus.COMPLETED;
        this.completedAt = LocalDateTime.now();
    }

    /**
     * 강의 ID에 맞는 강의 진행 찾기
     * @param id 강의 ID
     * @return 강의 진행 도메인 객체
     */
    private LectureProgress findLectureProgress(LectureId id) {
        return lectureProgresses.stream()
                .filter(lecProgress -> lecProgress.lectureId().equals(id))
                .findAny().orElseThrow(() -> new IllegalArgumentException("해당 LectureProgressID에 해당하는 LectureProgress가 없습니다. : " + id.value()));
    }

    @Override
    public CourseProgressId getId() { return courseProgressId;}

    public UserId userId() { return userId;}
    
    public CourseId courseId() { return courseId; }

    public float totalProgress() { return totalProgress;}

    public CourseProgressStatus studyStatus() { return courseProgressStatus;}

    public LocalDateTime completedAt() { return completedAt; }

    public List<LectureProgress> lectureProgresses() { return lectureProgresses;}

    private CourseProgress(UserId userId, CourseId courseId, CourseProgressStatus progressStatus, float totalProgress, List<LectureProgress> lectureProgresses, LocalDateTime completedAt) {
        this.lectureProgresses = lectureProgresses;
        this.completedAt = completedAt;
        this.courseProgressStatus = progressStatus;
        this.totalProgress = totalProgress;
        this.courseId = courseId;
        this.userId = userId;
    }

    private CourseProgress(
                           CourseProgressId courseProgressId,
                           UserId userId,
                           CourseId courseId,
                           float totalProgress,
                           CourseProgressStatus courseProgressStatus,
                           LocalDateTime completedAt,
                           List<LectureProgress> lectureProgresses) {
        this.courseProgressId = courseProgressId;
        this.userId = userId;
        this.courseId = courseId;
        this.totalProgress = totalProgress;
        this.courseProgressStatus = courseProgressStatus;
        this.completedAt = completedAt;
        this.lectureProgresses = lectureProgresses;
    }
}
