package com.showticket.show;

import com.showticket.common.advice.CommonExceptionAdvice;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;

@MapperScan("com.showticket.show.mapper")
@SpringBootApplication
@Import(CommonExceptionAdvice.class)
public class ShowApplication {
    public static void main(String[] args) {
        SpringApplication.run(ShowApplication.class, args);
    }
}