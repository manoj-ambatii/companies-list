package com.example.companies_list.dto;

import com.example.companies_list.model.ApplicationStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CompanyDto {
    private Long id;
    private String name;
    private String cleanName;
    private String category;
    private String employeeCount;
    private String location;
    private String gptwProfileUrl;
    private String careersUrl;
    private String jobSearchUrl;
    private String linkedInSearchUrl;
    private ApplicationStatus status;
    private LocalDate appliedDate;
    private String targetRole;
    private String notes;
    private String source;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
