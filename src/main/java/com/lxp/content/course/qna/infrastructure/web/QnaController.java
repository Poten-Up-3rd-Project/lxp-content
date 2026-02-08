package com.lxp.content.course.qna.infrastructure.web;

import com.lxp.content.course.qna.application.port.in.CreateQnaUseCase;
import com.lxp.content.course.qna.application.port.in.GetQnaQuery;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class QnaController {

    private final CreateQnaUseCase createQnaUseCase;
    private final GetQnaQuery getQnaQuery;

    @PostMapping("/courses/{courseUuid}/sections/{sectionUuid}/lectures/{lectureUuid}/qna")
    @ResponseStatus(HttpStatus.CREATED)
    public CreateResponse create(
        @PathVariable String courseUuid,
        @PathVariable String sectionUuid,
        @PathVariable String lectureUuid,
        @RequestBody CreateRequest body
    ) {
        var res = createQnaUseCase.handle(new CreateQnaUseCase.Command(
            courseUuid,
            sectionUuid,
            lectureUuid,
            body.authorId(),
            body.title(),
            body.content()
        ));
        return new CreateResponse(res.id());
    }

    @GetMapping("/qna/{qnaId}")
    public GetQnaQuery.QnaView get(@PathVariable String qnaId) {
        return getQnaQuery.byId(qnaId);
    }

    @GetMapping("/lectures/{lectureUuid}/qna")
    public List<GetQnaQuery.QnaView> listByLecture(@PathVariable String lectureUuid) {
        return getQnaQuery.byLecture(lectureUuid);
    }

    public record CreateRequest(String authorId, String title, String content) {
    }

    public record CreateResponse(String id) {
    }
}
