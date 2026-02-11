package com.lxp.content.resource.domain.model.vo;

import com.lxp.content.resource.domain.exception.FileSizeExceededException;
import com.lxp.content.resource.domain.exception.UnsupportedContentTypeException;
import lombok.RequiredArgsConstructor;

import java.util.Set;

@RequiredArgsConstructor
public enum UploadType {

    IMAGE(
        Set.of("image/png", "image/jpeg", "image/webp", "image/jpg")
    ) {
        @Override
        public long maxSize() {
            return 5 * MB;
        }

        @Override
        public String keyPrefix() {
            return "images";
        }
    },

    VIDEO(
        Set.of("video/mp4", "video/webm")
    ) {
        @Override
        public long maxSize() {
            return 200 * MB;
        }

        @Override
        public String keyPrefix() {
            return "videos";
        }
    };

    private static final long MB = 1024L * 1024L;

    public static UploadType fromContentType(String contentType) {
        for (var t : values()) {
            if (t.supports(contentType)) return t;
        }
        throw new UnsupportedContentTypeException(contentType);
    }

    private final Set<String> allowedContentTypes;

    protected abstract long maxSize();

    public String keyPrefix() {
        return "resources";
    }

    public void validateSize(long size) {
        if (size > this.maxSize()) {
            throw new FileSizeExceededException(this, size);
        }
    }

    public void isNotSupportedThenThrow(String contentType) {
        if (!supports(contentType)) {
            throw new UnsupportedContentTypeException(
                String.format("%s does not support %s", this, contentType)
            );
        }
    }

    public boolean supports(String contentType) {
        return allowedContentTypes.contains(contentType);
    }
}
