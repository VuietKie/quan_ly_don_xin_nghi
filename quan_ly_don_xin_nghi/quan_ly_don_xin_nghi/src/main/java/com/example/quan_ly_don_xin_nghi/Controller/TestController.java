package com.example.quan_ly_don_xin_nghi.Controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/test")
@CrossOrigin(origins = "http://localhost:8090")
public class TestController {

    @GetMapping
    public ResponseEntity<Map<String, Object>> testGet() {
        Map<String, Object> response = new HashMap<>();
        response.put("message", "GET request successful");
        response.put("timestamp", System.currentTimeMillis());
        return ResponseEntity.ok(response);
    }

    @PostMapping
    public ResponseEntity<Map<String, Object>> testPost(@RequestBody Map<String, String> request) {
        Map<String, Object> response = new HashMap<>();
        response.put("message", "POST request successful");
        response.put("receivedData", request);
        response.put("timestamp", System.currentTimeMillis());
        return ResponseEntity.ok(response);
    }
} 