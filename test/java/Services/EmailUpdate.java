package Services;

import com.jcraft.jsch.*; // Import JSch classes
import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.annotations.AfterMethod; // Added for cleanup
import org.testng.annotations.BeforeMethod; // Added for setup
import org.testng.annotations.Test;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class EmailUpdate {

    private static final String REMOTE_HOST = "172.30.90.131";
    private static final int REMOTE_PORT = 22;
    private static final String REMOTE_USERNAME = "ltfadmin.d2c";
    private static final String REMOTE_PASSWORD = "Ltfs#D2C@2025##";
    // Path to the log file ON THE REMOTE SERVER
    private static final String REMOTE_LOG_PATH = "/home/ltfadmin.d2c/Logs/planet-user/log4j.log";
    // Local path where the log file will be temporarily saved on the machine running this test
    private static final String LOCAL_TEMP_LOG_PATH = "D:\\Automation\\log4j_temp.log"; // Corrected to be a file path

    WebDriver driver;
    WebDriverWait wait; // Made WebDriverWait a class member

    @BeforeMethod // Use @BeforeMethod for setup that runs before each test
    void setup() {
        // Setup WebDriverManager to automatically download and configure ChromeDriver
        WebDriverManager.chromedriver().setup();

        // Create ChromeOptions object
        ChromeOptions options = new ChromeOptions();

        // Method 1: Using preferences to explicitly block notifications (More robust)
        Map<String, Object> prefs = new HashMap<>();
        prefs.put("profile.default_content_setting_values.notifications", 2);
        options.setExperimentalOption("prefs", prefs);

        // Method 2: Command-line arguments (often works, good to keep as backup)
        options.addArguments("--disable-notifications");
        options.addArguments("--disable-popup-blocking");

        // Other useful options for automation
        options.setExperimentalOption("excludeSwitches", new String[]{"enable-automation"});
        options.addArguments("--disable-infobars");
        options.addArguments("--disable-extensions");
        options.addArguments("--start-maximized"); // Add this argument to options

        // Initialize the ChromeDriver, PASSING THE CONFIGURED OPTIONS HERE
        driver = new ChromeDriver(options);

        // Initialize WebDriverWait with a reasonable timeout
        wait = new WebDriverWait(driver, Duration.ofSeconds(30)); // Increased wait time for robustness

        // Set implicit wait (though explicit waits are generally preferred)
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));

        System.out.println("Chrome browser launched with notification handling preferences.");
    }

    @Test
    void updateEmailTest() throws InterruptedException { // Renamed test method for clarity
        // Navigate to URL
        driver.get("https://uatd2cwbs.ltfinance.com/planet-web/");
        System.out.println("Navigated to URL: " + driver.getCurrentUrl());

        // Wait for page to load or specific element to be present
        // TODO: Replace Thread.sleep with WebDriverWait for better synchronization
        Thread.sleep(6000); // Consider replacing this with a wait for a specific element on the landing page

        // Interact with Phone Number Field
        WebElement phoneNumberField = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//input[@data-semantics-role='text-field' and @type='tel']")));
        phoneNumberField.click();
        phoneNumberField.sendKeys("9888484848");
        // TODO: Replace Thread.sleep with WebDriverWait for better synchronization
        Thread.sleep(2000);

        // Click Continue Button
        WebElement continueButton = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//flt-semantics[@role='button' and text()='Continue']")));
        continueButton.click();
        // TODO: Replace Thread.sleep with WebDriverWait for better synchronization
        Thread.sleep(2000);

        // Interact with Password/OTP Field
        WebElement passwordField = wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector("input[type=\"password\"]")));
        passwordField.click();
        // TODO: Replace Thread.sleep with WebDriverWait for better synchronization
        Thread.sleep(1000);
        driver.findElement(By.xpath("//input[@name='one-time-code']")).sendKeys("1111");

        System.out.println("User logged in successfully.");

        // Click Services
        WebElement services = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//flt-semantics[text()='Services']")));
        services.click();

        // Click Email ID


        WebElement emailid = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//flt-semantics[text()='Email ID']")));
        emailid.click();

        // Click Pencil icon (assuming this is the pencil icon for editing)
        // Note: The previous XPath "//flt-semantics[@role='button']" is very generic.
        // If there are multiple buttons, this might click the wrong one.
        // It's better to use a more specific locator if possible (e.g., based on position, parent, or a unique attribute).

        Thread.sleep(2000);
        WebElement pencil = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//flt-semantics[@role=\"button\"]")));
        pencil.click();

        // Click Continue (after editing, presumably)
        Thread.sleep(4000);
        wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//flt-semantics[text()='Continue']"))).click();

        // Enter new Email Address

        Thread.sleep(2000);
        WebElement emailAddressInput = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//input[@aria-label='Enter email address']")));
        emailAddressInput.sendKeys("mkmk@gmail.com");

        // Click Date of Birth field to open calendar/picker
        WebElement dobField = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//input[@aria-label='Date of birth']")));
        dobField.click();

        // Click "Select year"
        WebElement selectYearButton = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//flt-semantics[contains(text(), 'Select year')]")));
        selectYearButton.click();

        // Scroll to and click on "1900"
        scrollAndClickElement(By.xpath("//flt-semantics[text()='1900']"));

        for (int i = 0; i < 4; i++) {
            try {
                WebElement previousMonthButton = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//flt-semantics[text()='Previous month']")));
                previousMonthButton.click();
                System.out.println("Clicked 'Previous month' (Attempt " + (i + 1) + "/4)");
                // Add a small delay if the UI needs time to update after each click
                Thread.sleep(500); // Adjust delay as needed, or use a more specific wait
            } catch (Exception e) {
                System.err.println("Failed to click 'Previous month' on attempt " + (i + 1) + ": " + e.getMessage());
                // Handle the exception, e.g., break the loop, take a screenshot, log more details
                break; // Stop if the element becomes unclickable or disappears
            }
        }
        System.out.println("'Previous month' clicks completed.");

        wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//flt-semantics[contains(text(), '27, April 27, 1900')]"))).click();
        wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//flt-semantics[text()='OK']"))).click();
        wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//flt-semantics[@role='button' and text()='Continue']"))).click();


        String extractedOtp = null;
        try {
            // IMPORTANT: Give the backend time to generate the OTP and write it to the log on the remote server
            // Adjust this sleep duration based on how long it takes for the OTP to appear in your logs.
            System.out.println("Waiting for OTP to be generated and logged on the remote server...");
            Thread.sleep(15000); // Increased sleep to 15 seconds, adjust as needed

            // Download the log file from the remote server
            downloadLogFileFromRemoteServer(REMOTE_HOST, REMOTE_PORT, REMOTE_USERNAME, REMOTE_PASSWORD, REMOTE_LOG_PATH, LOCAL_TEMP_LOG_PATH);
            System.out.println("Log file downloaded successfully to: " + LOCAL_TEMP_LOG_PATH);

            // Added print statement to confirm the path being used for reading
            System.out.println("Attempting to read OTP from local file: " + LOCAL_TEMP_LOG_PATH);

            // Extract OTP from the locally downloaded log file
            extractedOtp = getOtpFromLocalLog(LOCAL_TEMP_LOG_PATH);

            if (extractedOtp != null) {
                System.out.println("Successfully extracted OTP: " + extractedOtp);
                // TODO: Here you would enter the extractedOtp into the OTP field on the web page.
                // Example: driver.findElement(By.xpath("//input[@name='otpField']")).sendKeys(extractedOtp);
            } else {
                System.out.println("OTP not found in the downloaded log file. Check log format and timing.");
                throw new RuntimeException("OTP could not be extracted from log.");
            }

        } catch (Exception e) { // Catch broader Exception for JSch errors and other issues
            System.err.println("An error occurred during log download or OTP extraction: " + e.getMessage());
            e.printStackTrace(); // Print full stack trace for debugging
            throw new RuntimeException("Failed to get OTP from remote log.", e);
        } finally {
            // Clean up the locally downloaded log file in a finally block to ensure it runs
            try {
                Path localLogPath = Paths.get(LOCAL_TEMP_LOG_PATH);
                if (Files.exists(localLogPath)) {
                    Files.delete(localLogPath);
                    System.out.println("Cleaned up local temp log file: " + LOCAL_TEMP_LOG_PATH);
                }
            } catch (IOException e) {
                System.err.println("Failed to delete local temp log file: " + e.getMessage());
            }
        }


        driver.findElement(By.xpath("//flt-semantics[@flt-tappable and text()='Continue']")).click();
    }


    /**
     * Scrolls the given element into view and then clicks it.
     * This method uses JavaScript for scrolling and WebDriverWait for element interaction.
     *
     * @param by The By locator strategy for the element to be scrolled and clicked.
     */
    private void scrollAndClickElement(By by) {
        try {
            // Wait until the element is present in the DOM
            WebElement element = wait.until(ExpectedConditions.presenceOfElementLocated(by));

            // Scroll the element into view using JavaScript
            // This ensures the element is visible in the viewport before attempting to click
            ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", element);
            System.out.println("Scrolled element located by '" + by.toString() + "' into view.");

            // Wait until the element is clickable after scrolling
            // This is crucial as scrolling might not immediately make it interactive
            wait.until(ExpectedConditions.elementToBeClickable(by)).click();
            System.out.println("Successfully clicked on element located by: " + by.toString());

        } catch (Exception e) {
            System.err.println("Failed to scroll to and click element located by '" + by.toString() + "': " + e.getMessage());
            throw e; // Re-throw the exception to indicate failure in the test
        }
    }

    /**
     * Downloads a file from a remote server using SFTP (SSH).
     *
     * @param host The remote host IP address or hostname.
     * @param port The SSH port, usually 22.
     * @param username The username for SSH connection.
     * @param password The password for SSH connection.
     * @param remoteFilePath The full path to the file on the remote server.
     * @param localFilePath The local path where the file will be saved.
     * @throws JSchException If there's an SSH connection error.
     * @throws SftpException If there's an SFTP transfer error.
     */
    private void downloadLogFileFromRemoteServer(String host, int port, String username, String password, String remoteFilePath, String localFilePath) throws JSchException, SftpException {
        JSch jsch = new JSch();
        Session session = null;
        ChannelSftp channelSftp = null;

        try {
            session = jsch.getSession(username, host, port);
            session.setPassword(password);

            // Avoid asking for key confirmation
            java.util.Properties config = new java.util.Properties();
            config.put("StrictHostKeyChecking", "no");
            session.setConfig(config);

            System.out.println("Connecting to SSH server...");
            session.connect();
            System.out.println("SSH Session connected.");

            Channel channel = session.openChannel("sftp");
            channel.connect();
            channelSftp = (ChannelSftp) channel;

            System.out.println("Downloading file from " + remoteFilePath + " to " + localFilePath);
            channelSftp.get(remoteFilePath, localFilePath);
            System.out.println("File downloaded successfully.");

        } finally {
            if (channelSftp != null) {
                channelSftp.exit();
                System.out.println("SFTP Channel disconnected.");
            }
            if (session != null) {
                session.disconnect();
                System.out.println("SSH Session disconnected.");
            }
        }
    }

    /**
     * Extracts an OTP (6 digits) from a local log file.
     * This method assumes the OTP is a 6-digit number and appears after a specific pattern,
     * or is the first 6-digit number found that looks like an OTP.
     * You might need to adjust the regex based on your actual log format.
     *
     * @param logFilePath The path to the local log file.
     * @return The extracted OTP as a String, or null if not found.
     * @throws IOException If there's an error reading the file.
     */
    private String getOtpFromLocalLog(String logFilePath) throws IOException {
        String otp = null;
        // Example regex: Looks for "OTP: 123456" or "your OTP is 123456"
        // Adjust this regex according to the actual format of your OTP in the logs.
        // This example assumes a 6-digit number.
        Pattern pattern = Pattern.compile("(?<!\\d)\\b(\\d{6})\\b(?!\\d)"); // Matches a 6-digit number not part of a larger number

        try (BufferedReader reader = new BufferedReader(new FileReader(logFilePath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                // You can add more specific filtering if your log is very noisy
                // e.g., if (line.contains("OTP") || line.contains("code")) { ... }

                Matcher matcher = pattern.matcher(line);
                if (matcher.find()) {
                    otp = matcher.group(1); // Group 1 contains the 6-digit OTP
                    // If multiple OTPs can be in the log, you might need more logic
                    // (e.g., get the last one, or one related to a timestamp)
                    break; // Stop after finding the first OTP
                }
            }
        }
        return otp;
    }

    @AfterMethod // Use @AfterMethod for cleanup that runs after each test
    void tearDown() {
        if (driver != null) {
            driver.quit();
            System.out.println("Browser closed.");
        }
    }
}