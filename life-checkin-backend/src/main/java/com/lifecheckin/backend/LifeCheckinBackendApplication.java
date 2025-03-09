package com.lifecheckin.backend;

import com.lifecheckin.backend.model.User;
import com.lifecheckin.backend.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class LifeCheckinBackendApplication implements CommandLineRunner {

	@Autowired
	private UserService userService;

	public static void main(String[] args) {
		SpringApplication.run(LifeCheckinBackendApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {
		// 建立測試用戶 (取消註釋這段代碼)
		User user = new User();
		user.setUsername("testuser2");
		user.setPassword("securepassword");
		user.setEmail("testuser2@example.com");
		userService.createUser(user);

		System.out.println("測試用戶已創建: admin / admin123");

	}
}
