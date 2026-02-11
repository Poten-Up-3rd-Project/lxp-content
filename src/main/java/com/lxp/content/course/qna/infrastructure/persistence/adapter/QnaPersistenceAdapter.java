package com.lxp.content.course.qna.infrastructure.persistence.adapter;

import com.lxp.content.course.qna.application.port.out.LoadQnaPort;
import com.lxp.content.course.qna.application.port.out.SaveQnaPort;
import com.lxp.content.course.qna.domain.model.Qna;
import com.lxp.content.course.qna.infrastructure.persistence.entity.QnaJpaEntity;
import com.lxp.content.course.qna.infrastructure.persistence.repository.QnaJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class QnaPersistenceAdapter implements SaveQnaPort, LoadQnaPort {

    private final QnaJpaRepository repository;

    @Override
    public Qna save(Qna qna) {
        QnaJpaEntity entity = new QnaJpaEntity(
            qna.getId(),
            qna.getCourseUuid(),
            qna.getSectionUuid(),
            qna.getLectureUuid(),
            qna.getAuthorId(),
            qna.getTitle(),
            qna.getContent()
        );
        repository.save(entity);
        return qna; // entity -> domain is 1:1 for now
    }

    @Override
    public Optional<Qna> findById(String id) {
        return repository.findByBusinessId(id).map(this::toDomain);
    }

    @Override
    public List<Qna> findByLectureUuid(String lectureUuid) {
        return repository.findByLectureUuidOrderByCreatedAtDesc(lectureUuid).stream().map(this::toDomain).toList();
    }

    private Qna toDomain(QnaJpaEntity e) {
        try {
            java.lang.reflect.Constructor<com.lxp.content.course.qna.domain.model.Qna> c = Qna.class.getDeclaredConstructor();
            c.setAccessible(true);
            Qna q = c.newInstance();
            java.lang.reflect.Field f;
            f = Qna.class.getDeclaredField("id");
            f.setAccessible(true);
            f.set(q, e.getBusinessId());
            f = Qna.class.getDeclaredField("courseUuid");
            f.setAccessible(true);
            f.set(q, e.getCourseUuid());
            f = Qna.class.getDeclaredField("sectionUuid");
            f.setAccessible(true);
            f.set(q, e.getSectionUuid());
            f = Qna.class.getDeclaredField("lectureUuid");
            f.setAccessible(true);
            f.set(q, e.getLectureUuid());
            f = Qna.class.getDeclaredField("authorId");
            f.setAccessible(true);
            f.set(q, e.getAuthorId());
            f = Qna.class.getDeclaredField("title");
            f.setAccessible(true);
            f.set(q, e.getTitle());
            f = Qna.class.getDeclaredField("content");
            f.setAccessible(true);
            f.set(q, e.getContent());
            f = Qna.class.getDeclaredField("createdAt");
            f.setAccessible(true);
            f.set(q, e.getCreatedAt());
            return q;
        } catch (Exception ex) {
            throw new IllegalStateException("Failed to map entity to domain", ex);
        }
    }
}
