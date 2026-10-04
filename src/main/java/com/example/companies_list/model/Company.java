package com.example.companies_list.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "companies")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Company {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 500)
    private String name;

    @Column(name = "clean_name", length = 300)
    private String cleanName;

    @Column(length = 200)
    private String category;

    @Column(name = "employee_count", length = 100)
    private String employeeCount;

    @Column(length = 300)
    private String location;

    @Column(name = "gptw_profile_url", length = 1000)
    private String gptwProfileUrl;

    @Column(name = "careers_url", length = 1000)
    private String careersUrl;

    @Column(name = "job_search_url", length = 1000)
    private String jobSearchUrl;

    @Column(name = "linkedin_search_url", length = 1000)
    private String linkedInSearchUrl;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private ApplicationStatus status = ApplicationStatus.NOT_APPLIED;

    @Column(name = "applied_date")
    private LocalDate appliedDate;

    @Column(name = "target_role", length = 200)
    @Builder.Default
    private String targetRole = "Java Developer";

    @Column(length = 2000)
    private String notes;

    @Column(length = 200)
    private String source;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        if (this.status == null) {
            this.status = ApplicationStatus.NOT_APPLIED;
        }
        if (this.targetRole == null || this.targetRole.isBlank()) {
            this.targetRole = "Java Developer";
        }
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}
