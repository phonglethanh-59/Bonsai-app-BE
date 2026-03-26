package com.vti.bevtilib.service;

public interface AuthService {
    void registerUser(String username, String rawPassword);
    boolean usernameExists(String username);
}
