package com.example.quan_ly_don_xin_nghi.Entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "Roles")
public class Role {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long role_id;

    @NotBlank(message = "Role name is required")
    @Column(unique = true)
    private String role_name;

    private String description;

    // Getters
    public Long getRole_id() {
        return role_id;
    }

    public String getRole_name() {
        return role_name;
    }

    public String getDescription() {
        return description;
    }

    // Setters
    public void setRole_id(Long role_id) {
        this.role_id = role_id;
    }

    public void setRole_name(String role_name) {
        this.role_name = role_name;
    }

    public void setDescription(String description) {
        this.description = description;
    }
} 