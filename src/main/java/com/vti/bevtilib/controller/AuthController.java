package com.vti.bevtilib.controller;

import com.vti.bevtilib.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<String> processRegister(@RequestParam String username, @RequestParam String rawPassword) {
        authService.registerUser(username.trim(), rawPassword);
        return ResponseEntity.ok("Đăng ký thành công!");
    }

    @GetMapping("/check-username")
    public boolean checkUsername(@RequestParam String username) {
        return authService.usernameExists(username);
    }
}
