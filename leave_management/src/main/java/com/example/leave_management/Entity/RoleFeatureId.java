package com.example.leave_management.Entity;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RoleFeatureId implements Serializable {
    private Long role;
    private Long feature;
} 