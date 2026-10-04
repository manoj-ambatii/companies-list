/**
 * Playwright Career Portal & Job Search Automator
 * Uses Playwright (headed or headless) to search for company career portals
 * and Java Developer openings.
 */
const { chromium } = require('playwright');
const fs = require('fs');
const path = require('path');

const DATA_FILE = path.join(__dirname, '..', 'src', 'main', 'resources', 'data', 'gptw_companies.json');

async function searchCareersForCompany(companyName, headed = false) {
  console.log(`\n[Playwright] Searching careers for: ${companyName}...`);
  const browser = await chromium.launch({
    headless: !headed,
    args: ['--disable-blink-features=AutomationControlled']
  });
  const context = await browser.newContext({
    userAgent: 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/122.0.0.0 Safari/537.36'
  });
  const page = await context.newPage();

  try {
    const query = `${companyName} careers java developer`;
    const searchUrl = `https://duckduckgo.com/?q=${encodeURIComponent(query)}`;
    console.log(`Navigating to: ${searchUrl}`);
    await page.goto(searchUrl, { waitUntil: 'domcontentloaded', timeout: 15000 });
    await page.waitForTimeout(2000);

    const results = await page.$$eval('a[data-testid="result-title-a"], a.result__url', elements => {
      return elements.map(el => ({
        title: el.innerText.trim(),
        url: el.href
      })).filter(r => r.url && !r.url.includes('duckduckgo.com'));
    });

    console.log(`Found ${results.length} search results:`);
    results.slice(0, 5).forEach((r, i) => console.log(`  ${i + 1}. [${r.title}] -> ${r.url}`));
    return results;
  } catch (err) {
    console.error(`Error during search: ${err.message}`);
    return [];
  } finally {
    await browser.close();
  }
}

async function main() {
  const args = process.argv.slice(2);
  const headed = args.includes('--headed');
  const targetCompany = args.find(a => !a.startsWith('--')) || 'Cisco';

  await searchCareersForCompany(targetCompany, headed);
}

if (require.main === module) {
  main();
}

module.exports = { searchCareersForCompany };
