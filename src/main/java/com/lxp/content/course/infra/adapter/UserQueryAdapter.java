package com.lxp.content.course.infra.adapter;

import com.lxp.content.common.exception.ExternalApiException;
import com.lxp.content.common.exception.ExternalServiceException;
import com.lxp.content.course.application.port.required.UserQueryPort;
import com.lxp.content.course.application.port.required.dto.InstructorResult;
import com.lxp.content.course.common.exception.InstructorNotFoundException;
import com.lxp.content.course.infra.web.client.UserServiceFeignClient;
import com.lxp.content.course.infra.web.client.response.UserInfoResponse;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class UserQueryAdapter implements UserQueryPort {

    private static final String USER_SERVICE = "UserService";
    private final UserServiceFeignClient userServiceFeignClient;

    @Override
    @CircuitBreaker(name = "default", fallbackMethod = "fallback")
    public InstructorResult getInstructorInfo(String userId) {
        UserInfoResponse response = userServiceFeignClient.getUserInfo(userId);
        return toInstructorResult(response);
    }

    private InstructorResult toInstructorResult(UserInfoResponse response) {
        return new InstructorResult(
                response.id(),
                response.name(),
                response.role()
        );
    }

    private InstructorResult fallback(String userId, Throwable t) {
        if (t instanceof ExternalApiException e && e.getStatusCode() == 404) {
            throw new InstructorNotFoundException(userId);
        }
        throw new ExternalServiceException(USER_SERVICE, "서비스 연결 불가", t);
    }


}
