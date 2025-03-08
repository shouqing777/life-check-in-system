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
//		User user = new User();
//		user.setUsername("testuser");
//		user.setPassword("password");
//		userService.createUser(user);
	}

}
