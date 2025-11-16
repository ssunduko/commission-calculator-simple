package com.chapman.edu.commissions.functional;

import com.chapman.edu.commissions.app.DealManagementApp;
import org.junit.jupiter.api.*;
import org.openqa.selenium.By;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.net.HttpURLConnection;
import java.net.URL;
import java.time.Duration;

public class DealListingTest {

    private WebDriver driver;
    private WebDriverWait wait;
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
        // For a cleaner shutdown, you could implement a shutdown hook in DealManagementApp
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
        driver = new FirefoxDriver();
        wait = new WebDriverWait(driver, Duration.ofSeconds(10));

        // Authenticate before running tests (if required)
        // Note: JSP pages are public according to AuthenticationFilter
        // but API calls may require authentication
    }

    @AfterEach
    public void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }

    @Test
    public void testDealListing() {
        // Open deals page
        driver.get(BASE_URL + "/jsp/deals");

        // Set window size
        driver.manage().window().setSize(new Dimension(1400, 900));

        // Click Clear link
        WebElement clearLink = wait.until(
                ExpectedConditions.elementToBeClickable(By.linkText("Clear"))
        );
        clearLink.click();

        // Click on deal card #3 deal ID
        WebElement dealCard3Id = wait.until(
                ExpectedConditions.elementToBeClickable(
                        By.cssSelector(".deal-card:nth-child(3) .deal-id")
                )
        );
        dealCard3Id.click();

        // Click on deal card #3 info value
        WebElement infoValue = wait.until(
                ExpectedConditions.elementToBeClickable(
                        By.cssSelector(".deal-card:nth-child(3) .info-item:nth-child(1) > .info-value")
                )
        );
        infoValue.click();

        // Click on deal card #2
        WebElement dealCard2 = wait.until(
                ExpectedConditions.elementToBeClickable(
                        By.cssSelector(".deal-card:nth-child(2)")
                )
        );
        dealCard2.click();

        // Click on sales rep ID input
        WebElement salesRepInput = wait.until(
                ExpectedConditions.elementToBeClickable(By.name("salesRepId"))
        );
        salesRepInput.click();

        // Type sales rep ID
        salesRepInput.sendKeys("USER-2530b14b-6081-4861-8760-774c95e609d5");

        // Click Apply Filters button
        WebElement applyButton = wait.until(
                ExpectedConditions.elementToBeClickable(
                        By.cssSelector(".btn-primary")
                )
        );
        applyButton.click();

        // Click Clear link again
        WebElement clearLink2 = wait.until(
                ExpectedConditions.elementToBeClickable(By.linkText("Clear"))
        );
        clearLink2.click();
    }
}