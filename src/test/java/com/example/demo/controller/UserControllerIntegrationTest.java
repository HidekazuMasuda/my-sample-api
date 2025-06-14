package com.example.demo.controller;

import com.example.demo.model.User;
import com.example.demo.mapper.UserMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Testcontainers
public class UserControllerIntegrationTest {

    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16")
            .withDatabaseName("testdb")
            .withUsername("test")
            .withPassword("test");

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
        registry.add("spring.sql.init.mode", () -> "always");
    }

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private UserMapper userMapper;

    @LocalServerPort
    private int port;

    @BeforeEach
    @Transactional
    void setUp() {
        // テストデータのセットアップ - 各テスト前にテーブルをクリア
        userMapper.deleteAll();
        System.out.println("クリア後のユーザー数: " + userMapper.count());
        
        // テストデータを作成 - 各テストで異なるメールアドレスを使用
        String timestamp = String.valueOf(System.currentTimeMillis());
        User user1 = new User("テストユーザー1", "test1_" + timestamp + "@example.com");
        User user2 = new User("テストユーザー2", "test2_" + timestamp + "@example.com");
        User user3 = new User("テストユーザー3", "test3_" + timestamp + "@example.com");
        
        userMapper.insert(user1);
        userMapper.insert(user2);
        userMapper.insert(user3);
        
        System.out.println("セットアップ後のユーザー数: " + userMapper.count());
    }

    @Test
    public void getAllUsers_shouldReturnAllUsersFromDatabase() {
        // テストで挿入した3件のユーザーを確認
        int expectedUserCount = 3;

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
        ResponseEntity<List<User>> response = restTemplate.exchange(
                "http://localhost:" + port + "/api/users",
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<List<User>>() {}
        );

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(3, response.getBody().size());
        
        // 最初のユーザーの構造を検証
        User firstUser = response.getBody().get(0);
        assertNotNull(firstUser.getId());
        assertNotNull(firstUser.getName());
        assertNotNull(firstUser.getEmail());
        assertNotNull(firstUser.getCreatedAt());
        
        // テストデータの内容を検証
        List<String> userNames = response.getBody().stream()
                .map(User::getName)
                .toList();
        assertTrue(userNames.contains("テストユーザー1"));
        assertTrue(userNames.contains("テストユーザー2"));
        assertTrue(userNames.contains("テストユーザー3"));
    }

    @Test
    public void getAllUsers_databaseIntegrationTest() {
        // データベースへの統合テスト - Testcontainerで独立したデータベースを使用
        List<User> dbUsers = userMapper.selectAll();
        assertEquals(3, dbUsers.size());
        
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
        assertEquals(3, responseUsers.size());
        
        // データベースのユーザーとAPIレスポンスのユーザーが一致することを確認
        for (User dbUser : dbUsers) {
            User matchingResponseUser = responseUsers.stream()
                    .filter(u -> u.getId().equals(dbUser.getId()))
                    .findFirst()
                    .orElse(null);
            
            assertNotNull(matchingResponseUser, "DBのユーザーがAPIレスポンスに含まれていません: " + dbUser.getName());
            assertEquals(dbUser.getName(), matchingResponseUser.getName());
            assertEquals(dbUser.getEmail(), matchingResponseUser.getEmail());
        }
    }
}