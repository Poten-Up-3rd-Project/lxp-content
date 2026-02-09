package com.lxp.content.common.passport;

import com.lxp.passport.core.support.PassportHeaderProvider;
import feign.RequestInterceptor;
import feign.RequestTemplate;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class FeignHeaderForwardInterceptor implements RequestInterceptor {

    private final PassportHeaderProvider provider;

    public FeignHeaderForwardInterceptor(PassportHeaderProvider provider) {
        this.provider = provider;
    }

    @Override
    public void apply(RequestTemplate requestTemplate) {
        provider.headers().forEach(requestTemplate::header);
    }
}
