package com.example.user.user;

import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.JpaRepository; // JpaRepository：提供基本的 CRUD 操作方法
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query; // JPQL（Java Persistence Query Language） 定義一個查詢語句
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.time.LocalDateTime;
import java.util.List;
@Repository // 啟用自動組件掃描和注入
// UserRepository 繼承自 JpaRepository，指定實體類型為 UserModel，主鍵類型為 Integer。
public interface UserRepository extends JpaRepository<UserModel, Integer> {
    List<UserModel> findByIpAddress(String ipAddress); // 義一個方法根據 ipAddress 查找用戶，返回符合條件的用戶列表
    Optional<UserModel> findByUserid(String userid); // 定義一個方法根據 userid 查找用戶，返回一個包含或不包含用戶的 Optional 對象
   
    @Modifying // 標記該方法執行更新操作
    @Transactional // 標記該方法需要在事務上下文中運行，以確保數據一致性
    @Query("UPDATE UserModel u SET u.ipAddress = null WHERE u.signInTime < :threeHoursAgo") // 查詢更新 UserModel 中 signInTime 早於 threeHoursAgo 的記錄，將其 ipAddress 字段設置為 null
    void clearIpAddressBefore(LocalDateTime threeHoursAgo); // 根據查詢條件執行更新操作
    
}

