package com.example.user;
// 為Spring Boot 應用的主類別
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication; // 組合註解，包含了 @SpringBootConfiguration、@EnableAutoConfiguration 和 @ComponentScan
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestTemplate;

@SpringBootApplication // 用於告訴 Spring Boot 這是一個應用程序，並啟用自動配置和組件掃描
public class DynamicQRCodedeApplication {
    // 啟動 Spring Boot 應用程序
    public static void main(String[] args) {
        SpringApplication.run(DynamicQRCodedeApplication.class, args);
    }

    @Bean
    //  Spring 提供的同步 HTTP 客戶端，用於簡化與 RESTful 服務的交互
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
}