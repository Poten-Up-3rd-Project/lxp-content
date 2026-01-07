package com.lxp.content.course.application.port.provider.usecase.command;

import com.lxp.common.application.port.in.UseCase;
import com.lxp.content.course.application.port.provider.command.CourseCreateCommand;
import com.lxp.content.course.application.port.provider.view.CourseDetailView;

public interface CourseCreateUseCase extends UseCase<CourseCreateCommand, CourseDetailView> {}
