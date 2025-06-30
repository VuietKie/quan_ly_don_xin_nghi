package com.example.leave_management.Repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.leave_management.Entity.Departments;

@Repository
public interface DepartmentsRepository extends JpaRepository<Departments, Long> {
    List<Departments> findByIdManager(Long idManager);
} 