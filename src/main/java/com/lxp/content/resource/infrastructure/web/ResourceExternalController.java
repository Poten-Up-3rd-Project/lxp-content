package com.lxp.content.resource.infrastructure.web;

import com.lxp.content.resource.application.port.provided.command.AttachResourceCommand;
import com.lxp.content.resource.application.port.provided.command.ConfirmUploadCommand;
import com.lxp.content.resource.application.port.provided.command.DeleteResourceCommand;
import com.lxp.content.resource.application.port.provided.command.DetachResourceCommand;
import com.lxp.content.resource.application.port.provided.command.DownloadUrlCommand;
import com.lxp.content.resource.application.port.provided.command.MarkForDeleteCommand;
import com.lxp.content.resource.application.port.provided.query.GenerateUploadUrlQuery;
import com.lxp.content.resource.application.port.provided.result.PresignedUrlResult;
import com.lxp.content.resource.application.port.provided.usecase.AttachResourceUseCase;
import com.lxp.content.resource.application.port.provided.usecase.ConfirmUploadUseCase;
import com.lxp.content.resource.application.port.provided.usecase.DeleteResourceUseCase;
import com.lxp.content.resource.application.port.provided.usecase.DetachResourceUseCase;
import com.lxp.content.resource.application.port.provided.usecase.DownloadUrlUseCase;
import com.lxp.content.resource.application.port.provided.usecase.GenerateUploadUrlUseCase;
import com.lxp.content.resource.application.port.provided.usecase.MarkForDeleteUseCase;
import com.lxp.content.resource.domain.model.vo.UploadType;
import com.lxp.content.resource.infrastructure.web.dto.ConfirmUploadRequest;
import com.lxp.content.resource.infrastructure.web.dto.ResourceKeyRequest;
import com.lxp.content.resource.infrastructure.web.dto.UploadUrlRequest;
import com.lxp.content.resource.infrastructure.web.dto.UploadUrlResponse;
import com.lxp.passport.authorization.annotation.CurrentUserId;
import com.lxp.passport.authorization.annotation.RequireRole;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.URL;

@RestController
@RequestMapping("/api-v1/resources")
@RequiredArgsConstructor
@Validated
@RequireRole("ROLE_INSTRUCTOR")
public class ResourceExternalController {

    private final GenerateUploadUrlUseCase generateUploadUrlUseCase;
    private final DownloadUrlUseCase downloadUrlUseCase;
    private final ConfirmUploadUseCase confirmUploadUseCase;
    private final AttachResourceUseCase attachResourceUseCase;
    private final DetachResourceUseCase detachResourceUseCase;
    private final MarkForDeleteUseCase markForDeleteUseCase;
    private final DeleteResourceUseCase deleteResourceUseCase;

    @PostMapping("/upload-url")
    public ResponseEntity<UploadUrlResponse> createUploadUrl(
        @CurrentUserId String userId,
        @RequestBody UploadUrlRequest req
    ) {
        PresignedUrlResult result = generateUploadUrlUseCase.execute(
            new GenerateUploadUrlQuery(userId, UploadType.valueOf(req.uploadType()), req.contentType())
        );
        return ResponseEntity.ok(
            new UploadUrlResponse(result.key(), result.url(), result.method(), result.headers())
        );
    }

    @PostMapping("/confirm-upload")
    public ResponseEntity<Void> confirmUpload(@RequestBody ConfirmUploadRequest req) {
        confirmUploadUseCase.execute(new ConfirmUploadCommand(req.key()));
        return ResponseEntity.ok().build();
    }

    @PostMapping("/attach")
    public ResponseEntity<Void> attach(@RequestBody ResourceKeyRequest req) {
        attachResourceUseCase.execute(new AttachResourceCommand(req.key()));
        return ResponseEntity.ok().build();
    }

    @PostMapping("/detach")
    public ResponseEntity<Void> detach(@RequestBody ResourceKeyRequest req) {
        detachResourceUseCase.execute(new DetachResourceCommand(req.key()));
        return ResponseEntity.ok().build();
    }

    @PostMapping("/mark-for-delete")
    public ResponseEntity<Void> markForDelete(@RequestBody ResourceKeyRequest req) {
        markForDeleteUseCase.execute(new MarkForDeleteCommand(req.key()));
        return ResponseEntity.ok().build();
    }

    @PostMapping("/delete")
    public ResponseEntity<Void> delete(@RequestBody ResourceKeyRequest req) {
        deleteResourceUseCase.execute(new DeleteResourceCommand(req.key()));
        return ResponseEntity.ok().build();
    }

    @GetMapping
    public ResponseEntity<URL> download(@RequestBody ResourceKeyRequest req) {
        return ResponseEntity.ok(downloadUrlUseCase.execute(new DownloadUrlCommand(req.key())));
    }

}
