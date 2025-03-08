package com.lifecheckin.backend.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

/**
 * CheckIn 實體代表使用者的打卡記錄。
 * 每次打卡會記錄使用者 ID 以及打卡時間。
 */
@Entity
@Data
@NoArgsConstructor
@Table(name = "checkin_records")
public class CheckIn {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 表示哪個使用者進行打卡
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    // 打卡的時間點
    @Column(name = "checkin_time", nullable = false)
    private LocalDateTime checkinTime = LocalDateTime.now();
}
