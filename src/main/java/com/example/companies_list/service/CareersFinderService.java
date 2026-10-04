package com.example.companies_list.service;

import com.microsoft.playwright.Browser;
import com.microsoft.playwright.BrowserType;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.Playwright;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

@Service
public class CareersFinderService {

    private static final Logger log = LoggerFactory.getLogger(CareersFinderService.class);

    private static final Map<String, String> KNOWN_CAREERS = new HashMap<>();

    static {
        KNOWN_CAREERS.put("accenture", "https://www.accenture.com/in-en/careers");
        KNOWN_CAREERS.put("admiral", "https://www.admiralgroup.co.uk/careers");
        KNOWN_CAREERS.put("startek", "https://www.startek.com/careers/");
        KNOWN_CAREERS.put("ags health", "https://www.agshealth.com/careers");
        KNOWN_CAREERS.put("altudo", "https://www.altudo.co/careers");
        KNOWN_CAREERS.put("blue yonder", "https://careers.blueyonder.com/");
        KNOWN_CAREERS.put("cadence", "https://cadence.wd1.myworkdayjobs.com/External_Careers");
        KNOWN_CAREERS.put("cisco", "https://jobs.cisco.com/");
        KNOWN_CAREERS.put("experian", "https://www.experian.com/careers/");
        KNOWN_CAREERS.put("ey", "https://www.ey.com/en_in/careers");
        KNOWN_CAREERS.put("firstsource", "https://www.firstsource.com/careers/");
        KNOWN_CAREERS.put("happiest minds", "https://www.happiestminds.com/careers/");
        KNOWN_CAREERS.put("hcl", "https://www.hcltech.com/careers");
        KNOWN_CAREERS.put("infosys", "https://www.infosys.com/careers/");
        KNOWN_CAREERS.put("intuit", "https://www.intuit.com/careers/");
        KNOWN_CAREERS.put("nagarro", "https://www.nagarro.com/en/careers");
        KNOWN_CAREERS.put("persistent", "https://www.persistent.com/careers/");
        KNOWN_CAREERS.put("ptc", "https://www.ptc.com/en/careers");
        KNOWN_CAREERS.put("salesforce", "https://www.salesforce.com/company/careers/");
        KNOWN_CAREERS.put("sap", "https://jobs.sap.com/");
        KNOWN_CAREERS.put("tcs", "https://www.tcs.com/careers");
        KNOWN_CAREERS.put("tata consultancy", "https://www.tcs.com/careers");
        KNOWN_CAREERS.put("tech mahindra", "https://careers.techmahindra.com/");
        KNOWN_CAREERS.put("wipro", "https://careers.wipro.com/");
        KNOWN_CAREERS.put("atlassian", "https://www.atlassian.com/company/careers");
        KNOWN_CAREERS.put("capgemini", "https://www.capgemini.com/in-en/careers/");
        KNOWN_CAREERS.put("cognizant", "https://careers.cognizant.com/global-en");
        KNOWN_CAREERS.put("dell", "https://jobs.dell.com/");
        KNOWN_CAREERS.put("oracle", "https://www.oracle.com/corporate/careers/");
        KNOWN_CAREERS.put("vmware", "https://careers.vmware.com/");
        KNOWN_CAREERS.put("servicenow", "https://careers.servicenow.com/");
        KNOWN_CAREERS.put("adobe", "https://www.adobe.com/careers.html");
        KNOWN_CAREERS.put("rackspace", "https://www.rackspace.com/careers");
        KNOWN_CAREERS.put("nutanix", "https://www.nutanix.com/company/careers");
        KNOWN_CAREERS.put("red hat", "https://www.redhat.com/en/jobs");
        KNOWN_CAREERS.put("paypal", "https://careers.pypl.com/");
        KNOWN_CAREERS.put("mastercard", "https://careers.mastercard.com/");
        KNOWN_CAREERS.put("visa", "https://corporate.visa.com/en/careers.html");
        KNOWN_CAREERS.put("goldman", "https://www.goldmansachs.com/careers/");
        KNOWN_CAREERS.put("morgan stanley", "https://www.morganstanley.com/people/opportunities");
        KNOWN_CAREERS.put("jpmorgan", "https://careers.jpmorgan.com/");
        KNOWN_CAREERS.put("barclays", "https://search.jobs.barclays/");
        KNOWN_CAREERS.put("fidelity", "https://jobs.fidelity.com/");
        KNOWN_CAREERS.put("fiserv", "https://www.fiserv.com/en/about-fiserv/careers.html");
        KNOWN_CAREERS.put("fis", "https://careers.fisglobal.com/");
        KNOWN_CAREERS.put("palo alto", "https://jobs.paloaltonetworks.com/");
        KNOWN_CAREERS.put("crowdstrike", "https://www.crowdstrike.com/careers/");
        KNOWN_CAREERS.put("zscaler", "https://www.zscaler.com/careers");
        KNOWN_CAREERS.put("akamai", "https://www.akamai.com/careers");
        KNOWN_CAREERS.put("splunk", "https://www.splunk.com/en_us/careers.html");
        KNOWN_CAREERS.put("synopsys", "https://www.synopsys.com/company/careers.html");
        KNOWN_CAREERS.put("ansys", "https://www.ansys.com/careers");
        KNOWN_CAREERS.put("autodesk", "https://www.autodesk.com/careers");
        KNOWN_CAREERS.put("mathworks", "https://www.mathworks.com/company/jobs/opportunities.html");
        KNOWN_CAREERS.put("teradata", "https://careers.teradata.com/");
        KNOWN_CAREERS.put("informatica", "https://www.informatica.com/about-us/careers.html");
        KNOWN_CAREERS.put("snowflake", "https://careers.snowflake.com/");
        KNOWN_CAREERS.put("databricks", "https://www.databricks.com/company/careers");
        KNOWN_CAREERS.put("confluent", "https://www.confluent.io/careers/");
        KNOWN_CAREERS.put("mongodb", "https://www.mongodb.com/company/careers");
        KNOWN_CAREERS.put("elastic", "https://www.elastic.co/about/careers");
        KNOWN_CAREERS.put("freshworks", "https://www.freshworks.com/company/careers/");
        KNOWN_CAREERS.put("zoho", "https://www.zoho.com/careers/");
        KNOWN_CAREERS.put("browserstack", "https://www.browserstack.com/careers");
        KNOWN_CAREERS.put("postman", "https://www.postman.com/company/careers/");
        KNOWN_CAREERS.put("chargebee", "https://www.chargebee.com/careers/");
        KNOWN_CAREERS.put("razorpay", "https://razorpay.com/jobs/");
        KNOWN_CAREERS.put("phonepe", "https://www.phonepe.com/careers/");
        KNOWN_CAREERS.put("swiggy", "https://careers.swiggy.com/");
        KNOWN_CAREERS.put("zomato", "https://www.zomato.com/careers");
        KNOWN_CAREERS.put("flipkart", "https://www.flipkartcareers.com/");
        KNOWN_CAREERS.put("meesho", "https://www.meesho.io/careers");
        KNOWN_CAREERS.put("delhivery", "https://www.delhivery.com/careers");
        KNOWN_CAREERS.put("thoughtworks", "https://www.thoughtworks.com/careers");
        KNOWN_CAREERS.put("epam", "https://www.epam.com/careers");
        KNOWN_CAREERS.put("hexaware", "https://hexaware.com/careers/");
        KNOWN_CAREERS.put("mphasis", "https://careers.mphasis.com/");
        KNOWN_CAREERS.put("globallogic", "https://www.globallogic.com/careers/");
        KNOWN_CAREERS.put("zensar", "https://www.zensar.com/careers");
        KNOWN_CAREERS.put("cyient", "https://www.cyient.com/careers");
        KNOWN_CAREERS.put("birlasoft", "https://www.birlasoft.com/careers");
        KNOWN_CAREERS.put("coforge", "https://www.coforge.com/careers");
        KNOWN_CAREERS.put("kpit", "https://www.kpit.com/careers/");
    }

