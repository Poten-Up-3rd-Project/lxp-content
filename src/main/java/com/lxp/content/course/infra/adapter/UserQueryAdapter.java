package com.lxp.content.course.infra.adapter;

import com.lxp.content.course.application.port.required.UserQueryPort;
import com.lxp.content.course.application.port.required.dto.InstructorResult;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UserQueryAdapter implements UserQueryPort {

    @Override
    public InstructorResult getInstructorInfo(String userId) {
        return null;
    }
}
