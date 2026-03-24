package com.vti.bevtilib.dto;

import lombok.Data;
import java.time.LocalDate;

@Data
public class AdminUserUpdateDTO {
    // Các trường trong User entity
    private String username;
    private String role;
    private Boolean status;

    // Các trường trong UserDetail entity
    private String fullName;
    private String email;
    private String phone;
    private String address;
    private LocalDate dob;
    private String gender;
    private String avatar; // Dùng để cập nhật qua link ảnh
}