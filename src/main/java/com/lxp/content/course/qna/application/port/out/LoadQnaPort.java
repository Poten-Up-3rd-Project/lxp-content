package com.lxp.content.course.qna.application.port.out;

import com.lxp.content.course.qna.domain.model.Qna;

import java.util.List;
import java.util.Optional;

public interface LoadQnaPort {

    Optional<Qna> findById(String id);

    List<Qna> findByLectureUuid(String lectureUuid);
}
