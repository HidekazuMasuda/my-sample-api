package com.example.demo.controller;

import com.example.demo.entity.User;
import com.example.demo.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.TestPropertySource;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@TestPropertySource(properties = {
    "spring.profiles.active=dev"
})
@Transactional
public class UserControllerIntegrationTest {

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private UserRepository userRepository;

    @LocalServerPort
    private int port;

    @Test
    public void getAllUsers_shouldReturnAllUsersFromDatabase() {
        // データベースからユーザー数を確認
        List<User> users = userRepository.findAll();
        int expectedUserCount = users.size();

        // /api/users エンドポイントをテスト
        ResponseEntity<List<User>> response = restTemplate.exchange(
                "http://localhost:" + port + "/api/users",
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<List<User>>() {}
        );

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(expectedUserCount, response.getBody().size());
        
        if (!response.getBody().isEmpty()) {
            User firstUser = response.getBody().get(0);
            assertNotNull(firstUser.getId());
            assertNotNull(firstUser.getName());
            assertNotNull(firstUser.getEmail());
            assertNotNull(firstUser.getCreatedAt());
        }
    }

    @Test
    public void getAllUsers_shouldReturnUsersWithCorrectStructure() {
        // 実際のユーザーデータを取得
        List<User> users = userRepository.findAll();
        
        if (!users.isEmpty()) {
            User firstUser = users.get(0);
            
            ResponseEntity<List<User>> response = restTemplate.exchange(
                    "http://localhost:" + port + "/api/users",
                    HttpMethod.GET,
                    null,
                    new ParameterizedTypeReference<List<User>>() {}
            );

            assertEquals(HttpStatus.OK, response.getStatusCode());
            assertNotNull(response.getBody());
            assertFalse(response.getBody().isEmpty());
            
            User responseUser = response.getBody().get(0);
            assertEquals(firstUser.getId(), responseUser.getId());
            assertEquals(firstUser.getName(), responseUser.getName());
            assertEquals(firstUser.getEmail(), responseUser.getEmail());
        }
    }

    @Test
    public void getAllUsers_databaseIntegrationTest() {
        // データベースへの統合テスト - 実際のデータベースからユーザーを取得できることを確認
        List<User> dbUsers = userRepository.findAll();
        
        // APIレスポンスとデータベースの内容が一致することを確認
        ResponseEntity<List<User>> response = restTemplate.exchange(
                "http://localhost:" + port + "/api/users",
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<List<User>>() {}
        );
        
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        
        List<User> responseUsers = response.getBody();
        
        // データベースとAPIレスポンスのサイズが一致することを確認
        assertEquals(dbUsers.size(), responseUsers.size());
        
        // 最初のユーザーの詳細が一致することを確認
        if (!dbUsers.isEmpty()) {
            User dbUser = dbUsers.get(0);
            User responseUser = responseUsers.get(0);
            
            assertEquals(dbUser.getId(), responseUser.getId());
            assertEquals(dbUser.getName(), responseUser.getName());
            assertEquals(dbUser.getEmail(), responseUser.getEmail());
        }
    }
}