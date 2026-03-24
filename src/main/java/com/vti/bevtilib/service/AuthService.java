package com.vti.bevtilib.service;

public interface AuthService {
    void registerUser(String username, String rawPassword) throws Exception;
    boolean usernameExists(String username);
}