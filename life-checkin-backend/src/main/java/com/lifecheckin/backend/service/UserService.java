package com.lifecheckin.backend.service;

import com.lifecheckin.backend.exception.InvalidInputException;
import com.lifecheckin.backend.exception.ResourceNotFoundException;
import com.lifecheckin.backend.exception.UserAlreadyExistsException;
import com.lifecheckin.backend.model.User;
import com.lifecheckin.backend.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

    private static final Logger logger = LoggerFactory.getLogger(UserService.class);

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    /**
     * 獲取所有用戶
     * @return 用戶列表
     */
    public List<User> getAllUsers() {
        logger.debug("獲取所有用戶");
        return userRepository.findAll();
    }

    /**
     * 根據ID獲取用戶
     * @param id 用戶ID
     * @return 用戶Optional
     */
    public Optional<User> getUserById(Long id) {
        logger.debug("根據ID獲取用戶: {}", id);
        return userRepository.findById(id);
    }

    /**
     * 創建新用戶
     * @param user 用戶對象
     * @return 創建的用戶
     */
    @Transactional
    public User createUser(User user) {
        logger.info("開始創建新用戶: {}", user.getUsername());

        // 檢查用戶名是否已存在
        if(userRepository.findByUsername(user.getUsername()).isPresent()) {
            logger.warn("用戶名已存在: {}", user.getUsername());
            throw new UserAlreadyExistsException("Username already exists: " + user.getUsername());
        }

        // 檢查郵箱是否已存在
        if(user.getEmail() != null && userRepository.findByEmail(user.getEmail()).isPresent()) {
            logger.warn("郵箱已存在: {}", user.getEmail());
            throw new UserAlreadyExistsException("Email already exists: " + user.getEmail());
        }

        // 加密密碼 - 確保使用加密
        String rawPassword = user.getPassword();
        String encodedPassword = bCryptPasswordEncoder.encode(rawPassword);
        user.setPassword(encodedPassword);
        logger.debug("密碼已加密，原始長度: {}, 加密後長度: {}",
                rawPassword.length(), encodedPassword.length());

        // 確保用戶有角色
        if(user.getRoles() == null || user.getRoles().isEmpty()) {
            user.setRoles("ROLE_USER");
            logger.debug("設置默認角色為 ROLE_USER");
        }

        // 保存用戶
        User savedUser = userRepository.save(user);
        logger.info("用戶創建成功: {}, ID: {}", savedUser.getUsername(), savedUser.getId());
        return savedUser;
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
        logger.info("開始註冊新用戶: {}", username);

        // 輸入驗證
        if (username == null || username.trim().isEmpty()) {
            logger.error("註冊失敗: 用戶名為空");
            throw new InvalidInputException("Username cannot be empty");
        }
        if (email == null || email.trim().isEmpty() || !email.contains("@")) {
            logger.error("註冊失敗: 郵箱無效: {}", email);
            throw new InvalidInputException("Valid email is required");
        }
        if (rawPassword == null || rawPassword.length() < 6) {
            logger.error("註冊失敗: 密碼長度不足");
            throw new InvalidInputException("Password must be at least 6 characters");
        }

        // 檢查用戶名是否已存在
        if (userRepository.findByUsername(username).isPresent()) {
            logger.warn("註冊失敗: 用戶名已存在: {}", username);
            throw new UserAlreadyExistsException("Username already exists: " + username);
        }

        // 檢查郵箱是否已存在
        if (userRepository.findByEmail(email).isPresent()) {
            logger.warn("註冊失敗: 郵箱已存在: {}", email);
            throw new UserAlreadyExistsException("Email already exists: " + email);
        }

        // 創建新用戶
        User user = new User();
        user.setUsername(username);
        user.setEmail(email);

        // 加密密碼 - 明確記錄
        String encodedPassword = bCryptPasswordEncoder.encode(rawPassword);
        user.setPassword(encodedPassword);
        logger.debug("密碼已加密，原始密碼長度: {}, 加密後密碼長度: {}",
                rawPassword.length(), encodedPassword.length());

        user.setRoles("ROLE_USER");

        // 保存用戶
        User savedUser = userRepository.save(user);
        logger.info("用戶註冊成功: {}, ID: {}", savedUser.getUsername(), savedUser.getId());

        // 檢查保存後的用戶
        Optional<User> checkUser = userRepository.findByUsername(username);
        if (checkUser.isPresent()) {
            logger.debug("驗證用戶已保存到資料庫: {}", username);
        } else {
            logger.error("用戶保存後無法從資料庫檢索: {}", username);
        }

        return savedUser;
    }

    /**
     * 檢查用戶名是否存在
     * @param username 用戶名
     * @return 是否存在
     */
    public boolean existsByUsername(String username) {
        boolean exists = userRepository.findByUsername(username).isPresent();
        logger.debug("檢查用戶名是否存在: {}, 結果: {}", username, exists);
        return exists;
    }

    /**
     * 根據用戶名查找用戶
     * @param username 用戶名
     * @return 用戶Optional
     */
    public Optional<User> findByUsername(String username) {
        logger.debug("根據用戶名查找用戶: {}", username);
        Optional<User> userOpt = userRepository.findByUsername(username);

        if (userOpt.isPresent()) {
            User user = userOpt.get();
            logger.debug("找到用戶: {}, ID: {}, 密碼長度: {}",
                    user.getUsername(), user.getId(),
                    (user.getPassword() != null ? user.getPassword().length() : 0));
        } else {
            logger.debug("用戶不存在: {}", username);
        }

        return userOpt;
    }

    /**
     * 更新用戶信息
     * @param id 用戶ID
     * @param updatedUser 更新的用戶信息
     * @return 更新後的用戶
     */
    @Transactional
    public User updateUser(Long id, User updatedUser) {
        logger.info("開始更新用戶信息, ID: {}", id);

        return userRepository.findById(id)
                .map(existingUser -> {
                    // 更新用戶名（如果有變更且不與其他用戶衝突）
                    if(updatedUser.getUsername() != null && !updatedUser.getUsername().equals(existingUser.getUsername())) {
                        if(userRepository.findByUsername(updatedUser.getUsername()).isPresent()) {
                            logger.warn("更新失敗: 用戶名已存在: {}", updatedUser.getUsername());
                            throw new UserAlreadyExistsException("Username already exists: " + updatedUser.getUsername());
                        }
                        existingUser.setUsername(updatedUser.getUsername());
                        logger.debug("用戶名已更新為: {}", updatedUser.getUsername());
                    }

                    // 更新郵箱（如果有變更且不與其他用戶衝突）
                    if(updatedUser.getEmail() != null && !updatedUser.getEmail().equals(existingUser.getEmail())) {
                        if(userRepository.findByEmail(updatedUser.getEmail()).isPresent()) {
                            logger.warn("更新失敗: 郵箱已存在: {}", updatedUser.getEmail());
                            throw new UserAlreadyExistsException("Email already exists: " + updatedUser.getEmail());
                        }
                        existingUser.setEmail(updatedUser.getEmail());
                        logger.debug("郵箱已更新為: {}", updatedUser.getEmail());
                    }

                    // 更新密碼（如果有提供）
                    if(updatedUser.getPassword() != null && !updatedUser.getPassword().isEmpty()) {
                        String encodedPassword = bCryptPasswordEncoder.encode(updatedUser.getPassword());
                        existingUser.setPassword(encodedPassword);
                        logger.debug("密碼已更新並加密");
                    }

                    // 更新角色（如果有變更且有適當權限）
                    if(updatedUser.getRoles() != null && !updatedUser.getRoles().isEmpty()) {
                        existingUser.setRoles(updatedUser.getRoles());
                        logger.debug("角色已更新為: {}", updatedUser.getRoles());
                    }

                    User saved = userRepository.save(existingUser);
                    logger.info("用戶信息更新成功, ID: {}", id);
                    return saved;
                }).orElseThrow(() -> {
                    logger.error("用戶不存在, ID: {}", id);
                    return new ResourceNotFoundException("User not found with id: " + id);
                });
    }

    /**
     * 刪除用戶
     * @param id 用戶ID
     */
    @Transactional
    public void deleteUser(Long id) {
        logger.info("開始刪除用戶, ID: {}", id);

        if (!userRepository.existsById(id)) {
            logger.error("用戶不存在, ID: {}", id);
            throw new ResourceNotFoundException("User not found with id: " + id);
        }
        userRepository.deleteById(id);
        logger.info("用戶已刪除, ID: {}", id);
    }
}