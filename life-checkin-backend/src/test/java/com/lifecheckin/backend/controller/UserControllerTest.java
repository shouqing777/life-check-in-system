package com.lifecheckin.backend.controller;

import com.lifecheckin.backend.model.User;
import com.lifecheckin.backend.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc; // 新增這行
import org.springframework.boot.test.context.SpringBootTest; // 新增這行
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@SpringBootTest // 替換 @WebMvcTest
@AutoConfigureMockMvc // 新增這行
public class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserRepository userRepository;

    private User user;

    @BeforeEach
    public void setUp() {
        user = new User();
        user.setId(1L);
        user.setUsername("testuser");
        user.setPassword("password");
    }

    @Test
    public void testGetAllUsers() throws Exception {
        List<User> users = Arrays.asList(user);
        when(userRepository.findAll()).thenReturn(users);

        mockMvc.perform(get("/api/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].username").value("testuser"));
    }

//    @Test
//    public void testCreateUser() throws Exception {
//        when(userRepository.save(user)).thenReturn(user);
//
//        mockMvc.perform(post("/api/users")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content("{\"username\":\"testuser\",\"password\":\"password\"}"))
//                .andExpect(status().isCreated())
//                .andExpect(jsonPath("$.username").value("testuser"));
//    }
}