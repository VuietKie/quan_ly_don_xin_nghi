package com.example.leave_management.Entity;

import java.util.Date;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "Access_Scope")
public class Access_Scope {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "access_id")
    private Long accessId;

    @ManyToOne
    @JoinColumn(name = "accessor_id", nullable = false)
    private Users accessor;

    @ManyToOne
    @JoinColumn(name = "target_user_id")
    private Users targetUser;

    @ManyToOne
    @JoinColumn(name = "target_department_id")
    private Departments targetDepartment;

    @Column(name = "permission_type", nullable = false)
    private String permissionType;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "expiry_date")
    private Date expiryDate;
} 