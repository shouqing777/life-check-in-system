package com.lifecheckin.backend.service;

import com.lifecheckin.backend.exception.ResourceNotFoundException;
import com.lifecheckin.backend.model.User;
import com.lifecheckin.backend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    // 取得所有使用者
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    // 根據 ID 取得使用者，返回 Optional
    public Optional<User> getUserById(Long id) {
        return userRepository.findById(id);
    }

    // 新增使用者
    public User createUser(User user) {
        return userRepository.save(user);
    }

    public User registerUser(String username, String rawPassword) {
        if (username == null || username.trim().isEmpty()) {
            throw new IllegalArgumentException("Username cannot be empty");
        }
        if (rawPassword == null || rawPassword.length() < 6) {
            throw new IllegalArgumentException("Password must be at least 6 characters");
        }
        if (userRepository.findByUsername(username) != null) {
            throw new IllegalArgumentException("Username already exists");
        }
        User user = new User();
        user.setUsername(username);
        user.setPassword(bCryptPasswordEncoder.encode(rawPassword));
        user.setRoles("ROLE_USER");
        return userRepository.save(user);
    }

    public boolean existsByUsername(String username) {
        return userRepository.findByUsername(username) != null;
    }

    public Optional<User> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    // 更新使用者
    public User updateUser(Long id, User updatedUser) {
        return userRepository.findById(id)
                .map(existingUser -> {
                    existingUser.setUsername(updatedUser.getUsername());
                    existingUser.setPassword(bCryptPasswordEncoder.encode(updatedUser.getPassword())); // 確保密碼加密
                    return userRepository.save(existingUser);
                }).orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));
    }

    // 刪除使用者
    public void deleteUser(Long id) {
        if (!userRepository.existsById(id)) {
            throw new ResourceNotFoundException("User not found with id: " + id);
        }
        userRepository.deleteById(id);
    }
}
