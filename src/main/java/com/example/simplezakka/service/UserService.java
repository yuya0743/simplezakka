package com.example.simplezakka.service; // service パッケージ
 
import com.example.simplezakka.dto.User.UserRequest; // 新しく作成する登録リクエストDTO
import com.example.simplezakka.dto.User.UserResponse; // 新しく作成する登録レスポンスDTO
import com.example.simplezakka.entity.User1; // 新しく作成するUserエンティティ
import com.example.simplezakka.entity.User1.User;
import com.example.simplezakka.repository.UserRepository; // 新しく作成するUserRepository
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.Optional;
 
@Service

public class UserService {
 
    private final UserRepository UserRepository;
 
    @Transactional

    public UserResponse registerUser(UserRequest request) {
 
        // 3. Userエンティティの作成

        User1 user = new User1();

        user.setName(request.getUserInfo().getName());

        user.setmail(request.getUserInfo().getEmail());

        user.setAddress(request.getUserInfo().getAddress());

        user.setRegistrationDate(LocalDateTime.now());

        // その他のユーザー固有のフィールドを設定（例: ロールなど）
 
        // 4. ユーザー情報をデータベースに保存

        User1 savedUser = userRepository.save(user);
 
        // 5. レスポンスの生成

        return new RegisterResponse(savedUser.getUserId(), "会員登録が成功しました。", savedUser.getRegistrationDate());

    }
 
    // 必要に応じて、ログイン処理やユーザー情報更新などのメソッドを追加

}
 