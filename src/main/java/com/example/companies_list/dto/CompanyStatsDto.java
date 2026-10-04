package com.example.companies_list.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CompanyStatsDto {
    private long totalCompanies;
    private long appliedCount;
    private long notAppliedCount;
    private long interviewingCount;
    private long offeredCount;
    private long rejectedCount;
    private double appliedPercentage;
}
