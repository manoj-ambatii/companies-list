package com.example.companies_list.controller;

import com.example.companies_list.dto.CompanyDto;
import com.example.companies_list.dto.CompanyStatsDto;
import com.example.companies_list.dto.StatusUpdateRequest;
import com.example.companies_list.model.ApplicationStatus;
import com.example.companies_list.model.Company;
import com.example.companies_list.service.CompanyService;
import com.example.companies_list.service.GptwScraperService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/companies")
@CrossOrigin(origins = "*")
public class CompanyRestController {

    private final CompanyService companyService;
    private final GptwScraperService gptwScraperService;

    public CompanyRestController(CompanyService companyService,
                                 GptwScraperService gptwScraperService) {
        this.companyService = companyService;
        this.gptwScraperService = gptwScraperService;
    }

    @GetMapping
    public ResponseEntity<List<CompanyDto>> listCompanies(
            @RequestParam(required = false) ApplicationStatus status,
            @RequestParam(required = false) String keyword) {
        return ResponseEntity.ok(companyService.getCompanies(status, keyword));
    }

    @GetMapping("/{id}")
    public ResponseEntity<CompanyDto> getCompany(@PathVariable Long id) {
        return companyService.getCompanyById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/stats")
    public ResponseEntity<CompanyStatsDto> getStats() {
        return ResponseEntity.ok(companyService.getStats());
    }

    @PutMapping("/{id}/apply")
    public ResponseEntity<CompanyDto> markAsApplied(
            @PathVariable Long id,
            @RequestBody(required = false) Map<String, String> body) {
        String notes = body != null ? body.get("notes") : null;
        return ResponseEntity.ok(companyService.markAsApplied(id, notes));
    }

    @PutMapping("/{id}/unapply")
    public ResponseEntity<CompanyDto> markAsNotApplied(@PathVariable Long id) {
        return ResponseEntity.ok(companyService.markAsNotApplied(id));
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<CompanyDto> updateStatus(
            @PathVariable Long id,
            @RequestBody StatusUpdateRequest request) {
        return ResponseEntity.ok(companyService.updateStatus(id, request));
    }

    @PostMapping
    public ResponseEntity<CompanyDto> createCompany(@RequestBody CompanyDto dto) {
        return ResponseEntity.ok(companyService.createCompany(dto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<CompanyDto> updateCompany(
            @PathVariable Long id,
            @RequestBody CompanyDto dto) {
        return ResponseEntity.ok(companyService.updateCompany(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCompany(@PathVariable Long id) {
        companyService.deleteCompany(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/sync")
    public ResponseEntity<Map<String, Object>> syncGptw() {
        List<Company> synced = gptwScraperService.scrapeAndSync();
        CompanyStatsDto stats = companyService.getStats();
        return ResponseEntity.ok(Map.of(
                "message", "GPTW sync completed successfully",
                "syncedCount", synced.size(),
                "stats", stats
        ));
    }

    @GetMapping("/export/csv")
    public ResponseEntity<String> exportCsv() {
        String csv = companyService.exportCsv();
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"gptw_companies_applied_tracker.csv\"")
                .contentType(MediaType.parseMediaType("text/csv; charset=UTF-8"))
                .body(csv);
    }
}
