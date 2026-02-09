package com.lxp.content.resource.domain.exception;

import com.lxp.content.resource.domain.model.vo.UploadType;

import static com.lxp.content.resource.domain.exception.ResourceErrorCode.FILE_SIZE_EXCEEDED;

public class FileSizeExceededException extends ResourceException {

    public FileSizeExceededException(UploadType type, long size) {
        super(
            FILE_SIZE_EXCEEDED,
            FILE_SIZE_EXCEEDED.getMessage() + ": type=" + type + ", size=" + size
        );
    }
}
