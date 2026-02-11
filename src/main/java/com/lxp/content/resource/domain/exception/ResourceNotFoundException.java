package com.lxp.content.resource.domain.exception;

public class ResourceNotFoundException extends ResourceException{

    public ResourceNotFoundException(String message) {
        super(ResourceErrorCode.RESOURCE_NOT_FOUND, message);
    }
}
