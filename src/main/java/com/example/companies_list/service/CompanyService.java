package com.example.companies_list.service;

import com.example.companies_list.dto.CompanyDto;
import com.example.companies_list.dto.CompanyStatsDto;
import com.example.companies_list.dto.StatusUpdateRequest;
import com.example.companies_list.model.ApplicationStatus;
import com.example.companies_list.model.Company;
import com.example.companies_list.repository.CompanyRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class CompanyService {

    private final CompanyRepository companyRepository;
    private final CareersFinderService careersFinderService;

    public CompanyService(CompanyRepository companyRepository,
                          CareersFinderService careersFinderService) {
        this.companyRepository = companyRepository;
        this.careersFinderService = careersFinderService;
    }

    public List<CompanyDto> getCompanies(ApplicationStatus status, String keyword) {
        List<Company> list;
        if (status == null && (keyword == null || keyword.isBlank())) {
            list = companyRepository.findAllByOrderByStatusAscIdAsc();
        } else {
            list = companyRepository.searchCompanies(status, (keyword != null && !keyword.isBlank()) ? keyword.trim() : null);
        }
        return list.stream().map(this::toDto).collect(Collectors.toList());
    }

    public Optional<CompanyDto> getCompanyById(Long id) {
        return companyRepository.findById(id).map(this::toDto);
    }

    public CompanyStatsDto getStats() {
        long total = companyRepository.count();
        long applied = companyRepository.countByStatus(ApplicationStatus.APPLIED);
        long notApplied = companyRepository.countByStatus(ApplicationStatus.NOT_APPLIED);
        long interviewing = companyRepository.countByStatus(ApplicationStatus.INTERVIEWING);
        long offered = companyRepository.countByStatus(ApplicationStatus.OFFERED);
        long rejected = companyRepository.countByStatus(ApplicationStatus.REJECTED);

        double pct = total > 0 ? ((double) (applied + interviewing + offered) / total) * 100.0 : 0.0;
        // round to 1 decimal place
        pct = Math.round(pct * 10.0) / 10.0;

        return CompanyStatsDto.builder()
                .totalCompanies(total)
                .appliedCount(applied)
                .notAppliedCount(notApplied)
                .interviewingCount(interviewing)
                .offeredCount(offered)
                .rejectedCount(rejected)
                .appliedPercentage(pct)
                .build();
    }

    @Transactional
    public CompanyDto markAsApplied(Long id, String notes) {
        Company company = companyRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Company not found with ID: " + id));
        company.setStatus(ApplicationStatus.APPLIED);
        company.setAppliedDate(LocalDate.now());
        if (notes != null && !notes.isBlank()) {
            company.setNotes(notes);
        }
        return toDto(companyRepository.save(company));
    }

    @Transactional
    public CompanyDto markAsNotApplied(Long id) {
        Company company = companyRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Company not found with ID: " + id));
        company.setStatus(ApplicationStatus.NOT_APPLIED);
        company.setAppliedDate(null);
        return toDto(companyRepository.save(company));
    }

    @Transactional
    public CompanyDto updateStatus(Long id, StatusUpdateRequest request) {
        Company company = companyRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Company not found with ID: " + id));
        if (request.getStatus() != null) {
            company.setStatus(request.getStatus());
            if (request.getStatus() == ApplicationStatus.APPLIED && company.getAppliedDate() == null) {
                company.setAppliedDate(request.getAppliedDate() != null ? request.getAppliedDate() : LocalDate.now());
            } else if (request.getStatus() == ApplicationStatus.NOT_APPLIED) {
                company.setAppliedDate(null);
            }
        }
        if (request.getAppliedDate() != null) {
            company.setAppliedDate(request.getAppliedDate());
        }
        if (request.getNotes() != null) {
            company.setNotes(request.getNotes());
        }
        return toDto(companyRepository.save(company));
    }

    @Transactional
    public CompanyDto createCompany(CompanyDto dto) {
        if (companyRepository.existsByName(dto.getName())) {
            throw new IllegalArgumentException("Company with name '" + dto.getName() + "' already exists");
        }
        String clean = careersFinderService.cleanCompanyName(dto.getName());
        String careersUrl = (dto.getCareersUrl() != null && !dto.getCareersUrl().isBlank())
                ? dto.getCareersUrl()
                : careersFinderService.resolveCareersUrl(dto.getName(), clean);
        String jobSearchUrl = (dto.getJobSearchUrl() != null && !dto.getJobSearchUrl().isBlank())
                ? dto.getJobSearchUrl()
                : careersFinderService.buildJobSearchUrl(clean, dto.getTargetRole());
        String linkedInUrl = (dto.getLinkedInSearchUrl() != null && !dto.getLinkedInSearchUrl().isBlank())
                ? dto.getLinkedInSearchUrl()
                : careersFinderService.buildLinkedInSearchUrl(clean, dto.getTargetRole());

        Company company = Company.builder()
                .name(dto.getName())
                .cleanName(clean)
                .category(dto.getCategory() != null ? dto.getCategory() : "Certified IT Company")
                .employeeCount(dto.getEmployeeCount())
                .location(dto.getLocation())
                .gptwProfileUrl(dto.getGptwProfileUrl())
                .careersUrl(careersUrl)
                .jobSearchUrl(jobSearchUrl)
                .linkedInSearchUrl(linkedInUrl)
                .status(dto.getStatus() != null ? dto.getStatus() : ApplicationStatus.NOT_APPLIED)
                .appliedDate(dto.getAppliedDate())
                .targetRole(dto.getTargetRole() != null ? dto.getTargetRole() : "Java Developer")
                .notes(dto.getNotes())
                .source(dto.getSource() != null ? dto.getSource() : "Manual Entry")
                .build();

        return toDto(companyRepository.save(company));
    }

    @Transactional
    public CompanyDto updateCompany(Long id, CompanyDto dto) {
        Company company = companyRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Company not found with ID: " + id));

        if (dto.getCareersUrl() != null) company.setCareersUrl(dto.getCareersUrl());
        if (dto.getLocation() != null) company.setLocation(dto.getLocation());
        if (dto.getCategory() != null) company.setCategory(dto.getCategory());
        if (dto.getEmployeeCount() != null) company.setEmployeeCount(dto.getEmployeeCount());
        if (dto.getTargetRole() != null) company.setTargetRole(dto.getTargetRole());
        if (dto.getNotes() != null) company.setNotes(dto.getNotes());
        if (dto.getStatus() != null) company.setStatus(dto.getStatus());
        if (dto.getAppliedDate() != null) company.setAppliedDate(dto.getAppliedDate());

        return toDto(companyRepository.save(company));
    }

    @Transactional
    public void deleteCompany(Long id) {
        companyRepository.deleteById(id);
    }

    public String exportCsv() {
        List<Company> list = companyRepository.findAllByOrderByStatusAscIdAsc();
        StringBuilder sb = new StringBuilder();
        sb.append("ID,Company Name,Clean Name,Category,Employees,Location,Status,Applied Date,Target Role,Careers URL,Job Search URL,Notes\n");
        for (Company c : list) {
            sb.append(c.getId()).append(",");
            sb.append(escapeCsv(c.getName())).append(",");
            sb.append(escapeCsv(c.getCleanName())).append(",");
            sb.append(escapeCsv(c.getCategory())).append(",");
            sb.append(escapeCsv(c.getEmployeeCount())).append(",");
            sb.append(escapeCsv(c.getLocation())).append(",");
            sb.append(c.getStatus()).append(",");
            sb.append(c.getAppliedDate() != null ? c.getAppliedDate() : "").append(",");
            sb.append(escapeCsv(c.getTargetRole())).append(",");
            sb.append(escapeCsv(c.getCareersUrl())).append(",");
            sb.append(escapeCsv(c.getJobSearchUrl())).append(",");
            sb.append(escapeCsv(c.getNotes())).append("\n");
        }
        return sb.toString();
    }

    private String escapeCsv(String val) {
        if (val == null) return "\"\"";
        return "\"" + val.replace("\"", "\"\"") + "\"";
    }

    public CompanyDto toDto(Company c) {
        return CompanyDto.builder()
                .id(c.getId())
                .name(c.getName())
                .cleanName(c.getCleanName())
                .category(c.getCategory())
                .employeeCount(c.getEmployeeCount())
                .location(c.getLocation())
                .gptwProfileUrl(c.getGptwProfileUrl())
                .careersUrl(c.getCareersUrl())
                .jobSearchUrl(c.getJobSearchUrl())
                .linkedInSearchUrl(c.getLinkedInSearchUrl())
                .status(c.getStatus())
                .appliedDate(c.getAppliedDate())
                .targetRole(c.getTargetRole())
                .notes(c.getNotes())
                .source(c.getSource())
                .createdAt(c.getCreatedAt())
                .updatedAt(c.getUpdatedAt())
                .build();
    }
}
