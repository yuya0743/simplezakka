package com.example.simplezakka.repository;

import com.example.simplezakka.entity.User1;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class AuthRepositoryTest {

    @Autowired
    private TestEntityManager entityManager; 

    @Autowired
    private AuthRepository authRepository; 

    @Test
    @DisplayName("findByEmail：ユーザーが存在する場合、User1 を返す")
    void findByEmail_ShouldReturnUser_WhenEmailExists() {
        // Arrange
        User1 user = new User1();
        user.setName("ユーザー");
        user.setEmail("user@email.com");
        user.setPassword("password");
        user.setAddress("東京都港区");

        authRepository.save(user);

        // Act
        Optional<User1> result = authRepository.findByEmail("user@email.com");

        // Assert
        assertThat(result).isPresent();
        assertThat(result.get().getEmail()).isEqualTo("user@email.com");
        assertThat(result.get().getName()).isEqualTo("ユーザー");
    }

    @Test
    @DisplayName("findByEmail：ユーザーが存在しない場合、Optional.empty() を返す")
    void findByEmail_ShouldReturnEmpty_WhenEmailNotExists() {
        // Act
        Optional<User1> result = authRepository.findByEmail("unknown@example.com");

        // Assert
        assertThat(result).isEmpty();
    }
}
