//package com.devon.contractmanagementsystem.service;
//
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Service;
//
//import com.devon.contractmanagementsystem.model.User;
//import com.devon.contractmanagementsystem.repository.UserRepository;
//
//import org.springframework.util.StringUtils;
//
//@Service
//public class UserService {
//    @Autowired
//    private UserRepository userRepository;
//
//    public UserService(UserRepository userRepository)
//    {
//        this.userRepository=userRepository;
//    }
//
//    public User getUserByEmail(String email)
//    {
//        return userRepository.findByEmail(email);
//    }
//    public User signUp(User user) {
//        validateUser(user);
//        //hashPassword(user);
//        return userRepository.save(user);
//    }
//
//    public User login(String email, String password) {
//        User user = userRepository.findByEmail(email);
////        if (user != null && verifyPassword(password, user.getPassword())) {
//        return user;
////        }
////        return null;
//    }
//
//    private void validateUser(User user) {
//        if (!StringUtils.hasText(user.getEmail()) ||
//                !StringUtils.hasText(user.getPhoneNumber()) ||
//                !StringUtils.hasText(user.getPassword())) {
//            throw new IllegalArgumentException("Required fields are missing");
//        }
//
//        // Check if email or phone number is already registered
//        User existingUserByEmail = userRepository.findByEmail(user.getEmail());
//        if (existingUserByEmail != null) {
//            throw new IllegalArgumentException("Email is already registered");
//        }
//
//        User existingUserByPhoneNumber = userRepository.findByPhoneNumber(user.getPhoneNumber());
//        if (existingUserByPhoneNumber != null) {
//            throw new IllegalArgumentException("Phone number is already registered");
//        }
//    }
//
//    }
//
//
//
//
//

package com.devon.contractmanagementsystem.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.devon.contractmanagementsystem.model.User;
import com.devon.contractmanagementsystem.repository.UserRepository;

import org.springframework.util.StringUtils;

@Service
public class UserService {
    @Autowired
    private UserRepository userRepository;

    public UserService(UserRepository userRepository)
    {
        this.userRepository=userRepository;
    }

    public User signUp(User user) {
        validateUser(user);
        //hashPassword(user);
        return userRepository.save(user);
    }

    public User login(String email, String password) {
        User user = userRepository.findByEmailAndPassword(email,password);
        return user;
    }

    public User getUserByEmail(String email)
    {
        return userRepository.findByEmail(email);
    }

    private void validateUser(User user) {
        if (!StringUtils.hasText(user.getEmail()) ||
                !StringUtils.hasText(user.getPhoneNumber()) ||
                !StringUtils.hasText(user.getPassword())) {
            throw new IllegalArgumentException("Required fields are missing");
        }

        // Check if email or phone number is already registered
        User existingUserByEmail = userRepository.findByEmail(user.getEmail());
        if (existingUserByEmail != null) {
            throw new IllegalArgumentException("Email is already registered");
        }

        User existingUserByPhoneNumber = userRepository.findByPhoneNumber(user.getPhoneNumber());
        if (existingUserByPhoneNumber != null) {
            throw new IllegalArgumentException("Phone number is already registered");
        }
    }


}