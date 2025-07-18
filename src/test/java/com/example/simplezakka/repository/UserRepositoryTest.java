package com.example.simplezakka.repository;
 
//import lombok.Data;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
//import org.springframework.test.annotation.DirtiesContext;
import com.example.simplezakka.entity.User1;
//import com.example.simplezakka.repository.UserRepository;
 
//import jakarta.persistence.EntityManager; // これは TestEntityManager を使うなら不要かもしれません
 
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import static org.assertj.core.api.Assertions.assertThat; // AssertJのアサーションをインポート
 
@DataJpaTest
public class UserRepositoryTest {
 
    @Autowired // TestEntityManager を注入
    private TestEntityManager entityManager;
 
    @Autowired // UserRepository を注入
    private UserRepository userRepository;
 
    @BeforeEach
    void setUp() {
        User1 user = new User1(); // 変数名を小文字に
        user.setName("山下");
        user.setPassword("0000");
        user.setEmail("yama@gmail.com");
        user.setAddress("東京都");
        entityManager.persist(user); // データベースに永続化
        entityManager.flush(); // 即座にDBに反映
    }
 
    // テスト用のUserオブジェクトを作成するヘルパーメソッド
    private User1 createSampleUser() { // メソッドとして定義し、引数も検討
        User1 user = new User1();
        user.setName("テストユーザー"); // 毎回同じデータだとテストの意味が薄れるので、引数で変化させると良い
        user.setPassword("password");
        user.setEmail("test@example.com");
        user.setAddress("大阪府");
        return user;
    }
 
    @Test
    @DisplayName("新しいユーザーを正常に保存できること") // テスト内容に合わせてDisplayNameを変更
    public void saveUser_Success() {
        // Arrange
        User1 user = createSampleUser(); // メソッドとして呼び出す
 
        // Act
        User1 savedUser = userRepository.save(user);
        entityManager.flush();
        entityManager.clear();
 
        // Assert
        User1 foundUser = entityManager.find(User1.class, savedUser.getUserId()); // User1のIDフィールド名がgetId()だと仮定
        assertThat(foundUser).isNotNull();
        assertThat(foundUser.getUserId()).isNotNull();
        assertThat(foundUser.getName()).isEqualTo(user.getName()); // 保存したデータが正しいか検証
        assertThat(foundUser.getEmail()).isEqualTo(user.getEmail());
    }
}