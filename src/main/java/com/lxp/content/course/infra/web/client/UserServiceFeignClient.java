package com.lxp.content.course.infra.web.client;

import com.lxp.content.course.infra.web.client.response.UserInfoResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "userService", url = "${services.user.url}")
public interface UserServiceFeignClient {

    @GetMapping("/internal/api-v1/users/{userId}")
    UserInfoResponse getUserInfo(@PathVariable String userId);

}
