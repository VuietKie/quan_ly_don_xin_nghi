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

    @GetMapping("/user")
    public ResponseEntity<List<Users>> getAllUsers() {
        List<Users> users = userRepository.findAll();
        return ResponseEntity.ok(users);
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest) {
        Optional<Users> userOpt = userRepository.findAll().stream()
            .filter(u -> u.getUsername().equals(loginRequest.getUsername()) && u.getPassword().equals(loginRequest.getPassword()))
            .findFirst();
        if (userOpt.isPresent()) {
            Users user = userOpt.get();
            String role = user.getRole().getRoleName();
            String token = jwtUtil.generateToken(user.getUsername(), role);

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
                .body(new LoginResponse(token, role, user.getUsername()));
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