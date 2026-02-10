package com.lxp.content.course.qna.application.port.out;

import com.lxp.content.course.qna.domain.model.Qna;

@FunctionalInterface
public interface SaveQnaPort {
    Qna save(Qna qna);
}
