package com.vti.bevtilib.repository;

import com.vti.bevtilib.model.UserDetail;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserDetailRepository extends JpaRepository<UserDetail, Long> {
    // Thêm phương thức này
    boolean existsByPhoneAndUser_UserIdNot(String phone, String userId);

}