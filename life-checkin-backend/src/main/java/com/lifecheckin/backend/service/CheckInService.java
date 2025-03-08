package com.lifecheckin.backend.service;
import com.lifecheckin.backend.exception.ResourceNotFoundException;
import com.lifecheckin.backend.model.CheckIn;
import com.lifecheckin.backend.repository.CheckInRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class CheckInService {

    @Autowired
    private CheckInRepository checkInRepository;

    // 取得所有打卡紀錄
    public List<CheckIn> getAllCheckIns() {
        return checkInRepository.findAll();
    }

    // 根據 ID 取得打卡紀錄
    public Optional<CheckIn> getCheckInById(Long id) {
        return checkInRepository.findById(id);
    }

    // 建立打卡紀錄
    public CheckIn createCheckIn(CheckIn checkIn) {
        checkIn.setCheckinTime(LocalDateTime.now()); // 預設打卡時間為當下
        return checkInRepository.save(checkIn);
    }

    // 更新打卡紀錄
    public CheckIn updateCheckIn(Long id, CheckIn updatedCheckIn) {
        return checkInRepository.findById(id)
                .map(existingCheckIn -> {
                    existingCheckIn.setCheckinTime(updatedCheckIn.getCheckinTime());
                    return checkInRepository.save(existingCheckIn);
                }).orElseThrow(() -> new ResourceNotFoundException("CheckIn not found with id: " + id));
    }

    // 刪除打卡紀錄
    public void deleteCheckIn(Long id) {
        if (!checkInRepository.existsById(id)) {
            throw new ResourceNotFoundException("CheckIn not found with id: " + id);
        }
        checkInRepository.deleteById(id);
    }
}
