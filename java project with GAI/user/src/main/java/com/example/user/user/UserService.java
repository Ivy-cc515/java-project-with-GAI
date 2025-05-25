package com.example.user.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.transaction.annotation.Transactional;
import jakarta.servlet.http.HttpServletRequest; // HttpServletRequest：處理 HTTP 請求，獲取客戶端信息
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException; // ConstraintViolationException：處理驗證錯誤
import jakarta.validation.Validator;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service // 註解為 Spring 服務層
public class UserService {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private Validator validator;

    @Autowired
    private HttpServletRequest request;

    // 用於新增用戶。使用 validator 驗證用戶，若存在驗證錯誤則拋出 ConstraintViolationException。若無錯誤保存用戶對象到資料庫，返回新用戶的 ID
    public Integer addUser(UserModel user) {
        Set<ConstraintViolation<UserModel>> violations = validator.validate(user);
        if (!violations.isEmpty()) {
            StringBuilder sb = new StringBuilder();
            for (ConstraintViolation<UserModel> constraintViolation : violations) {
                sb.append(constraintViolation.getMessage());
            }
            throw new ConstraintViolationException(sb.toString(), violations);
        }
        UserModel newUser = userRepository.save(user);
        return newUser.getId();
    }

    // 用於獲取所有用戶列表，呼叫 userRepository.findAll() 方法
    public List<UserModel> getAllUsers() {
        return userRepository.findAll();
    }

    // 用於用戶簽到
    public UserModel signIn(UserModel user) throws Exception {
        // 獲取IP地址
        String ipAddress = getClientIpAddress(request);
        
        // 檢查IP地址是否已經簽到過，如果已簽到過則拋出例外
        List<UserModel> usersWithSameIp = userRepository.findByIpAddress(ipAddress);
        if (!usersWithSameIp.isEmpty()) {
            throw new Exception("此IP地址已經簽到過,無法再次簽到");
        }

        Optional<UserModel> existingUser = userRepository.findByUserid(user.getUserid());
        if (existingUser.isPresent()) {  // 根據用戶 ID 查找是否存在相同用戶，如果存在更新並保存用戶的簽到信息
            UserModel updateUser = existingUser.get();
            if (updateUser.getUsername().equals(user.getUsername())) {
                updateUser.setSignInTime(LocalDateTime.now());
                updateUser.setLatitude(user.getLatitude());
                updateUser.setLongitude(user.getLongitude());
                updateUser.setIpAddress(ipAddress);
                updateUser.setSignedIn(true);
                if (updateUser.getSignInCount() == null) {
                    updateUser.setSignInCount(1); // 如果簽到次數為null，設置為1
                } else {
                    updateUser.setSignInCount(updateUser.getSignInCount() + 1); // 否則簽到次數加一
                }
                return userRepository.save(updateUser);
            } else { 
                throw new Exception("Username does not match the existing record.");
            }
        } else { // 如果用戶不存在，設置簽到信息並保存新用戶
            user.setSignInTime(LocalDateTime.now());
            user.setSignedIn(true);
            user.setSignInCount(1);
            user.setIpAddress(ipAddress);
            return userRepository.save(user);
            
        }
    }

    // 用於從請求中獲取客戶端 IP 地址
    private String getClientIpAddress(HttpServletRequest request) {
        String ipAddress = request.getHeader("X-Forwarded-For");
        if (ipAddress == null || ipAddress.isEmpty() || "unknown".equalsIgnoreCase(ipAddress)) {
            ipAddress = request.getHeader("Proxy-Client-IP");
        }
        if (ipAddress == null || ipAddress.isEmpty() || "unknown".equalsIgnoreCase(ipAddress)) {
            ipAddress = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ipAddress == null || ipAddress.isEmpty() || "unknown".equalsIgnoreCase(ipAddress)) {
            ipAddress = request.getRemoteAddr();
        }
        if ("0:0:0:0:0:0:0:1".equals(ipAddress)) { // 如果是本地地址(IPv6) 0:0:0:0:0:0:0:1，則轉換為(IPv4) 127.0.0.1
            ipAddress = "127.0.0.1";
        }
        return ipAddress;
    }

    // 定時清空三小時前的簽到記錄
    @Transactional
    @Scheduled(fixedRate = 3600000) // 定時任務，每小時執行一次
    public void clearExpiredRecords() {
        LocalDateTime threeHoursAgo = LocalDateTime.now().minusHours(3);
        userRepository.clearIpAddressBefore(threeHoursAgo);
    }
}
