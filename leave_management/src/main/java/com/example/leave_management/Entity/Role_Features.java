package com.example.leave_management.Entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "Role_Features")
@IdClass(RoleFeatureId.class)
public class Role_Features {
    @Id
    @ManyToOne
    @JoinColumn(name = "role_id", nullable = false)
    private Roles role;

    @Id
    @ManyToOne
    @JoinColumn(name = "feature_id", nullable = false)
    private Features feature;

    @Column(name = "is_active", nullable = false)
    private boolean isActive;
} 