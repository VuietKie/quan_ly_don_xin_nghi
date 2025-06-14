package com.example.quan_ly_don_xin_nghi.Repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.quan_ly_don_xin_nghi.Entity.LeaveRequest;
import com.example.quan_ly_don_xin_nghi.Entity.User;

@Repository
public interface LeaveRequestRepository extends JpaRepository<LeaveRequest, Long> {
    List<LeaveRequest> findByUser(User user);
    List<LeaveRequest> findByProcessedBy(User processedBy);
    List<LeaveRequest> findByStatus(String status);
} 