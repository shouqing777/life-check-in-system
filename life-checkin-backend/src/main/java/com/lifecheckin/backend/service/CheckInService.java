package com.lifecheckin.backend.service;

import com.lifecheckin.backend.exception.InvalidInputException;
import com.lifecheckin.backend.exception.ResourceNotFoundException;
import com.lifecheckin.backend.model.CheckIn;
import com.lifecheckin.backend.model.User;
import com.lifecheckin.backend.repository.CheckInRepository;
import com.lifecheckin.backend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

/**
 * 打卡服務類
 * 處理打卡相關的業務邏輯
 */
@Service
public class CheckInService {

    @Autowired
    private CheckInRepository checkInRepository;

    @Autowired
    private UserRepository userRepository;

    /**
     * 獲取所有打卡記錄
     * @return 打卡記錄列表
     */
    public List<CheckIn> getAllCheckIns() {
        return checkInRepository.findAll();
    }

    /**
     * 根據ID獲取打卡記錄
     * @param id 打卡記錄ID
     * @return 打卡記錄Optional
     */
    public Optional<CheckIn> getCheckInById(Long id) {
        return checkInRepository.findById(id);
    }

    /**
     * 創建打卡記錄
     * @param checkIn 打卡記錄對象
     * @return 創建的打卡記錄
     */
    @Transactional
    public CheckIn createCheckIn(CheckIn checkIn) {
        // 驗證用戶
        User user = checkIn.getUser();
        if (user == null || user.getId() == null) {
            throw new InvalidInputException("User information is required for check-in");
        }

        // 檢查用戶是否存在
        User existingUser = userRepository.findById(user.getId())
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + user.getId()));

        // 檢查今天是否已經打卡
        LocalDate today = LocalDate.now();
        LocalDateTime startOfDay = today.atStartOfDay();
        LocalDateTime endOfDay = today.atTime(LocalTime.MAX);

        Optional<CheckIn> existingCheckIn = checkInRepository.findByUserAndCheckinTimeBetween(
                existingUser, startOfDay, endOfDay);

        if (existingCheckIn.isPresent()) {
            throw new InvalidInputException("You have already checked in today");
        }

        // 設置當前時間作為打卡時間
        checkIn.setCheckinTime(LocalDateTime.now());

        // 設置用戶
        checkIn.setUser(existingUser);

        // 保存打卡記錄
        CheckIn savedCheckIn = checkInRepository.save(checkIn);

        // 更新用戶的最後打卡日期和連續打卡天數
        updateUserCheckInStatus(existingUser);

        return savedCheckIn;
    }

    /**
     * 更新用戶的打卡狀態
     * @param user 用戶
     */
    private void updateUserCheckInStatus(User user) {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime previousCheckIn = user.getLastCheckInDate();

        // 如果之前有打卡記錄
        if (previousCheckIn != null) {
            LocalDate previousDate = previousCheckIn.toLocalDate();
            LocalDate today = now.toLocalDate();
            LocalDate yesterday = today.minusDays(1);

            // 如果上次打卡是昨天，增加連續打卡天數
            if (previousDate.isEqual(yesterday)) {
                user.setStreakDays(user.getStreakDays() + 1);
            }
            // 如果不是昨天，且不是今天，重置連續打卡天數
            else if (!previousDate.isEqual(today)) {
                user.setStreakDays(1);
            }
        } else {
            // 第一次打卡
            user.setStreakDays(1);
        }

        // 更新最後打卡日期
        user.setLastCheckInDate(now);

        // 保存用戶
        userRepository.save(user);
    }

    /**
     * 更新打卡記錄
     * @param id 打卡記錄ID
     * @param updatedCheckIn 更新的打卡記錄
     * @return 更新後的打卡記錄
     */
    @Transactional
    public CheckIn updateCheckIn(Long id, CheckIn updatedCheckIn) {
        return checkInRepository.findById(id)
                .map(existingCheckIn -> {
                    // 只允許修改特定字段，不允許修改打卡用戶和時間
                    // 可以根據業務需求調整允許修改的字段

                    return checkInRepository.save(existingCheckIn);
                }).orElseThrow(() -> new ResourceNotFoundException("CheckIn not found with id: " + id));
    }

    /**
     * 刪除打卡記錄
     * @param id 打卡記錄ID
     */
    @Transactional
    public void deleteCheckIn(Long id) {
        if (!checkInRepository.existsById(id)) {
            throw new ResourceNotFoundException("CheckIn not found with id: " + id);
        }
        checkInRepository.deleteById(id);
    }

    /**
     * 獲取用戶的打卡記錄
     * @param userId 用戶ID
     * @return 打卡記錄列表
     */
    public List<CheckIn> getUserCheckIns(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));

        // 這裡需要在 CheckInRepository 中添加按用戶查詢的方法
        return checkInRepository.findByUserOrderByCheckinTimeDesc(user);
    }

    /**
     * 檢查用戶今日是否已打卡
     * @param userId 用戶ID
     * @return 是否已打卡
     */
    public boolean hasCheckedInToday(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));

        LocalDate today = LocalDate.now();
        LocalDateTime startOfDay = today.atStartOfDay();
        LocalDateTime endOfDay = today.atTime(LocalTime.MAX);

        return checkInRepository.findByUserAndCheckinTimeBetween(user, startOfDay, endOfDay).isPresent();
    }
}