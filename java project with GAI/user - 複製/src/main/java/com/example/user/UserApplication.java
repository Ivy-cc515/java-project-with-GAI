package com.example.user;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@ComponentScan({ // 查找 Spring 管理的組件
        "com.example",
})
@EnableJpaRepositories({ // 啟用 JPA 資料庫，查找帶有 @Repository 註解的port
        "com.example",
})
@EntityScan({ // 查找帶有 @Entity 註解的 JPA 實體類
        "com.example",
})
public class UserApplication {
    // 啟動 Spring Boot 應用程序
    public static void main(String[] args) {
        SpringApplication.run(UserApplication.class, args);
    }
}
