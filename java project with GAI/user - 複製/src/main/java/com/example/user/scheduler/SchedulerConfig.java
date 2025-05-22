package com.example.user.scheduler;

// 導入 Spring 框架中的相關類和註解。
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;

@Configuration // 註解為配置類，用於告訴 Spring 框架該類包含一個或多個 @Bean 定義，這些定義將由 Spring IoC 容器來處理並註冊為 Bean
@EnableScheduling // Spring 的計劃任務功能，用於告訴 Spring 框架去掃描 Spring 內置的 @Scheduled 註解，以支持定時任務的執行
public class SchedulerConfig {
    // Spring 框架自動掃描並啟用在應用程序中的定時任務。
}
