package com.example.leave_management.Controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.leave_management.Entity.Features;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

@RestController
@RequestMapping("/features")
public class FeaturesController {
    @PersistenceContext
    private EntityManager entityManager;

    // Lấy tất cả features
    @GetMapping("/all")
    public ResponseEntity<List<Features>> getAllFeatures() {
        List<Features> list = entityManager.createQuery("SELECT f FROM Features f", Features.class).getResultList();
        return ResponseEntity.ok(list);
    }

    // Lấy feature theo id
    @GetMapping("/{id}")
    public ResponseEntity<?> getFeatureById(@PathVariable Long id) {
        Features feature = entityManager.find(Features.class, id);
        if (feature == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(feature);
    }

    // Thêm mới feature
    @PostMapping("/add")
    public ResponseEntity<?> addFeature(@RequestBody Features feature) {
        entityManager.persist(feature);
        return ResponseEntity.ok(feature);
    }

    // Cập nhật feature
    @PatchMapping("/update/{id}")
    public ResponseEntity<?> updateFeature(@PathVariable Long id, @RequestBody Features update) {
        Features feature = entityManager.find(Features.class, id);
        if (feature == null) {
            return ResponseEntity.notFound().build();
        }
        if (update.getFeatureName() != null) {
            feature.setFeatureName(update.getFeatureName());
        }
        entityManager.merge(feature);
        return ResponseEntity.ok(feature);
    }

    // Xóa feature (nếu cần)
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> deleteFeature(@PathVariable Long id) {
        Features feature = entityManager.find(Features.class, id);
        if (feature == null) {
            return ResponseEntity.notFound().build();
        }
        entityManager.remove(feature);
        return ResponseEntity.ok("Deleted");
    }
} 