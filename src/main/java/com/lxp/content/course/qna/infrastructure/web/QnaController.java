package com.lxp.content.course.qna.infrastructure.web;

import com.lxp.content.course.qna.application.port.in.AddQnaAnswerUseCase;
import com.lxp.content.course.qna.application.port.in.CreateQnaUseCase;
import com.lxp.content.course.qna.application.port.in.GetQnaQuery;
import com.lxp.content.course.qna.application.port.in.GetQnaAnswersQuery;
import com.lxp.content.course.qna.infrastructure.web.dto.request.AddAnswerRequest;
import com.lxp.content.course.qna.infrastructure.web.dto.request.QnaCreateRequest;
import com.lxp.content.course.qna.infrastructure.web.dto.response.AddAnswerResponse;
import com.lxp.content.course.qna.infrastructure.web.dto.response.QnaCreateResponse;
import com.lxp.passport.authorization.annotation.RequireRole;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api-v1/")
public class QnaController {

    private final CreateQnaUseCase createQnaUseCase;
    private final GetQnaQuery getQnaQuery;
    private final AddQnaAnswerUseCase addQnaAnswerUseCase;
    private final GetQnaAnswersQuery getQnaAnswersQuery;

    @RequireRole(anyOf = {"ROLE_USER", "ROLE_INSTRUCTOR"})
    @PostMapping("/courses/{courseUuid}/sections/{sectionUuid}/lectures/{lectureUuid}/qna")
    @ResponseStatus(HttpStatus.CREATED)
    public QnaCreateResponse create(
        @PathVariable String courseUuid,
        @PathVariable String sectionUuid,
        @PathVariable String lectureUuid,
        @RequestBody QnaCreateRequest body
    ) {
        var res = createQnaUseCase.handle(new CreateQnaUseCase.Command(
            courseUuid,
            sectionUuid,
            lectureUuid,
            body.authorId(),
            body.title(),
            body.content()
        ));
        return new QnaCreateResponse(res.id());
    }

    @GetMapping("/qna/public/{qnaId}")
    public GetQnaQuery.QnaView get(@PathVariable String qnaId) {
        return getQnaQuery.byId(qnaId);
    }

    @GetMapping("/lectures/public/{lectureUuid}/qna")
    public List<GetQnaQuery.QnaView> listByLecture(@PathVariable String lectureUuid) {
        return getQnaQuery.byLecture(lectureUuid);
    }

    @PostMapping("/qna/{qnaId}/answers")
    @ResponseStatus(HttpStatus.CREATED)
    public AddAnswerResponse addAnswer(
        @PathVariable String qnaId,
        @RequestBody AddAnswerRequest body,
        @RequestHeader(value = "Idempotency-Key", required = false) String idempotencyKey
    ) {
        // Diagnostics: incoming answer length
        org.slf4j.LoggerFactory.getLogger(QnaController.class).info(
            "qna.answer.incoming qnaId={}, eventId={}, len={}",
            qnaId,
            idempotencyKey != null ? idempotencyKey : body.eventId(),
            body.answerText() != null ? body.answerText().length() : 0
        );

        var cmd = new com.lxp.content.course.qna.application.port.in.AddQnaAnswerUseCase.Command(
            qnaId,
            body.answerText(),
            body.model(),
            body.answeredAt(),
            body.source(),
            idempotencyKey != null ? idempotencyKey : body.eventId()
        );
        var res = addQnaAnswerUseCase.handle(cmd);
        return new AddAnswerResponse(res.id());
    }

    @GetMapping("/qna/public/{qnaId}/answers")
    public java.util.List<GetQnaAnswersQuery.AnswerView> listAnswers(@PathVariable String qnaId) {
        return getQnaAnswersQuery.byQnaId(qnaId);
    }
}
