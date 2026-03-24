package com.vti.bevtilib.repository;

import com.vti.bevtilib.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, String> {
    Optional<User> findByUsername(String username);
    boolean existsByUsername(String username);
    boolean existsByUserDetail_EmailAndUserIdNot(String email, String userId);
    
    // Query methods for admin functionality
    @Query("SELECT u FROM User u LEFT JOIN FETCH u.userDetail ud WHERE " +
           "(:search IS NULL OR :search = '' OR " +
           "LOWER(u.username) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "LOWER(ud.fullName) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "LOWER(ud.email) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "LOWER(ud.phone) LIKE LOWER(CONCAT('%', :search, '%'))) AND " +
           "(:role IS NULL OR u.role = :role) AND " +
           "(:status IS NULL OR u.status = :status)")
    Page<User> findAllWithFilters(@Param("search") String search, 
                                  @Param("role") String role, 
                                  @Param("status") Boolean status, 
                                  Pageable pageable);
    
    long countByRole(String role);
    long countByStatus(boolean status);
    long countByRoleAndStatus(String role, boolean status);

}