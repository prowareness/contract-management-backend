package com.devon.contractmanagementsystem.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.devon.contractmanagementsystem.model.User;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {
    User findByEmailAndPassword(String email, String password);
    User findById(int id);
    User findByEmail(String email);
    User findByPhoneNumber(String phoneNumber);

}
