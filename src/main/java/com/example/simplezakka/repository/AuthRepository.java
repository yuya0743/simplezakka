package com.example.simplezakka.repository;

import java.util.Optional;
import com.example.simplezakka.entity.User1;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface AuthRepository extends JpaRepository<User1, Integer> {

    Optional<User1> findByEmailAndPassword(String email, String password);

    Optional<User1> findByEmail(String email);

    boolean existsByEmail(String email);
}