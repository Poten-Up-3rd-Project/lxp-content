package com.lxp.content.course.domain.model;

import com.lxp.common.domain.event.AggregateRoot;
import com.lxp.common.domain.policy.BusinessRuleValidator;
import com.lxp.content.course.domain.event.CourseCreatedEvent;
import com.lxp.content.course.domain.exception.CourseException;
import com.lxp.content.course.domain.model.collection.CourseSections;
import com.lxp.content.course.domain.model.collection.CourseTags;
import com.lxp.content.course.domain.model.enums.Level;
import com.lxp.content.course.domain.model.id.*;
import com.lxp.content.course.domain.model.vo.CourseDescription;
import com.lxp.content.course.domain.model.vo.CourseTitle;
import com.lxp.content.course.domain.model.vo.duration.CourseDuration;
import com.lxp.content.course.domain.model.vo.duration.LectureDuration;
import com.lxp.content.course.domain.rule.LectureMinCountRule;
import com.lxp.content.course.domain.rule.SectionMinCountRule;
import com.lxp.content.course.domain.rule.TagMinCountRule;
import com.lxp.content.course.domain.service.spec.CourseMetaUpdateSpec;

import java.time.Instant;
import java.util.Objects;

public class Course extends AggregateRoot<CourseUUID> {
    private final Long id;
    private final CourseUUID uuid;
    private final InstructorUUID instructorUUID;
    private String thumbnailUrl;
    private CourseTitle title;
    private CourseDescription description;
    private CourseSections sections;
    private Level difficulty;
    private CourseTags tags;
    private Instant createdAt;
    private Instant updatedAt;

    private Course(
            Long id,
            CourseUUID uuid,
            InstructorUUID instructorUUID,
            String thumbnailUrl,
            CourseTitle title,
            CourseDescription description,
            Level difficulty,
            CourseSections sections,
            CourseTags tags,
            Instant createdAt,
            Instant updatedAt
    ) {
        this.id = id;
        this.uuid = uuid;
        this.instructorUUID = Objects.requireNonNull(instructorUUID);
        this.thumbnailUrl = thumbnailUrl;
        this.title = Objects.requireNonNull(title);
        this.description = description;
        this.difficulty = Objects.requireNonNull(difficulty);
        this.sections = Objects.requireNonNull(sections);
        this.tags = Objects.requireNonNull(tags);
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public static Course create(
            CourseUUID uuid,
            InstructorUUID instructorUUID,
            String thumbnailUrl,
            String title,
            String description,
            Level difficulty,
            CourseSections sections,
            CourseTags tags)
    {
        validateCreationInvariant(sections, tags);
        Course course = new Course(
                null,
                uuid,
                instructorUUID,
                thumbnailUrl,
                CourseTitle.of(title),
                CourseDescription.of(description),
                difficulty,
                sections,
                tags,
                null,
                null
        );

        course.registerEvent(new CourseCreatedEvent(
                uuid.value(),
                instructorUUID.value(),
                title,
                description,
                thumbnailUrl,
                difficulty,
                tags.values().stream().map(TagId::value).toList()
        ));

        return course;
    }

    public static Course reconstruct(
            Long id,
            CourseUUID uuid,
            InstructorUUID instructorUUID,
            String thumbnailUrl,
            String title,
            String description,
            Level difficulty,
            CourseSections sections,
            CourseTags tags,
            Instant createdAt,
            Instant updatedAt
    ) {
        return new Course(
                id,
                uuid,
                instructorUUID,
                thumbnailUrl,
                CourseTitle.of(title),
                CourseDescription.of(description),
                difficulty,
                sections,
                tags,
                createdAt,
                updatedAt
        );
    }

    private static void validateCreationInvariant(
            CourseSections sections,
            CourseTags tags
    ) {
        BusinessRuleValidator.validateAll(
                new SectionMinCountRule(sections),
                new LectureMinCountRule(sections),
                new TagMinCountRule(tags)
        );
    }

    private void ensureSectionStructureIsValid() {
        BusinessRuleValidator.validateAll(
                new SectionMinCountRule(sections),
                new LectureMinCountRule(sections)
        );
    }

    private void ensureTagPolicySatisfied() {
        BusinessRuleValidator.validate(
                new TagMinCountRule(tags)
        );
    }

    //setters
    public void apply(CourseMetaUpdateSpec changeSet) {
        changeSet.title().ifPresent(this::rename);
        changeSet.description().ifPresent(this::changeDescription);
        changeSet.thumbnailUrl().ifPresent(this::changeThumbnailUrl);
        changeSet.difficulty().ifPresent(this::changeDifficulty);
    }


    public void rename(CourseTitle title) {
        this.title = title;
    }

    public void changeDescription(CourseDescription description) {
        this.description = description;
    }

    public void changeDifficulty(Level difficulty) {
        this.difficulty = difficulty;
    }

    public void changeThumbnailUrl(String thumbnailUrl) {
        this.thumbnailUrl = thumbnailUrl;
    }


    //section
    public void addSection(SectionUUID uuid, String title) {
        this.sections = sections.addSection(uuid, title);
    }

    public void removeSection(SectionUUID uuid) {
        this.sections = sections.removeSection(uuid);
        ensureSectionStructureIsValid();
    }

    public void renameSection(SectionUUID uuid, String title) {
        this.sections = sections.renameSection(uuid, title);
    }

    public void reorderSection(SectionUUID uuid, int newOrder) {
        this.sections = sections.reorderSection(uuid, newOrder);
    }

    //lecture
    public void addLecture(
            SectionUUID sectionUUID,
            LectureUUID lectureUUID,
            String title,
            LectureDuration duration,
            String videoUrl
    ) {
        this.sections = sections.addLecture(sectionUUID, lectureUUID, title, duration, videoUrl);
    }

    public void removeLecture(SectionUUID sectionUUID, LectureUUID lectureUUID) {
        this.sections = sections.removeLecture(sectionUUID, lectureUUID);
        ensureSectionStructureIsValid();
    }

    public void renameLecture(SectionUUID sectionUUID, LectureUUID lectureUUID, String newTitle) {
        this.sections = sections.renameLecture(sectionUUID, lectureUUID, newTitle);
    }

    public void changeLectureVideoUrl(SectionUUID sectionUUID, LectureUUID lectureUUID, String url) {
        this.sections = sections.changeLectureVideoUrl(sectionUUID, lectureUUID, url);
    }

    // tag
    public void addTag(TagId tag) {
        this.tags = this.tags.add(tag);
    }

    public void removeTag(TagId tag) {
        this.tags = this.tags.remove(tag);
        ensureTagPolicySatisfied();
    }

    public boolean hasTag(TagId tag) {
        return this.tags.contains(tag);
    }

    public CourseTags tags() { return tags; }
    public CourseUUID uuid() { return uuid; }
    public Long id() { return id; }
    public CourseTitle title() { return title; }
    public CourseDescription description() { return description; }
    public Level difficulty() { return difficulty; }
    public CourseSections sections() { return sections; }
    public String thumbnailUrl() { return thumbnailUrl; }
    public InstructorUUID instructorUUID() { return instructorUUID; }
    public Instant createdAt() { return createdAt; }
    public Instant updatedAt() { return updatedAt; }
    public Level level() { return difficulty; }

    public CourseDuration totalDuration() {
        return sections.totalDuration();
    }

    @Override
    public CourseUUID getId() {
        return uuid;
    }
}
