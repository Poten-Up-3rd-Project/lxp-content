package com.lxp.content.course.qna.infrastructure.persistence.adapter;

import com.lxp.content.course.infra.persistence.mysql.write.entity.CourseJpaEntity;
import com.lxp.content.course.infra.persistence.mysql.write.entity.LectureJpaEntity;
import com.lxp.content.course.infra.persistence.mysql.write.entity.SectionJpaEntity;
import com.lxp.content.course.infra.persistence.mysql.write.repository.CourseJpaRepository;
import com.lxp.content.course.qna.application.port.out.ReadCourseStructurePort;
import com.lxp.content.course.qna.infrastructure.persistence.repository.LectureReadRepository;
import com.lxp.content.course.qna.infrastructure.persistence.repository.SectionReadRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CourseStructureReadAdapter implements ReadCourseStructurePort {

    private final CourseJpaRepository courseRepo;
    private final SectionReadRepository sectionRepo;
    private final LectureReadRepository lectureRepo;

    @Override
    public Titles titlesOf(String courseUuid, String sectionUuid, String lectureUuid) {
        String courseTitle = courseRepo.findByUuid(courseUuid).map(CourseJpaEntity::getTitle).orElse("N/A");
        String sectionTitle = sectionRepo.findByUuid(sectionUuid).map(SectionJpaEntity::getTitle).orElse("N/A");
        String lectureTitle = lectureRepo.findByUuid(lectureUuid).map(LectureJpaEntity::getTitle).orElse("N/A");
        return new Titles(courseTitle, sectionTitle, lectureTitle);
    }
}
