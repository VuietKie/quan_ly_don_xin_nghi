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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.leave_management.Entity.Features;
import com.example.leave_management.Entity.RoleFeatureId;
import com.example.leave_management.Entity.Role_Features;
import com.example.leave_management.Entity.Roles;
import com.example.leave_management.Repository.RoleFeaturesRepository;
import com.example.leave_management.Repository.RoleRepository;

@RestController
@RequestMapping("/role-features")
public class RoleFeaturesController {
    @Autowired
    private RoleFeaturesRepository roleFeaturesRepository;

    @Autowired
    private RoleRepository roleRepository;

    // DTO cho request thêm mới
    public static class RoleFeatureRequestDTO {
        public Long roleId;
        public Long featureId;
    }

    // Lấy tất cả các role-feature (isActive=true)
    @GetMapping("/all")
    public ResponseEntity<List<Role_Features>> getAllRoleFeatures() {
        List<Role_Features> list = roleFeaturesRepository.findAll();
        return ResponseEntity.ok(list);
    }

    // Lấy các feature theo roleId (isActive=true)
    @GetMapping("/by-role/{roleId}")
    public ResponseEntity<?> getFeaturesByRole(@PathVariable Long roleId) {
        Optional<Roles> roleOpt = roleRepository.findById(roleId);
        if (roleOpt.isEmpty()) {
            return ResponseEntity.badRequest().body("Role not found");
        }
        List<Role_Features> list = roleFeaturesRepository.findByRole(roleOpt.get())
                .stream().filter(Role_Features::isActive).toList();
        return ResponseEntity.ok(list);
    }

    // Thêm mới (gán feature cho role, isActive=true)
    @PostMapping("/add")
    public ResponseEntity<?> addRoleFeature(@RequestBody RoleFeatureRequestDTO dto) {
        Optional<Roles> roleOpt = roleRepository.findById(dto.roleId);
        if (roleOpt.isEmpty()) {
            return ResponseEntity.badRequest().body("Role not found");
        }
        // Tìm feature bằng entity manager hoặc tạo FeaturesRepository nếu cần
        // Ở đây tạm thời chỉ tạo đối tượng Features với id
        Features feature = new Features();
        feature.setFeatureId(dto.featureId);
        Role_Features rf = new Role_Features(roleOpt.get(), feature, true);
        Role_Features saved = roleFeaturesRepository.save(rf);
        return ResponseEntity.ok(saved);
    }

    // Xóa (set isActive=false)
    @DeleteMapping("/delete")
    public ResponseEntity<?> deleteRoleFeature(@RequestBody RoleFeatureRequestDTO dto) {
        Optional<Roles> roleOpt = roleRepository.findById(dto.roleId);
        if (roleOpt.isEmpty()) {
            return ResponseEntity.badRequest().body("Role not found");
        }
        Features feature = new Features();
        feature.setFeatureId(dto.featureId);
        RoleFeatureId id = new RoleFeatureId(dto.roleId, dto.featureId);
        Optional<Role_Features> rfOpt = roleFeaturesRepository.findAll().stream()
                .filter(rf -> rf.getRole().getRoleId().equals(dto.roleId)
                        && rf.getFeature().getFeatureId().equals(dto.featureId))
                .findFirst();
        if (rfOpt.isEmpty()) {
            return ResponseEntity.badRequest().body("Role-Feature not found");
        }
        Role_Features rf = rfOpt.get();
        rf.setActive(false);
        roleFeaturesRepository.save(rf);
        return ResponseEntity.ok("Deleted (set isActive=false)");
    }

    // PATCH: cập nhật trạng thái isActive (bật/tắt)
    @PatchMapping("/active")
    public ResponseEntity<?> updateActiveStatus(@RequestBody RoleFeatureRequestDTO dto, @RequestParam boolean isActive) {
        Optional<Roles> roleOpt = roleRepository.findById(dto.roleId);
        if (roleOpt.isEmpty()) {
            return ResponseEntity.badRequest().body("Role not found");
        }
        Optional<Role_Features> rfOpt = roleFeaturesRepository.findAll().stream()
                .filter(rf -> rf.getRole().getRoleId().equals(dto.roleId)
                        && rf.getFeature().getFeatureId().equals(dto.featureId))
                .findFirst();
        if (rfOpt.isEmpty()) {
            return ResponseEntity.badRequest().body("Role-Feature not found");
        }
        Role_Features rf = rfOpt.get();
        rf.setActive(isActive);
        roleFeaturesRepository.save(rf);
        return ResponseEntity.ok("Updated isActive to " + isActive);
    }
} 