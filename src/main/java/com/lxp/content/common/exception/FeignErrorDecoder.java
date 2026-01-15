package com.lxp.content.common.exception;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import feign.Response;
import feign.Util;
import feign.codec.ErrorDecoder;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;

@Component
@RequiredArgsConstructor
public class FeignErrorDecoder implements ErrorDecoder {

    private final ObjectMapper objectMapper;
    private final ErrorDecoder defaultDecoder = new Default();

    @Override
    public Exception decode(String methodKey, Response response) {
        String serviceName = extractServiceName(methodKey);
        int status = response.status();
        String message = extractMessage(response);

        return switch (status) {
            case 400 -> new ExternalApiException(serviceName, status, "잘못된 요청: " + message);
            case 404 -> new ExternalApiException(serviceName, status, "리소스 없음: " + message);
            case 401, 403 -> new ExternalApiException(serviceName, status, "인증/인가 실패");
            case 500, 502, 503 -> new ExternalServiceException(serviceName, "서비스 장애: " + message);
            default -> defaultDecoder.decode(methodKey, response);
        };
    }

    private String extractServiceName(String methodKey) {
        return methodKey.split("#")[0].replace("FeignClient", "");
    }

    private String extractMessage(Response response) {
        try {
            if (response.body() == null) {
                return "응답 없음";
            }
            String body = Util.toString(response.body().asReader(StandardCharsets.UTF_8));

            JsonNode node = objectMapper.readTree(body);
            if (node.has("error") && node.get("error").has("message")) {
                return node.get("error").get("message").asText();
            }
            return body;
        } catch (Exception e) {
            return "메시지 파싱 실패";
        }
    }

}