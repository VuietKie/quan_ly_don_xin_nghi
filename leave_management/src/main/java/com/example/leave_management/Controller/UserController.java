package com.example.leave_management.Controller;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.leave_management.Entity.Role_Features;
import com.example.leave_management.Entity.Roles;
import com.example.leave_management.Entity.Users;
import com.example.leave_management.JwtUtil;
import com.example.leave_management.Repository.RoleFeaturesRepository;
import com.example.leave_management.Repository.RoleRepository;
import com.example.leave_management.Repository.UserRepository;

@RestController
@RequestMapping("/auth")
public class UserController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private RoleFeaturesRepository roleFeaturesRepository;

    // DTO cho user trả về
    public static class UserDTO {
        public Long userId;
        public String username;
        public String fullName;
        public String email;
        public Long departmentId;
        public String departmentName;
        public Long roleId;
        public String roleName;
        public Long managerId;

        public UserDTO(Users user) {
            this.userId = user.getUserId();
            this.username = user.getUsername();
            this.fullName = user.getFullName();
            this.email = user.getEmail();
            this.departmentId = user.getDepartment() != null ? user.getDepartment().getDepartmentId() : null;
            this.departmentName = user.getDepartment() != null ? user.getDepartment().getDepartmentName() : null;
            this.roleId = user.getRole() != null ? user.getRole().getRoleId() : null;
            this.roleName = user.getRole() != null ? user.getRole().getRoleName() : null;
            this.managerId = user.getManager() != null ? user.getManager().getUserId() : null;
        }
    }

    @GetMapping("/all")
    public ResponseEntity<List<UserDTO>> getAllUsers() {
        List<UserDTO> users = userRepository.findAll().stream().map(UserDTO::new).toList();
        return ResponseEntity.ok(users);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getUserById(@PathVariable Long id) {
        Optional<Users> userOpt = userRepository.findById(id);
        if (userOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(new UserDTO(userOpt.get()));
    }

    @PostMapping("/add")
    public ResponseEntity<?> addUser(@RequestBody Users user) {
        if (user.getUsername() == null || user.getUsername().isEmpty() ||
            user.getPassword() == null || user.getPassword().isEmpty() ||
            user.getFullName() == null || user.getFullName().isEmpty() ||
            user.getRole() == null) {
            return ResponseEntity.badRequest().body("Missing required fields");
        }
        // Kiểm tra trùng username
        if (userRepository.findAll().stream().anyMatch(u -> u.getUsername().equals(user.getUsername()))) {
            return ResponseEntity.badRequest().body("Username already exists");
        }
        Users saved = userRepository.save(user);
        return ResponseEntity.ok(saved);
    }

    @PatchMapping("/update/{id}")
    public ResponseEntity<?> updateUser(@PathVariable Long id, @RequestBody Users update) {
        Optional<Users> userOpt = userRepository.findById(id);
        if (userOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        Users user = userOpt.get();
        if (update.getFullName() != null && !update.getFullName().isEmpty()) {
            user.setFullName(update.getFullName());
        }
        if (update.getPassword() != null && !update.getPassword().isEmpty()) {
            user.setPassword(update.getPassword());
        }
        if (update.getEmail() != null && !update.getEmail().isEmpty()) {
            user.setEmail(update.getEmail());
        }
        if (update.getRole() != null) {
            user.setRole(update.getRole());
        }
        if (update.getDepartment() != null) {
            user.setDepartment(update.getDepartment());
        }
        if (update.getManager() != null) {
            user.setManager(update.getManager());
        }
        Users saved = userRepository.save(user);
        return ResponseEntity.ok(saved);
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest) {
        Optional<Users> userOpt = userRepository.findAll().stream()
            .filter(u -> u.getUsername().equals(loginRequest.getUsername()) && u.getPassword().equals(loginRequest.getPassword()))
            .findFirst();
        if (userOpt.isPresent()) {
            Users user = userOpt.get();
            String role = user.getRole().getRoleName();
            Long roleId = user.getRole() != null ? user.getRole().getRoleId() : null;
            String token = jwtUtil.generateToken(user.getUsername(), role, String.valueOf(user.getUserId()), roleId);

            // Tạo cookie chứa token
            ResponseCookie cookie = ResponseCookie.from("token", token)
                .httpOnly(false) // Để true nếu muốn bảo mật, false để JS đọc được
                .secure(false)   // Để true nếu dùng HTTPS
                .path("/")
                .maxAge(10 * 60 * 60) // 10 giờ
                .sameSite("Lax")
                .build();

            return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, cookie.toString())
                .body(new LoginResponse(token, role, user.getUsername(), roleId));
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid username or password");
        }
    }

    @GetMapping("/features")
    public ResponseEntity<?> getFeaturesByRole(@RequestParam String role) {
        Roles roleEntity = roleRepository.findByRoleName(role);
        if (roleEntity == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Role not found");
        }
        List<Role_Features> roleFeatures = roleFeaturesRepository.findByRole(roleEntity);
        List<String> features = roleFeatures.stream()
            .map(rf -> rf.getFeature().getFeatureName())
            .collect(Collectors.toList());
        return ResponseEntity.ok(features);
    }
} 