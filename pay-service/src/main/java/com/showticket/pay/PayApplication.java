package com.showticket.pay;

import com.showticket.api.config.DefaultFeignConfig;
import com.showticket.common.advice.CommonExceptionAdvice;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Import;

@EnableFeignClients(basePackages = "com.showticket.api.client", defaultConfiguration = DefaultFeignConfig.class)
@MapperScan("com.showticket.pay.mapper")
@SpringBootApplication
@Import(CommonExceptionAdvice.class)
public class PayApplication {
    public static void main(String[] args) {
        SpringApplication.run(PayApplication.class, args);
    }
}