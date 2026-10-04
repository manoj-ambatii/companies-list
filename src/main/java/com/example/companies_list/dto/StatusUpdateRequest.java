package com.example.companies_list.dto;

import com.example.companies_list.model.ApplicationStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StatusUpdateRequest {
    private ApplicationStatus status;
    private LocalDate appliedDate;
    private String notes;
}
