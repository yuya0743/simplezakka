package com.example.simplezakka.config;

import com.example.simplezakka.entity.User;
import com.example.simplezakka.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

@Component
public class Logindata implements CommandLineRunner {
    private final UserRepository userRepository;

    @Autowired
    public DataLoader(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public void run(String... args) {
        loadSampleProducts();
    }

    private void loadSampleProducts() {
        if (UserRepository.count() > 0) {
            return; // すでにデータが存在する場合はスキップ
        }

        List<User> Users = Arrays.asList(
            createuser(
                "森旺介", 
                "osuke.mori@avantcorp.com", 
                "東京都渋谷区1-2-3", 
                "password123"
                

                
            ),
            createuser(
                "山下桔平", 
                "kippei.yamashita@avantcorp.com", 
                "大阪府大阪市北区4-5-6", 
                "mypassword456"
            ),
            createuser(
                "吉岡裕矢", 
                "yuya.yoshioka@avantcorp.com", 
                "北海道札幌市中央区7-8-9", 
                "securepass789"

            ),
            createuser(
                "中村華", 
                "hikari.nakamura@avantcorp.com", 
                "福岡県福岡市博多区10-11-12", 
                "mypassword123"
            ),
            createuser(
                "三浦柄都",
                "etsu.miura@avantcorp.com",
                "愛知県名古屋市中区13-14-15",
                "mypassword456"
            ),
            createuser(
                "木村日向子",
                "hinako.kimura@avantcorp.com",
                "京都府京都市左京区16-17-18",
                "mypassword789"
            )
            
            
        );
        

        userRepository.saveAll(user);
    }
    
    private User createuser(String name, String email, String address, String password) {
        User user = new user();
        user.setusername(username);
        user.setEmail(email);
        user.setAddress(address);
        user.setPassword(password);
        user.setCreatedAt(LocalDateTime.now());
        user.setUpdatedAt(LocalDateTime.now());
        return user;
    }
}