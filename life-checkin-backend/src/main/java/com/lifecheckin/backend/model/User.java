package com.lifecheckin.backend.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

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

    @Column(nullable = false)
    private String roles;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(name = "created_Date_Time", nullable = false)
    private LocalDateTime createdDateTime = LocalDateTime.now();

    // 若有需要，可新增其他欄位，例如 email、姓名等等
}
