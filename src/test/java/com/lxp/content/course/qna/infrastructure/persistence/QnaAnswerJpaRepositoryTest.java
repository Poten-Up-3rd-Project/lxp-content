package com.lxp.content.course.qna.infrastructure.persistence;

import com.lxp.content.course.qna.infrastructure.persistence.entity.QnaAnswerJpaEntity;
import com.lxp.content.course.qna.infrastructure.persistence.repository.QnaAnswerJpaRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.test.context.jdbc.Sql;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@EnableJpaRepositories(basePackageClasses = QnaAnswerJpaRepository.class)
@EntityScan(basePackageClasses = QnaAnswerJpaEntity.class)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY)
class QnaAnswerJpaRepositoryTest {

    private final QnaAnswerJpaRepository repository;

    QnaAnswerJpaRepositoryTest(QnaAnswerJpaRepository repository) {
        this.repository = repository;
    }

    @Test
    @DisplayName("eventId로 조회와 저장이 가능하다")
    void saveAndFindByEventId() {
        var e = new QnaAnswerJpaEntity(
                "qna-1",
                "답변입니다",
                "gpt-4o-mini",
                LocalDateTime.now(),
                "lxp-qna-engine",
                "evt-abc"
        );
        repository.save(e);

        var found = repository.findByEventId("evt-abc");
        assertThat(found).isPresent();
        assertThat(found.get().getQnaBusinessId()).isEqualTo("qna-1");
        assertThat(found.get().getAnswerText()).isEqualTo("답변입니다");
    }
}
