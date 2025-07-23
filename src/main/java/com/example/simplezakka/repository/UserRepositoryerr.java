//DB接続失敗のため、テストが失敗することを確認するためのクラス

package com.example.simplezakka.repository;

import org.springframework.stereotype.Repository;
import com.example.simplezakka.entity.User1;

import jakarta.transaction.Transactional;

@Repository
public class UserRepositoryerr {
    @Transactional
    public void save(User1 user) {
     throw new IllegalStateException();
}

}