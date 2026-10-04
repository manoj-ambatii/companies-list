/**
 * Great Place To Work (GPTW) IT Companies Scraper
 * Uses Playwright and REST API to extract certified IT companies,
 * resolves career portals, and outputs enriched dataset.
 */
const fs = require('fs');
const path = require('path');

const OUTPUT_PATH = path.join(__dirname, '..', 'src', 'main', 'resources', 'data', 'gptw_companies.json');

const KNOWN_CAREERS = {
  "accenture": "https://www.accenture.com/in-en/careers",
  "admiral": "https://www.admiralgroup.co.uk/careers",
  "startek": "https://www.startek.com/careers/",
  "ags health": "https://www.agshealth.com/careers",
  "altudo": "https://www.altudo.co/careers",
  "blue yonder": "https://careers.blueyonder.com/",
  "cadence": "https://cadence.wd1.myworkdayjobs.com/External_Careers",
  "cisco": "https://jobs.cisco.com/",
  "experian": "https://www.experian.com/careers/",
  "ey": "https://www.ey.com/en_in/careers",
  "firstsource": "https://www.firstsource.com/careers/",
  "happiest minds": "https://www.happiestminds.com/careers/",
  "hcl": "https://www.hcltech.com/careers",
  "infosys": "https://www.infosys.com/careers/",
  "intuit": "https://www.intuit.com/careers/",
  "nagarro": "https://www.nagarro.com/en/careers",
  "persistent": "https://www.persistent.com/careers/",
  "ptc": "https://www.ptc.com/en/careers",
  "salesforce": "https://www.salesforce.com/company/careers/",
  "sap": "https://jobs.sap.com/",
  "tcs": "https://www.tcs.com/careers",
  "tata consultancy": "https://www.tcs.com/careers",
  "tech mahindra": "https://careers.techmahindra.com/",
  "wipro": "https://careers.wipro.com/",
  "atlassian": "https://www.atlassian.com/company/careers",
  "capgemini": "https://www.capgemini.com/in-en/careers/",
  "cognizant": "https://careers.cognizant.com/global-en",
  "dell": "https://jobs.dell.com/",
  "oracle": "https://www.oracle.com/corporate/careers/",
  "vmware": "https://careers.vmware.com/",
  "servicenow": "https://careers.servicenow.com/",
  "adobe": "https://www.adobe.com/careers.html",
  "rackspace": "https://www.rackspace.com/careers",
  "nutanix": "https://www.nutanix.com/company/careers",
  "red hat": "https://www.redhat.com/en/jobs",
  "paypal": "https://careers.pypl.com/",
  "mastercard": "https://careers.mastercard.com/",
  "visa": "https://corporate.visa.com/en/careers.html",
  "goldman": "https://www.goldmansachs.com/careers/",
  "morgan stanley": "https://www.morganstanley.com/people/opportunities",
  "jpmorgan": "https://careers.jpmorgan.com/",
  "barclays": "https://search.jobs.barclays/",
  "fidelity": "https://jobs.fidelity.com/",
  "fiserv": "https://www.fiserv.com/en/about-fiserv/careers.html",
  "fis": "https://careers.fisglobal.com/",
  "palo alto": "https://jobs.paloaltonetworks.com/",
  "crowdstrike": "https://www.crowdstrike.com/careers/",
  "zscaler": "https://www.zscaler.com/careers",
  "akamai": "https://www.akamai.com/careers",
  "splunk": "https://www.splunk.com/en_us/careers.html",
  "synopsys": "https://www.synopsys.com/company/careers.html",
  "ansys": "https://www.ansys.com/careers",
  "autodesk": "https://www.autodesk.com/careers",
  "mathworks": "https://www.mathworks.com/company/jobs/opportunities.html",
  "teradata": "https://careers.teradata.com/",
  "informatica": "https://www.informatica.com/about-us/careers.html",
  "snowflake": "https://careers.snowflake.com/",
  "databricks": "https://www.databricks.com/company/careers",
  "confluent": "https://www.confluent.io/careers/",
  "mongodb": "https://www.mongodb.com/company/careers",
  "elastic": "https://www.elastic.co/about/careers",
  "freshworks": "https://www.freshworks.com/company/careers/",
  "zoho": "https://www.zoho.com/careers/",
  "browserstack": "https://www.browserstack.com/careers",
  "postman": "https://www.postman.com/company/careers/",
  "chargebee": "https://www.chargebee.com/careers/",
  "razorpay": "https://razorpay.com/jobs/",
  "phonepe": "https://www.phonepe.com/careers/",
  "swiggy": "https://careers.swiggy.com/",
  "zomato": "https://www.zomato.com/careers",
  "flipkart": "https://www.flipkartcareers.com/",
  "meesho": "https://www.meesho.io/careers",
  "delhivery": "https://www.delhivery.com/careers",
  "thoughtworks": "https://www.thoughtworks.com/careers",
  "epam": "https://www.epam.com/careers",
  "hexaware": "https://hexaware.com/careers/",
  "mphasis": "https://careers.mphasis.com/",
  "globallogic": "https://www.globallogic.com/careers/",
  "zensar": "https://www.zensar.com/careers",
  "cyient": "https://www.cyient.com/careers",
  "birlasoft": "https://www.birlasoft.com/careers",
  "coforge": "https://www.coforge.com/careers",
  "kpit": "https://www.kpit.com/careers/"
};

