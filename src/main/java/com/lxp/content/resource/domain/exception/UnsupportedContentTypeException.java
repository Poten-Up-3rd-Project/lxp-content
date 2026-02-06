package com.lxp.content.resource.domain.exception;

public class UnsupportedContentTypeException extends ResourceException {

    public UnsupportedContentTypeException(String contentType) {
        super(ResourceErrorCode.UNSUPPORTED_CONTENT_TYPE, "unsupported content type: " + contentType);
    }
}
