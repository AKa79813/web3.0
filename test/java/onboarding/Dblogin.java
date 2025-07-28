package onboarding;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.testng.Assert; // Import TestNG Assertions

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
    private static final String DB_URL = "jdbc:sqlserver://172.30.91.45:5007;databaseName=d2c_testing_read;encrypt=true;trustServerCertificate=true;";
    private static final String DB_USERNAME = "d2c_testing_read";
    private static final String DB_PASSWORD = "CC3#@aHxrscd";
    // --- Database Table and Column Names (UPDATE THESE based on your schema) ---
    private static final String TABLE_NAME = "DBO.D2C_USER_LOGIN_TRACKER"; // Example from your image
    private static final String PHONE_NUMBER_COLUMN = "PHONE_NO"; // Column storing phone number
    private static final String TIMESTAMP_COLUMN = "LOGIN_DATE"; // Column storing login timestamp

    @BeforeClass // This method will run once before any test methods in this class
    void setup() throws InterruptedException {
        // Setup WebDriverManager to automatically download and configure ChromeDriver
        WebDriverManager.chromedriver().setup();

        // Create ChromeOptions object
        ChromeOptions options = new ChromeOptions();

        // Configure options to disable notifications and maximize window
        Map<String, Object> prefs = new HashMap<>();
        prefs.put("profile.default_content_setting_values.notifications", 2);
        options.setExperimentalOption("prefs", prefs);
        options.addArguments("--disable-notifications");
        options.addArguments("--disable-popup-blocking");
        options.setExperimentalOption("excludeSwitches", new String[]{"enable-automation"});
        options.addArguments("--disable-infobars");
        options.addArguments("--disable-extensions");
        options.addArguments("--start-maximized"); // Maximize window on start

        // Initialize the ChromeDriver with configured options
        driver = new ChromeDriver(options);
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10)); // Implicit wait for elements

        // Navigate to URL
        driver.get("https://uatd2cwbs.ltfinance.com/planet-web/");
        System.out.println("Chrome browser launched and navigated to login page.");

        // Allow page to load
        Thread.sleep(6000); // Consider replacing with WebDriverWait for specific elements

        // Using WebDriverWait for better synchronization
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(20));

        // Enter phone number
        WebElement phoneNumberField = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//input[@data-semantics-role='text-field' and @type='tel']")));
        phoneNumberField.click();
        phoneNumberField.sendKeys(TEST_PHONE_NUMBER);
        Thread.sleep(2000);

        // Click Continue button
        WebElement continueButton = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//flt-semantics[@role='button' and text()='Continue']")));
        continueButton.click();
        Thread.sleep(4000);

        // Enter password/OTP
        WebElement passwordField = wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector("input[type=\"password\"]")));
        passwordField.click();
        Thread.sleep(1000);
        driver.findElement(By.xpath("//input[@name='one-time-code']")).sendKeys("1111");

        System.out.println("User logged in successfully via UI.");
    }

    @Test(priority = 1) // This test method will run after setup
    void validateDbEntryAfterLogin() {
        Connection connection = null;
        Statement statement = null;
        ResultSet resultSet = null;

        try {
            // Register JDBC driver (not strictly necessary for modern JDBC, but good practice)
            // Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver"); // Uncomment if you face issues

            System.out.println("Attempting to connect to database...");
            // Establish the connection
            connection = DriverManager.getConnection(DB_URL, DB_USERNAME, DB_PASSWORD);
            System.out.println("Database connection established successfully.");

            // Create a Statement object to execute the query
            statement = connection.createStatement();

            // Get current time minus a few minutes to check for recent entries
            // Adjust the 'minusMinutes' value based on how quickly the DB entry is expected
            LocalDateTime now = LocalDateTime.now();
            LocalDateTime timeThreshold = now.minusMinutes(5); // Check for entries within the last 5 minutes

            // Format the time for SQL query (adjust format if your DB expects a different one)
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            String formattedTimeThreshold = timeThreshold.format(formatter);

            // Construct the SQL query
            // This query checks for any entry for the test phone number
            // that was created/logged after the specified time threshold.
            String sqlQuery = String.format(
                    "SELECT * FROM %s WHERE %s = '%s' AND %s >= '%s' ORDER BY %s DESC;",
                    TABLE_NAME,
                    PHONE_NUMBER_COLUMN,
                    TEST_PHONE_NUMBER,
                    TIMESTAMP_COLUMN,
                    formattedTimeThreshold,
                    TIMESTAMP_COLUMN
            );

            System.out.println("Executing SQL Query: " + sqlQuery);

            // Execute the query
            resultSet = statement.executeQuery(sqlQuery);

            // Check if any row was returned
            if (resultSet.next()) {
                System.out.println("DB Validation: Found a recent entry for phone number " + TEST_PHONE_NUMBER);
                // You can retrieve and print more details if needed
                // String actualCreatedTs = resultSet.getString(TIMESTAMP_COLUMN);
                // System.out.println("Entry Timestamp: " + actualCreatedTs);

                // Assert that an entry was found
                Assert.assertTrue(true, "Database entry found for the logged-in user.");
            } else {
                System.out.println("DB Validation: No recent entry found for phone number " + TEST_PHONE_NUMBER);
                // Assert that no entry was found (if "no entry" is the expected outcome)
                Assert.fail("Database entry NOT found for the logged-in user within the last 5 minutes.");
            }

        } catch (SQLException e) {
            System.err.println("Database error occurred: " + e.getMessage());
            Assert.fail("Database connection or query failed: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("An unexpected error occurred during DB validation: " + e.getMessage());
            Assert.fail("An unexpected error occurred during DB validation: " + e.getMessage());
        } finally {
            // Close resources in reverse order of creation
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

//    @AfterClass // This method will run once after all test methods in this class
//    void tearDown() {
//        if (driver != null) {
//            driver.quit();
//            System.out.println("Browser closed.");
        }

