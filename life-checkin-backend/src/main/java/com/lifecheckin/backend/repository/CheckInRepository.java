package com.lifecheckin.backend.repository;

import com.lifecheckin.backend.model.CheckIn;
import com.lifecheckin.backend.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * CheckInRepository 提供對 CheckIn 實體的資料庫操作。
 * 繼承 JpaRepository 以獲取標準 CRUD 功能。
 */
@Repository
public interface CheckInRepository extends JpaRepository<CheckIn, Long> {

    /**
     * 查找用戶在指定時間範圍內的打卡記錄
     * @param user 用戶
     * @param start 開始時間
     * @param end 結束時間
     * @return 打卡記錄Optional
     */
    Optional<CheckIn> findByUserAndCheckinTimeBetween(User user, LocalDateTime start, LocalDateTime end);

    /**
     * 查找用戶的所有打卡記錄，按時間倒序排列
     * @param user 用戶
     * @return 打卡記錄列表
     */
    List<CheckIn> findByUserOrderByCheckinTimeDesc(User user);

    /**
     * 查找用戶在指定日期的打卡記錄
     * @param user 用戶
     * @param date 日期
     * @return 打卡記錄列表
     */
    @Query("SELECT c FROM CheckIn c WHERE c.user = :user AND DATE(c.checkinTime) = DATE(:date)")
    List<CheckIn> findByUserAndDate(User user, LocalDateTime date);

    /**
     * 統計用戶的打卡總數
     * @param user 用戶
     * @return 打卡總數
     */
    long countByUser(User user);
}