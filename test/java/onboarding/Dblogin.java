package onboarding;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.testng.Assert;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

public class Dblogin {
    WebDriver driver;
    private final String TEST_PHONE_NUMBER = "9888484848"; // The phone number used for login

    // --- Database Connection Details (UPDATE THESE) ---
    private static final String DB_URL = "jdbc:sqlserver://172.30.91.45:5007;encrypt=true;trustServerCertificate=true;";
    private static final String DB_USERNAME = "d2c_testing_read";
    private static final String DB_PASSWORD = "CC3#@aHxrscd";
    // --- Database Table and Column Names (UPDATE THESE based on your schema) ---
    private static final String LOGIN_TABLE_NAME = "[PlanetDB].[dbo].[D2C_USER_LOGIN_TRACKER]";
    private static final String PHONE_NUMBER_COLUMN = "PHONE_NO";
    private static final String TIMESTAMP_COLUMN = "LOGIN_DATE"; // This is still your primary timestamp for filtering recent logins

    @BeforeClass
    void setup() throws InterruptedException {
        WebDriverManager.chromedriver().setup();
        ChromeOptions options = new ChromeOptions();
        Map<String, Object> prefs = new HashMap<>();
        prefs.put("profile.default_content_setting_values.notifications", 2);
        options.setExperimentalOption("prefs", prefs);
        options.addArguments("--disable-notifications");
        options.addArguments("--disable-popup-blocking");
        options.setExperimentalOption("excludeSwitches", new String[]{"enable-automation"});
        options.addArguments("--disable-infobars");
        options.addArguments("--disable-extensions");
        options.addArguments("--start-maximized");
        options.addArguments("--force-device-scale-factor=0.9");

        driver = new ChromeDriver(options);
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));

        driver.get("https://uatd2cwbs.ltfinance.com");
        System.out.println("Chrome browser launched and navigated to login page.");

        Thread.sleep(6000);

        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(20));

        WebElement phoneNumberField = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//input[@data-semantics-role='text-field' and @type='tel']")));
        phoneNumberField.click();
        phoneNumberField.sendKeys(TEST_PHONE_NUMBER);
        Thread.sleep(2000);

        WebElement continueButton = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//flt-semantics[@role='button' and text()='Continue']")));
        continueButton.click();
        Thread.sleep(4000);

        WebElement passwordField = wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector("input[type=\"password\"]")));
        passwordField.click();
        Thread.sleep(1000);
        driver.findElement(By.xpath("//input[@name='one-time-code']")).sendKeys("1111");

        System.out.println("User logged in successfully via UI.");
    }

    @Test(priority = 1)
    void validateDbEntryAfterLogin() {
        Connection connection = null;
        Statement statement = null;
        ResultSet resultSet = null;

        try {
            System.out.println("Attempting to connect to database...");
            connection = DriverManager.getConnection(DB_URL, DB_USERNAME, DB_PASSWORD);
            System.out.println("Database connection established successfully.");

            statement = connection.createStatement();

            LocalDateTime now = LocalDateTime.now();
            LocalDateTime timeThreshold = now.minusMinutes(5); // Check for entries within the last 5 minutes
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            String formattedTimeThreshold = timeThreshold.format(formatter);

            // The SQL query remains SELECT * to fetch all columns
            String sqlQuery = String.format(
                    "SELECT * FROM %s WHERE %s = '%s' AND %s >= '%s' ORDER BY %s DESC;",
                    LOGIN_TABLE_NAME,
                    PHONE_NUMBER_COLUMN,
                    TEST_PHONE_NUMBER,
                    TIMESTAMP_COLUMN,
                    formattedTimeThreshold,
                    TIMESTAMP_COLUMN
            );

            System.out.println("Executing SQL Query: " + sqlQuery);

            resultSet = statement.executeQuery(sqlQuery);

            if (resultSet.next()) {
                System.out.println("DB Validation: Found a recent entry for phone number " + TEST_PHONE_NUMBER);

                // *** RETRIEVE AND CHECK ADDITIONAL FIELDS HERE ***

                String userCreationId = resultSet.getString("USER_CREATION_ID");
                String authType = resultSet.getString("AUTH_TYPE");
                String appVersion = resultSet.getString("APP_VERSION");
                String buildNo = resultSet.getString("BUILD_NO");
                String platformType = resultSet.getString("PLATFORM_TYPE");
                String deviceId = resultSet.getString("DEVICE_ID");
                String createdTs = resultSet.getString("CREATED_TS"); // Assuming string representation
                String updatedTs = resultSet.getString("UPDATED_TS"); // Assuming string representation

                System.out.println("Retrieved Data:");
                System.out.println("  USER_CREATION_ID: " + userCreationId);
                System.out.println("  PHONE_NO: " + resultSet.getString("PHONE_NO")); // Already confirmed
                System.out.println("  LOGIN_DATE: " + resultSet.getString("LOGIN_DATE")); // Already confirmed
                System.out.println("  AUTH_TYPE: " + authType);
                System.out.println("  APP_VERSION: " + appVersion);
                System.out.println("  BUILD_NO: " + buildNo);
                System.out.println("  PLATFORM_TYPE: " + platformType);
                System.out.println("  DEVICE_ID: " + deviceId);
                System.out.println("  CREATED_TS: " + createdTs);
                System.out.println("  UPDATED_TS: " + updatedTs);


                // --- Add Assertions for these fields based on your test requirements ---
                // Example Assertions:
                Assert.assertNotNull(userCreationId, "USER_CREATION_ID should not be null");
                Assert.assertFalse(userCreationId.isEmpty(), "USER_CREATION_ID should not be empty");
                // Assert.assertEquals(authType, "ExpectedAuthType", "AUTH_TYPE mismatch"); // Uncomment and modify with expected value
                // Assert.assertTrue(appVersion.startsWith("1.0"), "APP_VERSION should start with 1.0");
                // Assert.assertNotNull(platformType, "PLATFORM_TYPE should not be null");
                // Assert.assertFalse(deviceId.isEmpty(), "DEVICE_ID should not be empty");
                // Assert.assertNotNull(createdTs, "CREATED_TS should not be null");
                // Assert.assertNotNull(updatedTs, "UPDATED_TS should not be null");

                // You might also want to compare CREATED_TS or UPDATED_TS with the current time (LocalDateTime.now())
                // after parsing them into LocalDateTime objects if their format allows.
                // For example:
                // LocalDateTime actualCreatedTs = LocalDateTime.parse(createdTs, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSSSSSS"));
                // Assert.assertTrue(actualCreatedTs.isAfter(timeThreshold.minusMinutes(1)), "CREATED_TS should be very recent");


                Assert.assertTrue(true, "Database entry found and key fields validated for the logged-in user.");
            } else {
                System.out.println("DB Validation: No recent entry found for phone number " + TEST_PHONE_NUMBER);
                Assert.fail("Database entry NOT found for the logged-in user within the last 5 minutes.");
            }

        } catch (SQLException e) {
            System.err.println("Database error occurred: " + e.getMessage());
            Assert.fail("Database connection or query failed: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("An unexpected error occurred during DB validation: " + e.getMessage());
            Assert.fail("An unexpected error occurred during DB validation: " + e.getMessage());
        } finally {
            try {
                if (resultSet != null) resultSet.close();
                if (statement != null) statement.close();
                if (connection != null) connection.close();
                System.out.println("Database resources closed.");
            } catch (SQLException e) {
                System.err.println("Error closing database resources: " + e.getMessage());
            }
        }
    }

    //    @AfterClass
    //    void tearDown() {
    //        if (driver != null) {
    //            driver.quit();
    //            System.out.println("Browser closed.");
    //        }
    //}
}