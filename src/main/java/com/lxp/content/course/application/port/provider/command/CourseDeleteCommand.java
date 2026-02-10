package com.lxp.content.course.application.port.provider.command;

import com.lxp.common.application.cqrs.Command;

public record CourseDeleteCommand (
        String userId,
        String courseId
) implements Command {
}
