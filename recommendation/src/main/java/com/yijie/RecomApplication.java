package com.yijie;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients
public class RecomApplication {
    public static void main(String[] args) {
        SpringApplication.run(RecomApplication.class, args);
    }
}
