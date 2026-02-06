package com.lxp.content.resource.domain.support;

import com.lxp.content.resource.domain.exception.ResourceErrorCode;
import com.lxp.content.resource.domain.exception.ResourceException;

import static java.util.Objects.isNull;

public class ResourceGuard {

    public static <T> T requireNonNull(T obj, String message) {
        if (isNull(obj)) {
            throw missing(message);
        }
        return obj;
    }

    public static String requireNonBlank(String value, String message) {
        if (isNull(value) || value.isBlank()) {
            throw missing(message);
        }
        return value;
    }

    private static ResourceException missing(String message) {
        return new ResourceException(ResourceErrorCode.MISSING_REQUIRED_FIELD, message);
    }

}
