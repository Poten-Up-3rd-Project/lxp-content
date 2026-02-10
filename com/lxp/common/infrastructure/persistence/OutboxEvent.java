package com.lxp.common.infrastructure.persistence;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Lob;
import jakarta.persistence.Table;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Outbox 패턴용 이벤트 엔티티
 * 트랜잭션과 함께 이벤트 저장 후 별도 프로세스에서 발행
 */
@Entity
@Table(name = "outbox_events", indexes = {
        @Index(name = "idx_outbox_status", columnList = "status"),
        @Index(name = "idx_outbox_created_at", columnList = "created_at"),
        @Index(name = "idx_outbox_aggregate", columnList = "aggregate_type, aggregate_id")
})
public class OutboxEvent {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "event_id", nullable = false, unique = true, length = 36)
    private String eventId;

    @Column(name = "event_type", nullable = false, length = 255)
    private String eventType;

    @Column(name = "aggregate_type", nullable = false, length = 255)
    private String aggregateType;

    @Column(name = "aggregate_id", nullable = false, length = 255)
    private String aggregateId;

    @Lob
    @Column(columnDefinition = "LONGTEXT", name = "payload", nullable = false)
    private String payload;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private OutboxStatus status;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "published_at")
    private LocalDateTime publishedAt;

    @Column(name = "retry_count", nullable = false)
    private int retryCount;

    @Column(name = "last_error", length = 1000)
    private String lastError;

    private int priority;               // Priority 전략
    private Long ttlMillis;             // Expirable (null = 만료 없음)
    private String partitionKey;        // Ordering 전략
    private int maxRetries;             // Failure 전략
    private long initialBackoffMillis;  // Failure 전략
    private boolean useDlq;             // Failure 전략
    private boolean exactlyOnce;        // Delivery 전략
    private LocalDateTime nextRetryAt;  // Backoff 스케줄링


    protected OutboxEvent() {
    }

    public OutboxEvent(String eventId,
                       String eventType,
                       String aggregateType,
                       String aggregateId,
                       String payload,
                       LocalDateTime createdAt
                       ) {
        this.eventId = eventId;
        this.eventType = eventType;
        this.aggregateType = aggregateType;
        this.aggregateId = aggregateId;
        this.payload = payload;
        this.status = OutboxStatus.PENDING;
        this.createdAt = createdAt == null ? LocalDateTime.now() : createdAt;
        this.retryCount = 0;
    }

    // 새 생성자
    public OutboxEvent(String eventId, String eventType, String aggregateType,
                       String aggregateId, String payload, LocalDateTime createdAt,
                       OutboxOptions options) {
        this.eventId = eventId;
        this.eventType = eventType;
        this.aggregateType = aggregateType;
        this.aggregateId = aggregateId;
        this.payload = payload;
        this.createdAt = createdAt;
        this.status = OutboxStatus.PENDING;
        this.retryCount = 0;
        this.createdAt = LocalDateTime.now();
        this.nextRetryAt = LocalDateTime.now();

        if (options != null) {
            this.priority = options.priority();
            this.ttlMillis = options.ttl() != null ? options.ttl().toMillis() : null;
            this.partitionKey = options.partitionKey();
            this.maxRetries = options.maxRetries();
            this.initialBackoffMillis = options.initialBackoff().toMillis();
            this.useDlq = options.useDlq();
            this.exactlyOnce = options.exactlyOnce();
        } else {
            // 기본값
            this.priority = 50;
            this.maxRetries = 3;
            this.initialBackoffMillis = 5000;
            this.useDlq = false;
        }
    }

    public void markAsPublished() {
        this.status = OutboxStatus.PUBLISHED;
        this.publishedAt = LocalDateTime.now();
    }

    public void markAsFailed(String error) {
        this.retryCount++;
        this.lastError = error;

        if (this.retryCount >= this.maxRetries) {
            this.status = OutboxStatus.FAILED;
        } else {
            // Backoff 계산
            long backoff = (long) (this.initialBackoffMillis * Math.pow(2, this.retryCount - 1));
            this.nextRetryAt = LocalDateTime.now().plus(Duration.ofMillis(backoff));
            this.status = OutboxStatus.PENDING;
        }
    }

    public boolean isExpired() {
        if (this.ttlMillis == null) return false;
        return this.createdAt.plus(Duration.ofMillis(this.ttlMillis))
                .isBefore(LocalDateTime.now());
    }


    public void markForRetry() {
        this.status = OutboxStatus.PENDING;
    }

    // Getters
    public Long getId() {
        return id;
    }

    public String getEventId() {
        return eventId;
    }

    public String getEventType() {
        return eventType;
    }

    public String getAggregateType() {
        return aggregateType;
    }

    public String getAggregateId() {
        return aggregateId;
    }

    public String getPayload() {
        return payload;
    }

    public OutboxStatus getStatus() {
        return status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getPublishedAt() {
        return publishedAt;
    }

    public int getRetryCount() {
        return retryCount;
    }

    public String getLastError() {
        return lastError;
    }

    public enum OutboxStatus {
        PENDING,
        PUBLISHED,
        FAILED,
        EXPIRED,
        DLQ
    }
}
