package com.example.quan_ly_don_xin_nghi.Repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.quan_ly_don_xin_nghi.Entity.User;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    List<User> findByDepartmentId(Long departmentId);
    List<User> findByRoleId(Long roleId);
    Optional<User> findByUsername(String username);
    Optional<User> findByEmail(String email);
    boolean existsByUsername(String username);
    boolean existsByEmail(String email);
} 