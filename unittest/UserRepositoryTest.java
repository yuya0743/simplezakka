package unittest;

import lombok.Data;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.annotation.DirtiesContext;
import com.example.simplezakka.entity.User1;
import com.example.simplezakka.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;



@DataJpaTest
public class UserRepositoryTest {

    private UserRepository userRepository;
    
    @BegoforeEach
    public void setUp() {
        User1 user = new User1();
        user.setName("山下");
        user.setPassword("0000");
        user.setEmail("yama@gmail.com"); 
        user.setAddress("東京都");
        userRepository.save(user);
    }
    @Test
    @DisplayName("ユーザー名で検索できること")
    public void saveUser_Success() {
        
    }  

}   