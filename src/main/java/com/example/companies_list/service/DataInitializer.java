package com.example.companies_list.service;

import com.example.companies_list.model.ApplicationStatus;
import com.example.companies_list.model.Company;
import com.example.companies_list.repository.CompanyRepository;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import java.io.InputStream;
import java.util.List;
import java.util.Map;

@Component
public class DataInitializer implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(DataInitializer.class);

    private final CompanyRepository companyRepository;
    private final GptwScraperService gptwScraperService;
    private final CareersFinderService careersFinderService;
    private final ObjectMapper objectMapper;

    public DataInitializer(CompanyRepository companyRepository,
                           GptwScraperService gptwScraperService,
                           CareersFinderService careersFinderService,
                           ObjectMapper objectMapper) {
        this.companyRepository = companyRepository;
        this.gptwScraperService = gptwScraperService;
        this.careersFinderService = careersFinderService;
        this.objectMapper = objectMapper;
    }

    @Override
    public void run(String... args) {
        long count = companyRepository.count();
        if (count > 0) {
            log.info("Database already contains {} companies. Skipping initial seed.", count);
            return;
        }

        log.info("Company database is empty. Initializing with Great Place To Work certified IT companies...");
        try {
            ClassPathResource resource = new ClassPathResource("data/gptw_companies.json");
            if (resource.exists()) {
                try (InputStream is = resource.getInputStream()) {
                    List<Map<String, Object>> list = objectMapper.readValue(is, new TypeReference<>() {});
                    int savedCount = 0;
                    for (Map<String, Object> map : list) {
                        String name = (String) map.get("name");
                        if (name == null || name.isBlank()) continue;

                        String cleanName = (String) map.getOrDefault("cleanName", careersFinderService.cleanCompanyName(name));
                        String category = (String) map.getOrDefault("category", "Top IT & IT-BPM");
                        String employees = (String) map.getOrDefault("employees", "N/A");
                        String location = (String) map.getOrDefault("location", "India");
                        String gptwUrl = (String) map.getOrDefault("gptwProfileUrl", "");
                        String careersUrl = (String) map.getOrDefault("careersUrl", careersFinderService.resolveCareersUrl(name, cleanName));
                        String jobSearchUrl = (String) map.getOrDefault("jobSearchUrl", careersFinderService.buildJobSearchUrl(cleanName, "Java Developer"));
                        String linkedInUrl = (String) map.getOrDefault("linkedInSearchUrl", careersFinderService.buildLinkedInSearchUrl(cleanName, "Java Developer"));
                        String source = (String) map.getOrDefault("source", "Great Place To Work");

                        Company company = Company.builder()
                                .name(name)
                                .cleanName(cleanName)
                                .category(category)
                                .employeeCount(employees)
                                .location(location)
                                .gptwProfileUrl(gptwUrl)
                                .careersUrl(careersUrl)
                                .jobSearchUrl(jobSearchUrl)
                                .linkedInSearchUrl(linkedInUrl)
                                .status(ApplicationStatus.NOT_APPLIED)
                                .targetRole("Java Developer")
                                .source(source)
                                .build();

                        companyRepository.save(company);
                        savedCount++;
                    }
                    log.info("Successfully seeded {} Great Place To Work certified IT companies into database!", savedCount);
                    return;
                }
            }
        } catch (Exception e) {
            log.warn("Could not load bundled seed file ({}). Falling back to live GPTW scraping.", e.getMessage());
        }

        // Fallback: scrape live
        try {
            List<Company> synced = gptwScraperService.scrapeAndSync();
            log.info("Live scraped and seeded {} companies.", synced.size());
        } catch (Exception e) {
            log.error("Live scraper fallback failed: {}", e.getMessage());
        }
    }
}
