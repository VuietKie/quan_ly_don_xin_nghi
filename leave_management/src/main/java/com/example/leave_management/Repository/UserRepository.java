package com.example.leave_management.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.leave_management.Entity.Users;

@Repository
public interface UserRepository extends JpaRepository<Users, Long> {
    java.util.List<Users> findByManager_UserId(Long managerId);
} 