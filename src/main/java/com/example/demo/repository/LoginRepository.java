package com.example.demo.repository;

import com.example.demo.entity.LoginEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LoginRepository extends JpaRepository<LoginEntity, Integer> {
//    @Query("select m from LoginEntity m where m.id = :id and m.pw = :pw")
    LoginEntity findByIdAndPw(String id, String pw);
    LoginEntity findById(String id);
}