    private static final Pattern CLEAN_PATTERN = Pattern.compile(
        "\\b(Private Limited|Pvt\\.? Ltd\\.?|Limited|LLP|Inc\\.?|Corp\\.?|Corporation|Services|Technologies|Technology|India|Solutions)\\b",
        Pattern.CASE_INSENSITIVE
    );

    public String cleanCompanyName(String rawName) {
        if (rawName == null) return "";
        String cleaned = rawName.replaceAll("\\(.*?\\)", "").trim();
        cleaned = CLEAN_PATTERN.matcher(cleaned).replaceAll("").trim();
        cleaned = cleaned.replaceAll("\\s+", " ").trim();
        return cleaned.isEmpty() ? rawName : cleaned;
    }

    public String resolveCareersUrl(String rawName, String cleanName) {
        String lowerRaw = (rawName != null ? rawName : "").toLowerCase();
        String lowerClean = (cleanName != null ? cleanName : "").toLowerCase();

        for (Map.Entry<String, String> entry : KNOWN_CAREERS.entrySet()) {
            if (lowerRaw.contains(entry.getKey()) || lowerClean.contains(entry.getKey())) {
                return entry.getValue();
            }
        }

        String domainName = lowerClean.replaceAll("[^a-z0-9]", "");
        if (!domainName.isEmpty()) {
            return "https://www." + domainName + ".com/careers";
        }
        return "https://www.google.com/search?q=" + encode(cleanName + " careers");
    }

    public String buildJobSearchUrl(String cleanName, String role) {
        String query = cleanName + " careers " + (role != null ? role : "Java Developer") + " jobs";
        return "https://www.google.com/search?q=" + encode(query);
    }

    public String buildLinkedInSearchUrl(String cleanName, String role) {
        String keywords = cleanName + " " + (role != null ? role : "Java Developer");
        return "https://www.linkedin.com/jobs/search/?keywords=" + encode(keywords) + "&location=India";
    }

    public String searchCareersWithPlaywright(String companyName) {
        log.info("Starting Playwright to search careers for {}", companyName);
        try (Playwright playwright = Playwright.create()) {
            Browser browser = playwright.chromium().launch(new BrowserType.LaunchOptions().setHeadless(true));
            Page page = browser.newPage();
            String query = companyName + " careers java developer";
            String searchUrl = "https://duckduckgo.com/?q=" + encode(query);
            page.navigate(searchUrl);
            page.waitForTimeout(2000);

            Object firstResultUrl = page.evalOnSelector(
                "a[data-testid=\"result-title-a\"], a.result__url",
                "el => el ? el.href : null"
            );

            browser.close();
            if (firstResultUrl != null) {
                return firstResultUrl.toString();
            }
        } catch (Exception e) {
            log.warn("Playwright search error for {}: {}", companyName, e.getMessage());
        }
        return resolveCareersUrl(companyName, cleanCompanyName(companyName));
    }

    private String encode(String value) {
        return URLEncoder.encode(value, StandardCharsets.UTF_8);
    }
}
