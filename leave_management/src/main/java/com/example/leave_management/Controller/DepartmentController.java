package com.example.leave_management.Controller;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.leave_management.Entity.Departments;
import com.example.leave_management.Repository.DepartmentsRepository;

@RestController
@RequestMapping("/departments")
public class DepartmentController {
    @Autowired
    private DepartmentsRepository departmentsRepository;

    // Lấy tất cả departments
    @GetMapping("/all")
    public ResponseEntity<List<Departments>> getAllDepartments() {
        List<Departments> list = departmentsRepository.findAll();
        return ResponseEntity.ok(list);
    }

    // Lấy department theo id
    @GetMapping("/{id}")
    public ResponseEntity<?> getDepartmentById(@PathVariable Long id) {
        Optional<Departments> depOpt = departmentsRepository.findById(id);
        if (depOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(depOpt.get());
    }

    // Thêm mới department
    @PostMapping("/add")
    public ResponseEntity<?> addDepartment(@RequestBody Departments department) {
        if (department.getDepartmentName() == null || department.getDepartmentName().isEmpty()) {
            return ResponseEntity.badRequest().body("Department name is required");
        }
        // Kiểm tra trùng tên
        if (departmentsRepository.findAll().stream().anyMatch(d -> d.getDepartmentName().equals(department.getDepartmentName()))) {
            return ResponseEntity.badRequest().body("Department name already exists");
        }
        Departments saved = departmentsRepository.save(department);
        return ResponseEntity.ok(saved);
    }

    // Cập nhật department
    @PatchMapping("/update/{id}")
    public ResponseEntity<?> updateDepartment(@PathVariable Long id, @RequestBody Departments update) {
        Optional<Departments> depOpt = departmentsRepository.findById(id);
        if (depOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        Departments department = depOpt.get();
        if (update.getDepartmentName() != null && !update.getDepartmentName().isEmpty()) {
            // Kiểm tra trùng tên
            boolean exists = departmentsRepository.findAll().stream()
                .anyMatch(d -> d.getDepartmentName().equals(update.getDepartmentName()) && !d.getDepartmentId().equals(id));
            if (exists) {
                return ResponseEntity.badRequest().body("Department name already exists");
            }
            department.setDepartmentName(update.getDepartmentName());
        }
        if (update.getIdManager() != null) {
            department.setIdManager(update.getIdManager());
        }
        Departments saved = departmentsRepository.save(department);
        return ResponseEntity.ok(saved);
    }

    // Xóa department
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> deleteDepartment(@PathVariable Long id) {
        Optional<Departments> depOpt = departmentsRepository.findById(id);
        if (depOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        departmentsRepository.deleteById(id);
        return ResponseEntity.ok("Deleted");
    }
} 