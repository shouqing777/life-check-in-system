package com.lifecheckin.backend.repository;

import com.lifecheckin.backend.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * UserRepository 提供對 User 實體的資料庫操作。
 * 繼承 JpaRepository 後，自動獲得常用的 CRUD 方法。
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    // 如有需要，可新增自定義查詢方法
    Optional<User> findByUsername(String username);
    Optional<User> findByEmail(String email);
}
