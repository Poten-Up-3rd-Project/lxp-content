package com.lxp.content.course.qna.application.port.out;

@FunctionalInterface
public interface ReadCourseStructurePort {

    record Titles(String courseTitle, String sectionTitle, String lectureTitle) {}

    Titles titlesOf(String courseUuid, String sectionUuid, String lectureUuid);
}
