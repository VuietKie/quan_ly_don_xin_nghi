package com.example.leave_management.Controller;

import java.util.Date;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.leave_management.Entity.Leave_Requests;
import com.example.leave_management.Entity.Users;
import com.example.leave_management.Repository.DepartmentsRepository;
import com.example.leave_management.Repository.LeaveRequestsRepository;
import com.example.leave_management.Repository.UserRepository;

@RestController
@RequestMapping("/leave-request")
public class LeaveRequestController {
    @Autowired
    private LeaveRequestsRepository leaveRequestsRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private DepartmentsRepository departmentsRepository;

    // DTO cho request body
    public static class LeaveRequestDTO {
        public Long userId;
        public Date startDate;
        public Date endDate;
        public String reason;
        public String status;
        public Long processedById;
        public String processedReason;
    }

    public static class ApproveRequestDTO {
        public Long processedById;
        public String status; // 'Approved' hoặc 'Rejected'
        public String processedReason;
    }

    @PostMapping("/create")
    public ResponseEntity<?> createLeaveRequest(@RequestBody LeaveRequestDTO dto) {
        Optional<Users> userOpt = userRepository.findById(dto.userId);
        if (!userOpt.isPresent()) {
            return ResponseEntity.badRequest().body("User not found");
        }
        Users user = userOpt.get();

        Users processedBy = null;
        if (dto.processedById != null) {
            Optional<Users> processedByOpt = userRepository.findById(dto.processedById);
            if (!processedByOpt.isPresent()) {
                return ResponseEntity.badRequest().body("Processed by user not found");
            }
            processedBy = processedByOpt.get();
        }

        Leave_Requests leaveRequest = new Leave_Requests();
        leaveRequest.setUser(user);
        leaveRequest.setStartDate(dto.startDate);
        leaveRequest.setEndDate(dto.endDate);
        leaveRequest.setReason(dto.reason);
        leaveRequest.setStatus(dto.status);
        leaveRequest.setProcessedBy(processedBy);
        leaveRequest.setProcessedReason(dto.processedReason);
        leaveRequest.setCreatedAt(new Date());
        leaveRequest.setUpdatedAt(new Date());

        Leave_Requests saved = leaveRequestsRepository.save(leaveRequest);
        return ResponseEntity.ok(saved);
    }

    @GetMapping("/list")
    public ResponseEntity<?> listSubordinateLeaveRequests(@RequestParam Long userId) {
        // Lấy danh sách nhân viên cấp dưới trực tiếp
        java.util.List<Users> subordinates = userRepository.findByManager_UserId(userId);
        // Lấy các phòng ban mà user này là trưởng phòng
        java.util.List<com.example.leave_management.Entity.Departments> managedDepartments = departmentsRepository.findByIdManager(userId);
        java.util.Set<Long> departmentIds = managedDepartments.stream().map(com.example.leave_management.Entity.Departments::getDepartmentId).collect(java.util.stream.Collectors.toSet());
        // Lấy tất cả nhân viên thuộc các phòng ban này
        java.util.List<Users> departmentUsers = userRepository.findAll().stream()
            .filter(u -> u.getDepartment() != null && departmentIds.contains(u.getDepartment().getDepartmentId()))
            .toList();
        // Gộp userId của cấp dưới và nhân viên phòng ban quản lý
        java.util.Set<Long> allUserIds = new java.util.HashSet<>();
        allUserIds.addAll(subordinates.stream().map(Users::getUserId).toList());
        allUserIds.addAll(departmentUsers.stream().map(Users::getUserId).toList());
        if (allUserIds.isEmpty()) {
            return ResponseEntity.ok(java.util.Collections.emptyList());
        }
        java.util.List<Leave_Requests> leaveRequests = leaveRequestsRepository.findAll().stream()
                .filter(lr -> allUserIds.contains(lr.getUser().getUserId()))
                .toList();
        return ResponseEntity.ok(leaveRequests);
    }

    @GetMapping("/my-request")
    public ResponseEntity<?> getMyLeaveRequests(@RequestParam Long userId) {
        java.util.List<Leave_Requests> myRequests = leaveRequestsRepository.findAll().stream()
            .filter(lr -> lr.getUser() != null && lr.getUser().getUserId().equals(userId))
            .toList();
        return ResponseEntity.ok(myRequests);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getLeaveRequestById(@PathVariable Long id) {
        Optional<Leave_Requests> leaveOpt = leaveRequestsRepository.findById(id);
        if (leaveOpt.isPresent()) {
            return ResponseEntity.ok(leaveOpt.get());
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PatchMapping("/{id}")
    public ResponseEntity<?> updateLeaveRequestStatus(
            @PathVariable Long id,
            @RequestBody ApproveRequestDTO dto) {
        Optional<Leave_Requests> leaveOpt = leaveRequestsRepository.findById(id);
        if (leaveOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        Leave_Requests leaveRequest = leaveOpt.get();

        // Kiểm tra status hợp lệ
        if (dto.status != null) {
            String status = dto.status;
            if (!status.equals("Inprogress") && !status.equals("Approved") && !status.equals("Rejected")) {
                return ResponseEntity.badRequest().body("Invalid status value");
            }
            leaveRequest.setStatus(status);
        }

        // Cập nhật processed_by
        if (dto.processedById != null) {
            Optional<Users> processedByOpt = userRepository.findById(dto.processedById);
            if (processedByOpt.isEmpty()) {
                return ResponseEntity.badRequest().body("Processed by user not found");
            }
            leaveRequest.setProcessedBy(processedByOpt.get());
        }

        // Cập nhật processed_reason
        if (dto.processedReason != null) {
            leaveRequest.setProcessedReason(dto.processedReason);
        }

        // Cập nhật updated_at
        leaveRequest.setUpdatedAt(new java.util.Date());
        Leave_Requests saved = leaveRequestsRepository.save(leaveRequest);
        return ResponseEntity.ok(saved);
    }

    @GetMapping("/filter-by-date")
    public ResponseEntity<?> filterLeaveRequestsByDate(@RequestParam String start_date, @RequestParam String end_date) {
        try {
            java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd");
            java.util.Date startDate = sdf.parse(start_date);
            java.util.Date endDate = sdf.parse(end_date);
            java.util.List<Leave_Requests> filtered = leaveRequestsRepository.findAll().stream()
                .filter(lr -> lr.getStartDate() != null && lr.getEndDate() != null)
                .filter(lr -> !lr.getStartDate().before(startDate) && !lr.getEndDate().after(endDate))
                .toList();
            return ResponseEntity.ok(filtered);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Invalid date format. Use yyyy-MM-dd");
        }
    }
}
