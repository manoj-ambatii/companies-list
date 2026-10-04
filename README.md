# Great Place To Work® — IT Companies & Java Developer Job Tracker

An automated job search portal, career portal finder, and application tracking system built with **Spring Boot 3**, **Playwright Java**, **Playwright MCP**, and an interactive dashboard. 

The project extracts IT & IT-BPM certified companies from **Great Place To Work® (GPTW)**, discovers their career pages, and provides a real-time tracking interface specifically tailored for applying to **Java Developer** roles.

---

## 🌟 Key Features

1. **Certified IT Companies Directory**:
   - Pre-loaded with **183+ certified IT companies** recognized by Great Place To Work® (Top 25, Top 50, Top 100 IT & IT-BPM).
   - Includes company name, recognized category, employee headcount, city/location, and official GPTW certification URL.
   - Live synchronization via `POST /api/companies/sync` or the **"Sync from GPTW"** dashboard button.

2. **Smart Career Portal & Job Openings Discovery**:
   - Direct career portal mapping for top tech firms (Cisco, Accenture, Atlassian, Cadence, Blue Yonder, Experian, SAP, Salesforce, and more).
   - Direct 1-click **Java Jobs Search** targeting Google and LinkedIn (`"<Company>" "Java Developer" careers`).
   - Automated career portal lookup powered by **Playwright**.

3. **Application & Quota Tracking (The Core Goal)**:
   - **Real-Time KPI Dashboard**:
     - **Total IT Companies** certified by GPTW.
     - **Applied for Java Role** count with percentage progress bar.
     - **Left to Apply** counter (clear visibility on remaining target companies).
     - **Interviewing & Offer Pipeline** stats.
   - **1-Click Status Toggle**:
     - Quickly mark any company as **Applied** (records application date).
     - Easily undo or adjust status to **Interviewing**, **Offered**, or **Rejected**.
   - **Instant Search & Filters**:
     - Filter tabs: *All*, *Left to Apply*, *Applied*, *Interviewing*.
     - Real-time search across company names, locations, and categories.
   - **Notes & Follow-up**:
     - Add recruiter names, referral contacts, application URLs, and interview notes.

4. **Playwright MCP Integration**:
   - Configured with `.mcp.json` for Microsoft's official `@playwright/mcp`.
   - Supports both headed mode (`playwright`) and headless mode (`playwright-headless`).
   - Allows AI coding agents and MCP clients to automate browsing, applying, and scraping.

5. **Multi-Database Support (Zero Setup Required)**:
   - Out-of-the-box persistent **H2 Database** (`./data/companiesdb`) — starts immediately with zero installation.
   - Ready-to-toggle **MySQL** configuration in `src/main/resources/application.properties`.

6. **Spreadsheet Export**:
   - 1-click **CSV export** for easy import into Google Sheets or Excel.

---

## 🏗️ Architecture

```
companies_list/
├── .mcp.json                                   # Playwright MCP server configuration
├── pom.xml                                     # Maven dependencies (Spring Boot, Playwright Java, JPA, H2, Jsoup)
├── package.json                                # Node scripts for Playwright & MCP
├── README.md                                   # Comprehensive documentation
├── scripts/
│   ├── scrape-gptw.js                          # Playwright/Node GPTW scraper
│   └── search-careers.js                       # Playwright career URL automator
├── src/
│   ├── main/
│   │   ├── java/com/example/companies_list/
│   │   │   ├── CompaniesListApplication.java   # Spring Boot entry point
│   │   │   ├── config/
│   │   │   │   ├── AppConfig.java              # Jackson ObjectMapper bean
│   │   │   │   └── SecurityConfig.java         # Public access configuration
│   │   │   ├── model/
│   │   │   │   ├── Company.java                # JPA Entity
│   │   │   │   └── ApplicationStatus.java      # ApplicationStatus Enum
│   │   │   ├── repository/
│   │   │   │   └── CompanyRepository.java      # Data JPA query repository
│   │   │   ├── dto/
│   │   │   │   ├── CompanyDto.java             # REST DTO
│   │   │   │   ├── CompanyStatsDto.java        # Stats counters DTO
│   │   │   │   └── StatusUpdateRequest.java    # Status update payload
│   │   │   ├── service/
│   │   │   │   ├── CompanyService.java         # Business logic & CSV export
│   │   │   │   ├── CareersFinderService.java   # Career URL resolver & Playwright
│   │   │   │   ├── GptwScraperService.java     # Live GPTW web scraper
│   │   │   │   └── DataInitializer.java        # Auto-seeding on startup
│   │   │   └── controller/
│   │   │       ├── CompanyDashboardController.java # Dashboard web route (/)
│   │   │       └── CompanyRestController.java  # Full REST API (/api/companies)
│   │   └── resources/
│   │       ├── application.properties          # Database & server port configuration
│   │       ├── data/
│   │       │   └── gptw_companies.json         # Bundled dataset of 183+ certified IT companies
│   │       └── static/
│   │           └── index.html                  # Responsive Web Dashboard UI
│   └── test/
│       └── java/com/example/companies_list/
│           └── CompaniesListApplicationTests.java
```

