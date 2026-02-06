package com.lxp.content.resource.domain.model.vo;

public enum FileStatus {
    REQUESTED,
    UPLOADED,
    ATTACHED,
    MARKED_FOR_DELETE,
    DELETED;

    public boolean isUploaded() {
        return this == UPLOADED;
    }

    public boolean isAttached() {
        return this == ATTACHED;
    }

    public boolean canTransitionTo(FileStatus next) {
        return switch (this) {
            case REQUESTED -> next == UPLOADED || next == MARKED_FOR_DELETE;
            case UPLOADED -> next == ATTACHED || next == MARKED_FOR_DELETE;
            case ATTACHED -> next == UPLOADED || next == MARKED_FOR_DELETE;
            case MARKED_FOR_DELETE -> next == DELETED;
            case DELETED -> false;
        };
    }
}
