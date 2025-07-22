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
 
@DataJpaTest
public class UserRepositoryTest {
 
    @Autowired 
    private TestEntityManager entityManager;
 
    @Autowired
    private UserRepository userRepository;
 
    @BeforeEach
    void setUp() {
        User1 user = new User1(); 
        user.setName("山下");
        user.setPassword("0000");
        user.setEmail("yama@gmail.com");
        user.setAddress("東京都");
        entityManager.persist(user); // データベースに永続化
        entityManager.flush(); // 即座にDBに反映
    }
 
    // テスト用のUserオブジェクトを作成するヘルパーメソッド
    private User1 createSampleUser() { 
        User1 user = new User1();
        user.setName("テストユーザー"); 
        user.setPassword("password");
        user.setEmail("test@example.com");
        user.setAddress("大阪府");
        return user;
    }
 
    @Test
    @DisplayName("新しいユーザーを正常に保存できること") 
    public void saveUser_Success() {
        // Arrange
        User1 user = createSampleUser(); // メソッドとして呼び出す
 
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

        // User1 foundUser を取得した後に追加
assertThat(foundUser.getCreatedAt()).isNotNull(); // createdAtがnullではないことを確認
assertThat(foundUser.getUpdatedAt()).isNotNull(); // updatedAtがnullではないことを確認

// createdAt と updatedAt の値が論理的に正しいことを検証（例：createdAt <= updatedAt）
assertThat(foundUser.getCreatedAt()).isBeforeOrEqualTo(foundUser.getUpdatedAt());

// 必要であれば、現在時刻との近接性を検証（ただし、テストの実行時間に依存するため注意が必要）
// assertThat(foundUser.getCreatedAt()).isCloseTo(LocalDateTime.now(), within(Duration.ofSeconds(5)));
    }
}