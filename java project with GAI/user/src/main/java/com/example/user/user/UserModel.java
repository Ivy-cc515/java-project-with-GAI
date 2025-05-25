package com.example.user.user;
// 使用 JPA（Java Persistence API）和 Hibernate 的實體類，用於表示數據庫中的用戶記錄
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.time.LocalDateTime;

@Entity //標記該類別為 JPA 實體，對應資料庫中的Table
@Table(name = "user") // 指定 Table 名稱為"user"
@Getter @Setter // Lombok 註解，自動生成該類別所有屬性的 getter 和 setter 方法
public class UserModel {
    @Id // 標記為主鍵
    @GeneratedValue(strategy = GenerationType.IDENTITY) // 指定主鍵生成策略為自動增長（identity）
    private int id;

    @Column(unique = true) // 標記該列值必須唯一
    @NotBlank(message = "姓名不可為空")
    private String username;

    @Column(unique = true)
    @NotBlank(message = "學號不可為空") // Jakarta Validation 註解，確保該屬性值不為空
    @Size(min = 7, max = 7, message = "學號為7位數") // 註解同上，該屬性值的長度為 7 位數
    private String userid;

    @Column
    private String latitude;

    @Column
    private String longitude;

    @Column
    private Boolean signedIn;

    @Column
    private LocalDateTime signInTime;

    @Column
    private Integer SignInCount;

    @Column
    private String ipAddress; 

    @Column
    private Integer score;
}
