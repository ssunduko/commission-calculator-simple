package com.chapman.edu.commissions.functional;

import com.chapman.edu.commissions.app.DealManagementApp;
import com.codeborne.selenide.Configuration;
import com.codeborne.selenide.Selenide;
import org.junit.jupiter.api.*;

import java.io.File;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.*;

public class DealListingSelenideTest {

    private static final String BASE_URL = "http://localhost:8080";
    private static Thread serverThread;
    private static DealManagementApp app;

    @BeforeAll
    public static void startServer() throws Exception {
        // Start the DealManagementApp server in a separate thread
        serverThread = new Thread(() -> {
            app = new DealManagementApp();
            try {
                app.start();
            } catch (Exception e) {
                throw new RuntimeException("Failed to start server", e);
            }
        });
        serverThread.setDaemon(true);
        serverThread.start();

        // Wait for server to be ready (poll the health endpoint)
        waitForServerToStart();
    }

    @AfterAll
    public static void stopServer() {
        // Server will stop automatically when JVM exits due to daemon thread
    }

    private static void waitForServerToStart() throws Exception {
        int maxAttempts = 30;
        int attempt = 0;
        boolean serverReady = false;

        System.out.println("Waiting for server to start...");

        while (attempt < maxAttempts && !serverReady) {
            try {
                URL url = new URL(BASE_URL + "/index.html");
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                connection.setConnectTimeout(1000);
                connection.setReadTimeout(1000);

                int responseCode = connection.getResponseCode();
                if (responseCode == 200 || responseCode == 401) {
                    serverReady = true;
                    System.out.println("Server is ready!");
                }
                connection.disconnect();
            } catch (Exception e) {
                // Server not ready yet, wait and retry
                Thread.sleep(1000);
                attempt++;
                System.out.println("Attempt " + attempt + "/" + maxAttempts + ": Server not ready yet...");
            }
        }

        if (!serverReady) {
            throw new RuntimeException("Server did not start within the expected time");
        }
    }

    @BeforeEach
    public void setUp() {
        // Configure Selenide
        Configuration.browser = "firefox";
        Configuration.baseUrl = BASE_URL;
        Configuration.timeout = 10000; // 10 seconds
        Configuration.browserSize = "1400x900";

        // Screenshot settings
        Configuration.screenshots = true;
        Configuration.savePageSource = false;
        Configuration.reportsFolder = "target/screenshots";
    }

    @AfterEach
    public void tearDown() {
        // Close browser
        closeWebDriver();
    }

    @Test
    public void testDealListingWithScreenshots() throws IOException {
        // Open deals page
        open("/jsp/deals");
        screenshot("01-deals-page-loaded");

        // Click Clear link
        $("a[href*='/jsp/deals']").shouldBe(visible).click();
        screenshot("02-after-clear-click");

        // Click on deal card #3 deal ID
        $(".deal-card:nth-child(3) .deal-id").shouldBe(visible).click();
        screenshot("03-deal-card-3-clicked");

        // Click on deal card #3 info value
        $(".deal-card:nth-child(3) .info-item:nth-child(1) > .info-value")
                .shouldBe(visible)
                .click();
        screenshot("04-info-value-clicked");

        // Click on deal card #2
        $(".deal-card:nth-child(2)").shouldBe(visible).click();
        screenshot("05-deal-card-2-clicked");

        // Enter sales rep ID
        $("input[name='salesRepId']")
                .shouldBe(visible)
                .click();
        $("input[name='salesRepId']")
                .setValue("USER-2530b14b-6081-4861-8760-774c95e609d5");
        screenshot("06-sales-rep-id-entered");

        // Click Apply Filters button
        $(".btn-primary").shouldBe(visible).click();
        screenshot("07-filters-applied");

        // Wait a moment for results to load
        sleep(1000);
        screenshot("08-filtered-results");

        // Click Clear link again
        $("a[href*='/jsp/deals']").shouldBe(visible).click();
        screenshot("09-final-clear-clicked");
    }

    /**
     * Captures a screenshot with a custom name
     */
    private void screenshot(String name) {
        String screenshotPath = Selenide.screenshot(name);
        System.out.println("Screenshot saved: " + screenshotPath);
    }
}
