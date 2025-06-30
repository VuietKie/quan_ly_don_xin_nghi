package com.example.leave_management.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.leave_management.Entity.Leave_Requests;

@Repository
public interface LeaveRequestsRepository extends JpaRepository<Leave_Requests, Long> {
} 