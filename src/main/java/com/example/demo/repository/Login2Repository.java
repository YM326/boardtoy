package com.example.demo.repository;

import com.example.demo.entity.Login2Entity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface Login2Repository extends JpaRepository<Login2Entity, Integer>{
    Login2Entity findByIdAndPw(String id, String pw);
    Login2Entity findById(String id);
}