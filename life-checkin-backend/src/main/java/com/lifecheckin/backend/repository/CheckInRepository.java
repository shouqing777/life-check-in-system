package com.lifecheckin.backend.repository;

import com.lifecheckin.backend.model.CheckIn;
import com.lifecheckin.backend.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;

/**
 * CheckInRepository 提供對 CheckIn 實體的資料庫操作。
 * 繼承 JpaRepository 以獲取標準 CRUD 功能。
 */
@Repository
public interface CheckInRepository extends JpaRepository<CheckIn, Long> {
    // 可根據需求新增額外的查詢方法

    Optional<CheckIn> findByUserAndCheckinTimeBetween(User user,LocalDateTime start, LocalDateTime end);


}
