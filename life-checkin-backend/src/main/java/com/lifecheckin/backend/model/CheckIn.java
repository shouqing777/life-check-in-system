package com.lifecheckin.backend.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

/**
 * CheckIn 實體代表使用者的打卡記錄。
 * 每次打卡會記錄使用者 ID、打卡時間以及相關狀態。
 */
@Entity
@Data
@NoArgsConstructor
@Table(name = "checkin_records", indexes = {
        // 為使用者ID和打卡時間創建索引，優化查詢效能
        @Index(name = "idx_user_checkin_time", columnList = "user_id, checkin_time")
})
public class CheckIn {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 打卡關聯的使用者
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    /**
     * 打卡的時間點
     */
    @Column(name = "checkin_time", nullable = false)
    private LocalDateTime checkinTime = LocalDateTime.now();

    /**
     * 打卡的備註信息
     */
    @Column(name = "note", length = 255)
    private String note;

    /**
     * 打卡的位置信息（可選）
     */
    @Column(name = "location")
    private String location;

    /**
     * 打卡的狀態：NORMAL(正常), LATE(遲到), EARLY(早退)
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private CheckInStatus status = CheckInStatus.NORMAL;

    /**
     * 創建打卡記錄時的預處理
     */
    @PrePersist
    protected void onCreate() {
        if (checkinTime == null) {
            checkinTime = LocalDateTime.now();
        }
    }

    /**
     * 打卡狀態枚舉
     */
    public enum CheckInStatus {
        NORMAL, LATE, EARLY
    }
}