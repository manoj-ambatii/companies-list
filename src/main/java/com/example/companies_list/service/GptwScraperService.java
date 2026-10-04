package com.example.companies_list.service;

import com.example.companies_list.model.ApplicationStatus;
import com.example.companies_list.model.Company;
import com.example.companies_list.repository.CompanyRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class GptwScraperService {

    private static final Logger log = LoggerFactory.getLogger(GptwScraperService.class);

    private final CompanyRepository companyRepository;
    private final CareersFinderService careersFinderService;
    private final ObjectMapper objectMapper;
    private final HttpClient httpClient;

    private static final int[] GPTW_PAGE_IDS = {1866, 1871, 71397};

    public GptwScraperService(CompanyRepository companyRepository,
                             CareersFinderService careersFinderService,
                             ObjectMapper objectMapper) {
        this.companyRepository = companyRepository;
        this.careersFinderService = careersFinderService;
        this.objectMapper = objectMapper;
        this.httpClient = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(10))
                .followRedirects(HttpClient.Redirect.NORMAL)
                .build();
    }

    public List<Company> scrapeAndSync() {
        log.info("Starting live Great Place To Work sync...");
        List<Company> synced = new ArrayList<>();

        for (int pageId : GPTW_PAGE_IDS) {
            try {
                String url = "https://www.greatplacetowork.in/wp-json/wp/v2/pages/" + pageId;
                HttpRequest req = HttpRequest.newBuilder()
                        .uri(URI.create(url))
                        .header("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64)")
                        .timeout(Duration.ofSeconds(15))
                        .GET()
                        .build();

                HttpResponse<String> resp = httpClient.send(req, HttpResponse.BodyHandlers.ofString());
                if (resp.statusCode() == 200) {
                    JsonNode root = objectMapper.readTree(resp.body());
                    String rawTitle = root.path("title").path("rendered").asText();
                    String htmlContent = root.path("content").path("rendered").asText();

                    String year = "2025";
                    var matcher = java.util.regex.Pattern.compile("202\\d").matcher(rawTitle);
                    if (matcher.find()) {
                        year = matcher.group();
                    }

                    List<Company> fromPage = parseHtmlTable(htmlContent, year);
                    for (Company c : fromPage) {
                        Company saved = saveOrUpdate(c);
                        synced.add(saved);
                    }
                    log.info("Processed {} companies from GPTW page ID {}", fromPage.size(), pageId);
                }
            } catch (Exception e) {
                log.error("Failed scraping GPTW page {}: {}", pageId, e.getMessage());
            }
        }
        log.info("Completed GPTW sync. Total synced: {}", synced.size());
        return synced;
    }

    private List<Company> parseHtmlTable(String html, String year) {
        List<Company> list = new ArrayList<>();
        if (html == null || html.isBlank()) return list;

        Document doc = Jsoup.parse(html);
        Elements rows = doc.select("tr");

        for (int i = 1; i < rows.size(); i++) {
            Element row = rows.get(i);
            Elements tds = row.select("td");
            if (tds.size() >= 4) {
                String name = tds.get(0).text().trim();
                String category = tds.size() > 1 ? tds.get(1).text().trim() : "Top IT & IT-BPM";
                String employees = tds.size() > 2 ? tds.get(2).text().trim() : "N/A";
                String location = tds.size() > 3 ? tds.get(3).text().trim() : "India";
                String profileUrl = tds.size() > 4 ? tds.get(4).text().trim() : "";

                if (name.length() > 2) {
                    String cleanName = careersFinderService.cleanCompanyName(name);
                    String careersUrl = careersFinderService.resolveCareersUrl(name, cleanName);
                    String jobSearchUrl = careersFinderService.buildJobSearchUrl(cleanName, "Java Developer");
                    String linkedInUrl = careersFinderService.buildLinkedInSearchUrl(cleanName, "Java Developer");

                    Company company = Company.builder()
                            .name(name)
                            .cleanName(cleanName)
                            .category(category + " (IT & IT-BPM " + year + ")")
                            .employeeCount(employees)
                            .location(location)
                            .gptwProfileUrl(profileUrl)
                            .careersUrl(careersUrl)
                            .jobSearchUrl(jobSearchUrl)
                            .linkedInSearchUrl(linkedInUrl)
                            .status(ApplicationStatus.NOT_APPLIED)
                            .targetRole("Java Developer")
                            .source("Great Place To Work India " + year)
                            .build();

                    list.add(company);
                }
            }
        }
        return list;
    }

    private Company saveOrUpdate(Company incoming) {
        Optional<Company> existing = companyRepository.findByName(incoming.getName());
        if (existing.isPresent()) {
            Company current = existing.get();
            // Preserve user application status, applied date, and notes!
            current.setCategory(incoming.getCategory());
            current.setEmployeeCount(incoming.getEmployeeCount());
            current.setLocation(incoming.getLocation());
            if (current.getCareersUrl() == null || current.getCareersUrl().isBlank()) {
                current.setCareersUrl(incoming.getCareersUrl());
            }
            if (current.getJobSearchUrl() == null || current.getJobSearchUrl().isBlank()) {
                current.setJobSearchUrl(incoming.getJobSearchUrl());
            }
            if (current.getLinkedInSearchUrl() == null || current.getLinkedInSearchUrl().isBlank()) {
                current.setLinkedInSearchUrl(incoming.getLinkedInSearchUrl());
            }
            return companyRepository.save(current);
        } else {
            return companyRepository.save(incoming);
        }
    }
}
