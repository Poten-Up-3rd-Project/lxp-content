package com.lxp.content.course.application.event.policy;

public enum DeliveryPolicy {
    OUTBOX_REQUIRED,    // 반드시 전달되어야 함
    FIRE_AND_FORGET,    // 실패해도 괜찮음
}