---

## 🚀 Getting Started

### 1. Prerequisites
- **Java 17+** (JDK 17 LTS verified)
- **Maven 3.8+** (or bundled `mvnw`)
- **Node.js 18+** & **npm** (for Playwright MCP and node automation)

### 2. Run the Spring Boot Application

In the project root directory, run:
```powershell
mvn spring-boot:run
```
*(Or on Windows PowerShell: `.\mvnw.cmd spring-boot:run`)*

### 3. Open the Dashboard

Open your browser and navigate to:
👉 **[http://localhost:8080/](http://localhost:8080/)**

You will instantly see:
- Total certified IT companies (183)
- Left to Apply count
- Applied count (0 initially)
- Direct 1-click **"Mark Applied"** buttons
- Direct links to **"Careers"** and **"Java Jobs"** for every company!

---

## 🤖 Playwright MCP Setup

The project includes `.mcp.json` pre-configured for **Playwright MCP**:

```json
{
  "mcpServers": {
    "playwright": {
      "command": "npx",
      "args": ["-y", "@playwright/mcp@latest"]
    },
    "playwright-headless": {
      "command": "npx",
      "args": ["-y", "@playwright/mcp@latest", "--headless"]
    }
  }
}
```

### Running Playwright MCP manually:
- **Headed mode**:
  ```powershell
  npm run mcp:headed
  ```
- **Headless mode**:
  ```powershell
  npm run mcp:headless
  ```

---

## 🔍 Playwright & Helper Scripts

- **Scrape / Refresh certified companies from GPTW**:
  ```powershell
  npm run scrape
  ```
- **Automated career search for a specific company**:
  ```powershell
  npm run search:careers -- "Cisco"
  ```
  *(Add `--headed` to watch the browser in action)*

---

## 📡 REST API Reference

| Method | Endpoint | Description |
|---|---|---|
| `GET` | `/api/companies` | List companies (supports `?status=...&keyword=...`) |
| `GET` | `/api/companies/stats` | KPI statistics (total, applied, left to apply, %) |
| `GET` | `/api/companies/{id}` | Get single company details |
| `PUT` | `/api/companies/{id}/apply` | 1-click mark company as applied |
| `PUT` | `/api/companies/{id}/unapply` | 1-click mark company as not applied |
| `PUT` | `/api/companies/{id}/status` | Update company status, date, and notes |
| `POST` | `/api/companies` | Create a new custom company |
| `PUT` | `/api/companies/{id}` | Update company details |
| `DELETE` | `/api/companies/{id}` | Delete company |
| `POST` | `/api/companies/sync` | Trigger live scrape & sync from GPTW |
| `GET` | `/api/companies/export/csv` | Download full dataset as CSV |

---

## 🗄️ Database Configuration

By default, an embedded **H2 database** is stored at `./data/companiesdb`, so all application tracking data persists between restarts without requiring any external database.

### Optional: Switching to MySQL
To use MySQL:
1. Ensure your local MySQL server is running.
2. Edit `src/main/resources/application.properties` and uncomment the MySQL block:
   ```properties
   spring.datasource.url=jdbc:mysql://localhost:3306/companies_db?createDatabaseIfNotExist=true&useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC
   spring.datasource.username=root
   spring.datasource.password=your_password
   spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver
   ```

### Accessing H2 Web Console
- URL: `http://localhost:8080/h2-console`
- JDBC URL: `jdbc:h2:file:./data/companiesdb`
- Username: `sa`
- Password: *(leave blank)*
