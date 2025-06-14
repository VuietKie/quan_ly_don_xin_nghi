package com.example.quan_ly_don_xin_nghi.Entity;

import java.time.LocalDateTime;
import java.util.Date;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "Leave_Requests")
public class LeaveRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long request_id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    @NotNull(message = "User is required")
    private User user;

    @NotNull(message = "Start date is required")
    private Date start_date;

    @NotNull(message = "End date is required")
    private Date end_date;

    @NotBlank(message = "Reason is required")
    private String reason;

    @NotBlank(message = "Status is required")
    private String status;

    @ManyToOne
    @JoinColumn(name = "processed_by")
    private User processedBy;

    private String processed_reason;

    @Column(name = "created_at")
    private LocalDateTime created_at;

    @Column(name = "updated_at")
    private LocalDateTime updated_at;

    @PrePersist
    protected void onCreate() {
        created_at = LocalDateTime.now();
        updated_at = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updated_at = LocalDateTime.now();
    }

    // Getters
    public Long getRequest_id() {
        return request_id;
    }

    public User getUser() {
        return user;
    }

    public Date getStart_date() {
        return start_date;
    }

    public Date getEnd_date() {
        return end_date;
    }

    public String getReason() {
        return reason;
    }

    public String getStatus() {
        return status;
    }

    public User getProcessedBy() {
        return processedBy;
    }

    public String getProcessed_reason() {
        return processed_reason;
    }

    public LocalDateTime getCreated_at() {
        return created_at;
    }

    public LocalDateTime getUpdated_at() {
        return updated_at;
    }

    // Setters
    public void setRequest_id(Long request_id) {
        this.request_id = request_id;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public void setStart_date(Date start_date) {
        this.start_date = start_date;
    }

    public void setEnd_date(Date end_date) {
        this.end_date = end_date;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setProcessedBy(User processedBy) {
        this.processedBy = processedBy;
    }

    public void setProcessed_reason(String processed_reason) {
        this.processed_reason = processed_reason;
    }

    public void setCreated_at(LocalDateTime created_at) {
        this.created_at = created_at;
    }

    public void setUpdated_at(LocalDateTime updated_at) {
        this.updated_at = updated_at;
    }
} 