function cleanCompanyName(raw) {
  let name = raw.replace(/\((.*?)\)/g, '').trim();
  name = name.replace(/\b(Private Limited|Pvt\.? Ltd\.?|Limited|LLP|Inc\.?|Corp\.?|Corporation|Services|Technologies|Technology|India|Solutions)\b/gi, '').trim();
  name = name.replace(/\s+/g, ' ').trim();
  return name || raw;
}

function findCareersUrl(rawName, cleanName) {
  const lowerRaw = rawName.toLowerCase();
  const lowerClean = cleanName.toLowerCase();

  for (const [key, url] of Object.entries(KNOWN_CAREERS)) {
    if (lowerRaw.includes(key) || lowerClean.includes(key)) {
      return url;
    }
  }

  const domainPart = cleanName.toLowerCase().replace(/[^a-z0-9]/g, '');
  return `https://www.${domainPart}.com/careers`;
}

async function scrapeGPTW() {
  console.log('--- Great Place To Work IT Companies Scraper ---');
  const pages = [1866, 1871, 71397];
  const allCompanies = [];
  const seen = new Set();

  for (const pageId of pages) {
    try {
      console.log(`Scraping GPTW page ID: ${pageId}...`);
      const response = await fetch(`https://www.greatplacetowork.in/wp-json/wp/v2/pages/${pageId}`);
      if (!response.ok) {
        console.warn(`Failed to fetch page ${pageId}: ${response.status}`);
        continue;
      }
      const data = await response.json();
      const html = data.content?.rendered || '';
      const yearMatch = data.title?.rendered?.match(/202\d/);
      const year = yearMatch ? yearMatch[0] : '2025';
      const rows = [...html.matchAll(/<tr[^>]*>([\s\S]*?)<\/tr>/gis)];

      for (let i = 1; i < rows.length; i++) {
        const rowHtml = rows[i][1];
        const tds = [...rowHtml.matchAll(/<td[^>]*>([\s\S]*?)<\/td>/gis)].map(m => m[1].replace(/<[^>]+>/g, '').trim());
        if (tds.length >= 4) {
          const name = tds[0];
          const category = tds[1] || 'Top IT & IT-BPM';
          const employees = tds[2] || 'N/A';
          const location = tds[3] || 'India';
          const profileUrl = tds[4] || '';

          const norm = name.toLowerCase().replace(/[^a-z0-9]/g, '');
          if (!seen.has(norm) && name.length > 2) {
            seen.add(norm);
            const cleanName = cleanCompanyName(name);
            const careersUrl = findCareersUrl(name, cleanName);
            const googleSearch = `https://www.google.com/search?q=${encodeURIComponent(cleanName + ' careers java developer jobs')}`;
            const linkedInSearch = `https://www.linkedin.com/jobs/search/?keywords=${encodeURIComponent(cleanName + ' Java Developer')}&location=India`;

            allCompanies.push({
              name,
              cleanName,
              category: `${category} (IT & IT-BPM ${year})`,
              employees,
              location,
              gptwProfileUrl: profileUrl,
              careersUrl,
              jobSearchUrl: googleSearch,
              linkedInSearchUrl: linkedInSearch,
              targetRole: 'Java Developer',
              status: 'NOT_APPLIED',
              source: `Great Place To Work India ${year}`
            });
          }
        }
      }
    } catch (err) {
      console.error(`Error scraping page ${pageId}:`, err.message);
    }
  }

  console.log(`Successfully scraped ${allCompanies.length} unique certified IT companies.`);
  fs.mkdirSync(path.dirname(OUTPUT_PATH), { recursive: true });
  fs.writeFileSync(OUTPUT_PATH, JSON.stringify(allCompanies, null, 2));
  console.log(`Saved enriched companies list to: ${OUTPUT_PATH}`);
}

scrapeGPTW().catch(console.error);
