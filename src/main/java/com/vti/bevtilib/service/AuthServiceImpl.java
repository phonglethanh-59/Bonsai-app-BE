package com.vti.bevtilib.service;

import com.vti.bevtilib.model.User;
import com.vti.bevtilib.model.UserDetail;
import com.vti.bevtilib.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.Random;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public void registerUser(String username, String rawPassword) throws Exception {
        if (userRepository.existsByUsername(username)) {
            throw new Exception("Tên đăng nhập đã tồn tại.");
        }

        User user = new User();
        user.setUserId(generateUserId());
        user.setUsername(username);
        user.setPassword(passwordEncoder.encode(rawPassword));
        user.setRole("CUSTOMER");
        user.setStatus(true);

        UserDetail userDetail = new UserDetail();
        userDetail.setUser(user);
        user.setUserDetail(userDetail);

        userRepository.save(user);
    }

    @Override
    public boolean usernameExists(String username) {
        return userRepository.existsByUsername(username);
    }

    private String generateUserId() {
        String id;
        do {
            id = "U" + new Random().nextInt(9000) + 1000;
        } while (userRepository.existsById(id));
        return id;
    }
}