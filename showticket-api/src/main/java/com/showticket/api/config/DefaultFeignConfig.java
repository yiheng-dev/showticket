package com.showticket.api.config;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.showticket.api.client.fallback.ShowClientFallbackFactory;
import com.showticket.common.exception.BizIllegalException;
import com.showticket.common.utils.UserContext;
import feign.Logger;
import feign.RequestInterceptor;
import feign.RequestTemplate;
import feign.Response;
import feign.Util;
import feign.codec.ErrorDecoder;
import org.springframework.context.annotation.Bean;

public class DefaultFeignConfig {

    private static final ObjectMapper MAPPER = new ObjectMapper();

    @Bean
    public Logger.Level fullFeignLoggerLevel(){
        return Logger.Level.FULL;
    }

    @Bean
    public RequestInterceptor userInfoRequestInterceptor(){
        return new RequestInterceptor() {
            @Override
            public void apply(RequestTemplate template) {
                Long userId = UserContext.getUser();
                if (userId != null) {
                    template.header("user-info", userId.toString());
                }
            }
        };
    }

    @Bean
    public ErrorDecoder errorDecoder() {
        return (methodKey, response) -> {
            String body = "";
            try {
                if (response.body() != null) {
                    body = Util.toString(response.body().asReader());
                }
            } catch (Exception ignored) {
            }
            String msg = "远程服务调用失败";
            try {
                JsonNode node = MAPPER.readTree(body);
                if (node.hasNonNull("msg")) {
                    msg = node.get("msg").asText();
                } else if (node.hasNonNull("message")) {
                    msg = node.get("message").asText();
                } else if (node.hasNonNull("error")) {
                    msg = node.get("error").asText();
                }
            } catch (Exception ignored) {
            }
            return new BizIllegalException(msg);
        };
    }

    @Bean
    public ShowClientFallbackFactory showClientFallbackFactory(){
        return new ShowClientFallbackFactory();
    }
}
