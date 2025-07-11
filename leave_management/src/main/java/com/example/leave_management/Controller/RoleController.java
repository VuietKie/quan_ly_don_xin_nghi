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

import com.example.leave_management.Entity.Roles;
import com.example.leave_management.Repository.RoleRepository;

@RestController
@RequestMapping("/roles")
public class RoleController {
    @Autowired
    private RoleRepository roleRepository;

    // Lấy tất cả roles
    @GetMapping("/all")
    public ResponseEntity<List<Roles>> getAllRoles() {
        List<Roles> list = roleRepository.findAll();
        return ResponseEntity.ok(list);
    }

    // Lấy role theo id
    @GetMapping("/{id}")
    public ResponseEntity<?> getRoleById(@PathVariable Long id) {
        Optional<Roles> roleOpt = roleRepository.findById(id);
        if (roleOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(roleOpt.get());
    }

    // Thêm mới role
    @PostMapping("/add")
    public ResponseEntity<?> addRole(@RequestBody Roles role) {
        if (role.getRoleName() == null || role.getRoleName().isEmpty()) {
            return ResponseEntity.badRequest().body("Role name is required");
        }
        // Kiểm tra trùng tên
        if (roleRepository.findByRoleName(role.getRoleName()) != null) {
            return ResponseEntity.badRequest().body("Role name already exists");
        }
        Roles saved = roleRepository.save(role);
        return ResponseEntity.ok(saved);
    }

    // Cập nhật role
    @PatchMapping("/update/{id}")
    public ResponseEntity<?> updateRole(@PathVariable Long id, @RequestBody Roles update) {
        Optional<Roles> roleOpt = roleRepository.findById(id);
        if (roleOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        Roles role = roleOpt.get();
        if (update.getRoleName() != null && !update.getRoleName().isEmpty()) {
            // Kiểm tra trùng tên
            Roles existing = roleRepository.findByRoleName(update.getRoleName());
            if (existing != null && !existing.getRoleId().equals(id)) {
                return ResponseEntity.badRequest().body("Role name already exists");
            }
            role.setRoleName(update.getRoleName());
        }
        Roles saved = roleRepository.save(role);
        return ResponseEntity.ok(saved);
    }

    // Xóa role
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> deleteRole(@PathVariable Long id) {
        Optional<Roles> roleOpt = roleRepository.findById(id);
        if (roleOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        roleRepository.deleteById(id);
        return ResponseEntity.ok("Deleted");
    }
} 