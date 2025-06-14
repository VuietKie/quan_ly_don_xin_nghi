package com.example.quan_ly_don_xin_nghi.Controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.quan_ly_don_xin_nghi.Entity.LeaveRequest;
import com.example.quan_ly_don_xin_nghi.Entity.User;
import com.example.quan_ly_don_xin_nghi.Repository.LeaveRequestRepository;
import com.example.quan_ly_don_xin_nghi.Repository.UserRepository;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/leave-requests")
public class LeaveRequestController {

    @Autowired
    private LeaveRequestRepository leaveRequestRepo;

    @Autowired
    private UserRepository userRepo;

    // Get all leave requests
    @GetMapping
    public ResponseEntity<List<LeaveRequest>> getAllLeaveRequests() {
        List<LeaveRequest> requests = leaveRequestRepo.findAll();
        return ResponseEntity.ok(requests);
    }

    // Get leave request by ID
    @GetMapping("/{id}")
    public ResponseEntity<LeaveRequest> getLeaveRequestById(@PathVariable Long id) {
        return leaveRequestRepo.findById(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    // Create new leave request
    @PostMapping
    public ResponseEntity<LeaveRequest> createLeaveRequest(@Valid @RequestBody LeaveRequest leaveRequest) {
        LeaveRequest savedRequest = leaveRequestRepo.save(leaveRequest);
        return ResponseEntity.ok(savedRequest);
    }

    // Update leave request
    @PutMapping("/{id}")
    public ResponseEntity<LeaveRequest> updateLeaveRequest(@PathVariable Long id, @Valid @RequestBody LeaveRequest leaveRequestDetails) {
        return leaveRequestRepo.findById(id)
                .map(existingRequest -> {
                    existingRequest.setStart_date(leaveRequestDetails.getStart_date());
                    existingRequest.setEnd_date(leaveRequestDetails.getEnd_date());
                    existingRequest.setReason(leaveRequestDetails.getReason());
                    existingRequest.setStatus(leaveRequestDetails.getStatus());
                    existingRequest.setProcessedBy(leaveRequestDetails.getProcessedBy());
                    existingRequest.setProcessed_reason(leaveRequestDetails.getProcessed_reason());
                    
                    LeaveRequest updatedRequest = leaveRequestRepo.save(existingRequest);
                    return ResponseEntity.ok(updatedRequest);
                })
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    // Delete leave request
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteLeaveRequest(@PathVariable Long id) {
        return leaveRequestRepo.findById(id)
                .map(request -> {
                    leaveRequestRepo.delete(request);
                    return ResponseEntity.ok().build();
                })
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    // Get leave requests by user
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<LeaveRequest>> getLeaveRequestsByUser(@PathVariable Long userId) {
        return userRepo.findById(userId)
                .map(user -> {
                    List<LeaveRequest> requests = leaveRequestRepo.findByUser(user);
                    return ResponseEntity.ok(requests);
                })
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    // Get leave requests by status
    @GetMapping("/status/{status}")
    public ResponseEntity<List<LeaveRequest>> getLeaveRequestsByStatus(@PathVariable String status) {
        List<LeaveRequest> requests = leaveRequestRepo.findByStatus(status);
        return ResponseEntity.ok(requests);
    }
} 