package com.example.leave_management.Repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.leave_management.Entity.Role_Features;
import com.example.leave_management.Entity.Roles;

@Repository
public interface RoleFeaturesRepository extends JpaRepository<Role_Features, Long> {
    List<Role_Features> findByRole(Roles role);
} 