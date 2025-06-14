package com.example.quan_ly_don_xin_nghi.Entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "Departments")
public class Department {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long department_id;

    @NotBlank(message = "Department name is required")
    @Column(unique = true)
    private String department_name;

    @OneToOne
    @JoinColumn(name = "id_manager")
    private User manager;

    // Getters
    public Long getDepartment_id() {
        return department_id;
    }

    public String getDepartment_name() {
        return department_name;
    }

    public User getManager() {
        return manager;
    }

    // Setters
    public void setDepartment_id(Long department_id) {
        this.department_id = department_id;
    }

    public void setDepartment_name(String department_name) {
        this.department_name = department_name;
    }

    public void setManager(User manager) {
        this.manager = manager;
    }
} 