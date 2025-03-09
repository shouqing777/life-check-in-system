package com.lifecheckin.backend.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

/**
 * User 實體代表生命狀態打卡系統中的使用者。
 * 使用 JPA 註解定義資料庫映射，並利用 Lombok 減少樣板程式碼。
 */
@Entity
@Data
@NoArgsConstructor
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String username;

    @Column(nullable = false)
    private String password;

    // 用戶角色，使用逗號分隔
    @Column(nullable = false)
    private String roles = "ROLE_USER";  // 設定默認值為普通用戶

    // 使用者電子郵件
    @Column(nullable = false, unique = true)
    private String email;

    // 打卡記錄，與CheckIn實體形成一對多關係
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<CheckIn> checkIns = new HashSet<>();

    @Column(name = "created_Date_Time", nullable = false)
    private LocalDateTime createdDateTime = LocalDateTime.now();

    // 方便創建用戶的構造函數
    public User(String username, String email, String password) {
        this.username = username;
        this.email = email;
        this.password = password;
    }

    // 連續打卡天數（可選）
    @Column(name = "streak_days")
    private Integer streakDays = 0;

    // 最後打卡日期（可選）
    @Column(name = "last_check_in_date")
    private LocalDateTime lastCheckInDate;
}