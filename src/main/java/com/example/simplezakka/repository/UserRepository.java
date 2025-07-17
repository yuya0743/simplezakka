package com.example.simplezakka.repository;

// import com.example.simplezakka.dto.User.UserInfo;
import com.example.simplezakka.entity.User1;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User1, Integer> {
Optional<User1> findByEmail(String email);
}

