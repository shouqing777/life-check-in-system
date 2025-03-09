package com.lifecheckin.backend.controller;

import com.lifecheckin.backend.exception.ResourceNotFoundException;
import com.lifecheckin.backend.model.CheckIn;
import com.lifecheckin.backend.model.User;
import com.lifecheckin.backend.service.CheckInService;
import com.lifecheckin.backend.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 打卡控制器
 * 處理打卡相關的HTTP請求
 */
@RestController
@RequestMapping("/api/checkins")
@Tag(name = "CheckIn API", description = "API for managing check-in records")
public class CheckInController {

    @Autowired
    private CheckInService checkInService;

    @Autowired
    private UserService userService;

    /**
     * 獲取所有打卡記錄
     * @return 打卡記錄列表
     */
    @GetMapping
    @Operation(summary = "Get all check-ins", description = "Retrieve a list of all check-in records")
    @ApiResponse(responseCode = "200", description = "Successfully retrieved list",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = CheckIn.class)))
    public ResponseEntity<List<CheckIn>> getAllCheckIns() {
        return ResponseEntity.ok(checkInService.getAllCheckIns());
    }

    /**
     * 根據ID獲取打卡記錄
     * @param id 打卡記錄ID
     * @return 打卡記錄
     */
    @GetMapping("/{id}")
    @Operation(summary = "Get a check-in by ID", description = "Retrieve a single check-in record by its ID")
    @ApiResponse(responseCode = "200", description = "Successfully retrieved check-in",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = CheckIn.class)))
    @ApiResponse(responseCode = "404", description = "CheckIn not found", content = @Content)
    public ResponseEntity<CheckIn> getCheckInById(@PathVariable Long id) {
        CheckIn checkIn = checkInService.getCheckInById(id)
                .orElseThrow(() -> new ResourceNotFoundException("CheckIn not found with id: " + id));
        return ResponseEntity.ok(checkIn);
    }

    /**
     * 創建打卡記錄
     * @return 創建的打卡記錄
     */
    @PostMapping
    @Operation(summary = "Create a new check-in", description = "Create a new check-in record for the authenticated user")
    @ApiResponse(responseCode = "201", description = "CheckIn created successfully",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = CheckIn.class)))
    public ResponseEntity<CheckIn> createCheckIn() {
        // 獲取當前認證用戶
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();

        // 獲取用戶
        User user = userService.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with username: " + username));

        // 創建打卡記錄
        CheckIn checkIn = new CheckIn();
        checkIn.setUser(user);

        CheckIn savedCheckIn = checkInService.createCheckIn(checkIn);
        return ResponseEntity.status(201).body(savedCheckIn);
    }

    /**
     * 檢查今日是否已打卡
     * @return 是否已打卡
     */
    @GetMapping("/today")
    @Operation(summary = "Check if user has checked in today", description = "Check if the authenticated user has already checked in today")
    @ApiResponse(responseCode = "200", description = "Successfully checked")
    public ResponseEntity<Boolean> hasCheckedInToday() {
        // 獲取當前認證用戶
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();

        // 獲取用戶
        User user = userService.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with username: " + username));

        // 檢查今日是否已打卡
        boolean hasCheckedIn = checkInService.hasCheckedInToday(user.getId());
        return ResponseEntity.ok(hasCheckedIn);
    }

    /**
     * 獲取用戶的打卡記錄
     * @return 打卡記錄列表
     */
    @GetMapping("/my")
    @Operation(summary = "Get user's check-ins", description = "Retrieve the authenticated user's check-in records")
    @ApiResponse(responseCode = "200", description = "Successfully retrieved list",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = CheckIn.class)))
    public ResponseEntity<List<CheckIn>> getMyCheckIns() {
        // 獲取當前認證用戶
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();

        // 獲取用戶
        User user = userService.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with username: " + username));

        // 獲取用戶的打卡記錄
        List<CheckIn> checkIns = checkInService.getUserCheckIns(user.getId());
        return ResponseEntity.ok(checkIns);
    }

    /**
     * 刪除打卡記錄
     * @param id 打卡記錄ID
     * @return 操作結果
     */
    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a check-in", description = "Delete a check-in record by its ID")
    @ApiResponse(responseCode = "204", description = "CheckIn deleted successfully", content = @Content)
    @ApiResponse(responseCode = "404", description = "CheckIn not found", content = @Content)
    public ResponseEntity<Void> deleteCheckIn(@PathVariable Long id) {
        checkInService.deleteCheckIn(id);
        return ResponseEntity.noContent().build();
    }
}