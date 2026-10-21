package com.showticket.selection;

import com.showticket.api.config.DefaultFeignConfig;
import com.showticket.common.advice.CommonExceptionAdvice;
import com.showticket.selection.config.LoadBalancerConfiguration;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.loadbalancer.annotation.LoadBalancerClients;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.web.client.RestTemplate;

@LoadBalancerClients(defaultConfiguration = LoadBalancerConfiguration.class)
@EnableFeignClients(basePackages = "com.showticket.api.client", defaultConfiguration = DefaultFeignConfig.class)
@MapperScan("com.showticket.selection.mapper")
@SpringBootApplication
@Import(CommonExceptionAdvice.class)
public class SelectionApplication {
    public static void main(String[] args) {
        SpringApplication.run(SelectionApplication.class, args);
    }

    @Bean
    public RestTemplate restTemplate(){
        return new RestTemplate();
    }
}