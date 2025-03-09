package com.lifecheckin.backend.service;

import com.lifecheckin.backend.exception.InvalidInputException;
import com.lifecheckin.backend.exception.ResourceNotFoundException;
import com.lifecheckin.backend.exception.UserAlreadyExistsException;
import com.lifecheckin.backend.model.User;
import com.lifecheckin.backend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * 用戶服務類
 * 處理用戶相關的業務邏輯
 */
@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    /**
     * 獲取所有用戶
     * @return 用戶列表
     */
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    /**
     * 根據ID獲取用戶
     * @param id 用戶ID
     * @return 用戶Optional
     */
    public Optional<User> getUserById(Long id) {
        return userRepository.findById(id);
    }

    /**
     * 創建新用戶
     * @param user 用戶對象
     * @return 創建的用戶
     */
    @Transactional
    public User createUser(User user) {
        // 檢查用戶名是否已存在
        if(userRepository.findByUsername(user.getUsername()).isPresent()) {
            throw new UserAlreadyExistsException("Username already exists: " + user.getUsername());
        }

        // 檢查郵箱是否已存在
        if(user.getEmail() != null && userRepository.findByEmail(user.getEmail()).isPresent()) {
            throw new UserAlreadyExistsException("Email already exists: " + user.getEmail());
        }

        // 加密密碼
        user.setPassword(bCryptPasswordEncoder.encode(user.getPassword()));

        // 確保用戶有角色
        if(user.getRoles() == null || user.getRoles().isEmpty()) {
            user.setRoles("ROLE_USER");
        }

        return userRepository.save(user);
    }

    /**
     * 註冊新用戶
     * @param username 用戶名
     * @param email 電子郵件
     * @param rawPassword 原始密碼
     * @return 註冊成功的用戶
     */
    @Transactional
    public User registerUser(String username, String email, String rawPassword) {
        // 輸入驗證
        if (username == null || username.trim().isEmpty()) {
            throw new InvalidInputException("Username cannot be empty");
        }
        if (email == null || email.trim().isEmpty() || !email.contains("@")) {
            throw new InvalidInputException("Valid email is required");
        }
        if (rawPassword == null || rawPassword.length() < 6) {
            throw new InvalidInputException("Password must be at least 6 characters");
        }

        // 檢查用戶名是否已存在
        if (userRepository.findByUsername(username).isPresent()) {
            throw new UserAlreadyExistsException("Username already exists: " + username);
        }

        // 檢查郵箱是否已存在
        if (userRepository.findByEmail(email).isPresent()) {
            throw new UserAlreadyExistsException("Email already exists: " + email);
        }

        // 創建新用戶
        User user = new User();
        user.setUsername(username);
        user.setEmail(email);
        user.setPassword(bCryptPasswordEncoder.encode(rawPassword));
        user.setRoles("ROLE_USER");

        return userRepository.save(user);
    }

    /**
     * 檢查用戶名是否存在
     * @param username 用戶名
     * @return 是否存在
     */
    public boolean existsByUsername(String username) {
        return userRepository.findByUsername(username).isPresent();
    }

    /**
     * 根據用戶名查找用戶
     * @param username 用戶名
     * @return 用戶Optional
     */
    public Optional<User> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    /**
     * 更新用戶信息
     * @param id 用戶ID
     * @param updatedUser 更新的用戶信息
     * @return 更新後的用戶
     */
    @Transactional
    public User updateUser(Long id, User updatedUser) {
        return userRepository.findById(id)
                .map(existingUser -> {
                    // 更新用戶名（如果有變更且不與其他用戶衝突）
                    if(updatedUser.getUsername() != null && !updatedUser.getUsername().equals(existingUser.getUsername())) {
                        if(userRepository.findByUsername(updatedUser.getUsername()).isPresent()) {
                            throw new UserAlreadyExistsException("Username already exists: " + updatedUser.getUsername());
                        }
                        existingUser.setUsername(updatedUser.getUsername());
                    }

                    // 更新郵箱（如果有變更且不與其他用戶衝突）
                    if(updatedUser.getEmail() != null && !updatedUser.getEmail().equals(existingUser.getEmail())) {
                        if(userRepository.findByEmail(updatedUser.getEmail()).isPresent()) {
                            throw new UserAlreadyExistsException("Email already exists: " + updatedUser.getEmail());
                        }
                        existingUser.setEmail(updatedUser.getEmail());
                    }

                    // 更新密碼（如果有提供）
                    if(updatedUser.getPassword() != null && !updatedUser.getPassword().isEmpty()) {
                        existingUser.setPassword(bCryptPasswordEncoder.encode(updatedUser.getPassword()));
                    }

                    // 更新角色（如果有變更且有適當權限）
                    if(updatedUser.getRoles() != null && !updatedUser.getRoles().isEmpty()) {
                        existingUser.setRoles(updatedUser.getRoles());
                    }

                    return userRepository.save(existingUser);
                }).orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));
    }

    /**
     * 刪除用戶
     * @param id 用戶ID
     */
    @Transactional
    public void deleteUser(Long id) {
        if (!userRepository.existsById(id)) {
            throw new ResourceNotFoundException("User not found with id: " + id);
        }
        userRepository.deleteById(id);
    }
}