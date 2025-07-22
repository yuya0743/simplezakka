package com.example.simplezakka.repository;

import lombok.Data;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.annotation.DirtiesContext;
import com.example.simplezakka.entity.User1;
import com.example.simplezakka.repository.UserRepository;

import jakarta.persistence.EntityManager;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import static org.assertj.core.api.Assertions.assertThat;
import java.time.LocalDateTime; // LocalDateTimeを使用するためにインポートを追加

@DataJpaTest
public class UserRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private UserRepository userRepository;

    @BeforeEach
    void setUp() {
        // setUpでもcreateSampleUserを使ってユーザーを準備
        User1 user = createSampleUser("山下", "0000", "yama@gmail.com", "東京都");
        entityManager.persist(user); // データベースに永続化
        entityManager.flush(); // 即座にDBに反映
    }

    /**
     * テスト用のUserオブジェクトを作成するヘルパーメソッド
     * 名前、パスワード、メール、住所を引数で受け取るように汎用化
     */
    private User1 createSampleUser(String name, String password, String email, String address) {
        User1 user = new User1();
        user.setName(name);
        user.setPassword(password);
        user.setEmail(email);
        user.setAddress(address);
        return user;
    }

    @Test
    @DisplayName("新しいユーザーを正常に保存できること")
    public void saveUser_Success() {
        // Arrange
        // createSampleUserメソッドを使って新しいユーザーを作成
        User1 user = createSampleUser("テストユーザー", "password", "test@example.com", "大阪府");

        // Act
        User1 savedUser = userRepository.save(user);
        entityManager.flush();
        entityManager.clear();

        // Assert
        User1 foundUser = entityManager.find(User1.class, savedUser.getUserId());
        assertThat(foundUser).isNotNull();
        assertThat(foundUser.getUserId()).isNotNull();
        assertThat(foundUser.getName()).isEqualTo(user.getName());
        assertThat(foundUser.getEmail()).isEqualTo(user.getEmail());

        // タイムスタンプの検証
        assertThat(foundUser.getCreatedAt()).isNotNull(); // createdAtがnullではないことを確認
        assertThat(foundUser.getUpdatedAt()).isNotNull(); // updatedAtがnullではないことを確認
        assertThat(foundUser.getCreatedAt()).isBeforeOrEqualTo(foundUser.getUpdatedAt()); // 論理的な順序を確認
    }
}