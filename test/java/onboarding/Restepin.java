package onboarding;

import com.jcraft.jsch.*;
import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.annotations.AfterMethod; // Import for @AfterMethod
import org.testng.annotations.Test;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Duration;
import java.util.HashMap;
import java.util.List; // Added for Files.readAllLines
import java.util.Map;
import java.util.Properties;
import java.util.regex.Matcher; // Added for regex
import java.util.regex.Pattern; // Added for regex

public class Restepin {
    WebDriver driver;

    // --- Remote Server Details (YOU MUST CONFIGURE THESE!) ---
    private static final String REMOTE_HOST = "172.30.90.131";
    private static final int REMOTE_PORT = 22;
    private static final String REMOTE_USERNAME = "ltfadmin.d2c";
    private static final String REMOTE_PASSWORD = "Ltfs@SEP2025D2C##";
    // Path to the log file ON THE REMOTE SERVER
    private static final String REMOTE_LOG_PATH = "/home/ltfadmin.d2c/Logs/planet-user/log4j.log";
    // Local path where the log file will be temporarily saved on the machine running this test
    private static final String LOCAL_TEMP_LOG_PATH = "D:\\Automation\\log4j_temp.log";

    // Regex pattern to find a 6-digit OTP.
    // This pattern looks for "OTP", optionally followed by a colon or space, then captures 6 digits.
    // Adjust this regex if your log format for OTP is different.
    private static final Pattern OTP_PATTERN = Pattern.compile(".*OTP.*:?\\s*(\\d{6}).*");

    @Test
    void setupAndValidateOTP() throws InterruptedException {
        // Setup WebDriverManager to automatically download and configure ChromeDriver
        WebDriverManager.chromedriver().setup();

        // Create ChromeOptions object and configure it BEFORE initializing the ChromeDriver
        ChromeOptions options = new ChromeOptions();

        // --- Method 1: Using preferences to explicitly block notifications (More robust) ---
        // Create a HashMap to store browser preferences
        Map<String, Object> prefs = new HashMap<>();
        // Set the preference for notifications: 2 means BLOCK
        prefs.put("profile.default_content_setting_values.notifications", 2);
        // Add the preferences to ChromeOptions
        options.setExperimentalOption("prefs", prefs);

        // --- Method 2: Command-line arguments (often works, good to keep as backup) ---
        options.addArguments("--disable-notifications");
        options.addArguments("--disable-popup-blocking");

        // --- Other useful options for automation (helps make browser cleaner) ---
        // This is where you put the maximize argument
        options.addArguments("--start-maximized");
        options.setExperimentalOption("excludeSwitches", new String[]{"enable-automation"});
        options.addArguments("--disable-infobars");
        options.addArguments("--disable-extensions");
      //  options.addArguments("--force-device-scale-factor=0.9");

        // Initialize the ChromeDriver, passing the fully configured ChromeOptions
        driver = new ChromeDriver(options);

        // Now that the driver is initialized, set implicit wait and WebDriverWait
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(20));

        // Navigate to URL
        driver.get("https://uatd2cwbs.ltfinance.com/");

        System.out.println("Chrome browser launched with notification handling preferences and maximized.");

        // Wait for a significant element on the page to indicate it's loaded
        WebElement mobileNumberInput = driver.findElement(By.xpath("//input[contains(@aria-label, \"+91\")]"));
        mobileNumberInput.click();
        mobileNumberInput.sendKeys("6362285653");


        WebElement continueButton = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//flt-semantics[@role='button' and text()='Continue']")));
        continueButton.click();
        Thread.sleep(4000);



        WebElement reset = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//a[text()='Reset PIN']")));
        reset.click();

        String extractedOtp = null;
        try {
            // IMPORTANT: Give the backend time to generate the OTP and write it to the log on the remote server
            // Adjust this sleep duration based on how long it takes for the OTP to appear in your logs.
            System.out.println("Waiting for OTP to be generated and logged on the remote server...");
            Thread.sleep(15000); // Increased sleep to 15 seconds, adjust as needed based on server response time

            // Download the log file from the remote server using SFTP
            downloadLogFileFromRemoteServer(REMOTE_HOST, REMOTE_PORT, REMOTE_USERNAME, REMOTE_PASSWORD, REMOTE_LOG_PATH, LOCAL_TEMP_LOG_PATH);
            System.out.println("Log file downloaded successfully to: " + LOCAL_TEMP_LOG_PATH);

            // Print statement to confirm the path being used for reading
            System.out.println("Attempting to read OTP from local file: " + LOCAL_TEMP_LOG_PATH);

            // Extract OTP from the locally downloaded log file
            extractedOtp = getOtpFromLocalLog(LOCAL_TEMP_LOG_PATH);

            if (extractedOtp != null) {
                System.out.println("Successfully extracted OTP: " + extractedOtp);
            } else {
                System.out.println("OTP not found in the downloaded log file. Check log format and timing.");
                throw new RuntimeException("OTP could not be extracted from log.");
            }

        } catch (Exception e) { // Catch broader Exception for JSch errors and other issues during log handling
            System.err.println("An error occurred during log download or OTP extraction: " + e.getMessage());
            e.printStackTrace(); // Print full stack trace for detailed debugging
            throw new RuntimeException("Failed to get OTP from remote log.", e);
        } finally {
            // Clean up the locally downloaded log file in a finally block to ensure it runs even if errors occur
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

        // --- Now, pass the extracted OTP to the OTP input field on the web page ---
        // Adjust this locator to accurately find your OTP input field.
        WebElement otpInputField = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//input[contains(@aria-label, \"-\")]")));
        otpInputField.click();
        otpInputField.sendKeys(extractedOtp);
        System.out.println("Entered OTP into the input field.");

        // Click the 'Verify OTP' or 'Submit' button after entering OTP
        // Adjust this locator to accurately find your OTP verification button.
        WebElement verifyButton = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//flt-semantics[@role='button' and text()='Verify']")));
        verifyButton.click();
        System.out.println("Clicked the Verify button.");

        // Add a small delay to observe the result after OTP verification
        Thread.sleep(3000);

        WebElement mpin1 = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//input[@type='password' and @data-semantics-role='text-field']")));
        mpin1.click();;
        mpin1.sendKeys("6789");

        WebElement mpin2 =  wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//input[@autocomplete='off' and @data-semantics-role='text-field']")));
        mpin2.click();
        mpin2.sendKeys("6789");

        driver.findElement(By.xpath("//flt-semantics[@role='button' and text()='Continue']")).click();


        Thread.sleep(10000);

    }

    /**
     * Helper method to read a LOCAL log file and extract the OTP.
     * This method assumes the OTP is on a new line and follows "OTP is : " or similar pattern.
     * It will return the LAST found OTP in the file, which is typically the most recent.
     *
     * @param localLogFilePath The full path to the locally downloaded log file.
     * @return The extracted OTP string, or null if not found.
     * @throws IOException If there's an issue reading the file.
     */
    String getOtpFromLocalLog(String localLogFilePath) throws IOException {
        String otp = null;
        // Regex pattern to find "OTP is : " followed by one or more digits.
        // The parentheses create a capturing group for the digits.
        Pattern pattern = Pattern.compile("OTP is :\\s*(\\d+)");

        // Use try-with-resources to ensure the BufferedReader is closed automatically
        try (BufferedReader reader = new BufferedReader(new FileReader(localLogFilePath))) {
            String line;
            String lastFoundOtp = null; // Variable to store the most recently found OTP
            while ((line = reader.readLine()) != null) {
                Matcher matcher = pattern.matcher(line);
                if (matcher.find()) {
                    // Group 1 contains the actual digits of the OTP captured by the regex
                    lastFoundOtp = matcher.group(1);
                }
            }
            otp = lastFoundOtp; // Return the last found OTP, assuming it's the most recent relevant one
        }
        return otp;
    }

    /**
     * Downloads a file from a remote server using SFTP (SSH File Transfer Protocol).
     * This method connects to the remote server using SSH credentials and downloads the specified file.
     *
     * @param host Remote server IP or hostname.
     * @param port SSH port (usually 22).
     * @param username SSH username.
     * @param password SSH password.
     * @param remoteFilePath Path to the file on the remote server.
     * @param localSavePath Local path to save the downloaded file.
     * @throws Exception If there's an error during SSH/SFTP connection or transfer.
     */
    void downloadLogFileFromRemoteServer(
            String host, int port, String username, String password, String remoteFilePath, String localSavePath) throws Exception {

        Session session = null;
        ChannelSftp channelSftp = null;
        try {
            JSch jsch = new JSch();

            // Establish SSH session
            session = jsch.getSession(username, host, port);
            session.setPassword(password);

            // Configure SSH session properties.
            // "StrictHostKeyChecking=no" is used here for convenience in test environments,
            // but for production, it's recommended to handle host keys more securely (e.g., by adding to known_hosts).
            Properties config = new Properties();
            config.put("StrictHostKeyChecking", "no");
            session.setConfig(config);

            session.connect(); // Connect to the SSH server
            System.out.println("SSH Session connected to: " + host + " on port " + port + " with user " + username);

            // Open an SFTP channel for file transfer
            channelSftp = (ChannelSftp) session.openChannel("sftp");
            channelSftp.connect(); // Connect the SFTP channel
            System.out.println("SFTP Channel connected.");

            // Download the file from the remote path to the local path
            channelSftp.get(remoteFilePath, localSavePath);
            System.out.println("File downloaded from " + remoteFilePath + " to " + localSavePath);

        } finally {
            // Ensure SSH and SFTP connections are closed in a finally block
            // to prevent resource leaks, even if errors occur.
            if (channelSftp != null && channelSftp.isConnected()) {
                channelSftp.disconnect();
                System.out.println("SFTP Channel disconnected.");
            }
            if (session != null && session.isConnected()) {
                session.disconnect();
                System.out.println("SSH Session disconnected.");
            }
        }
    }

    @Test
    void verifySuccessfulLogin() {
        // This is where you would add assertions to verify that the login was successful.
        // For example, check for elements visible only after a successful login.
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(15));
        try {
            // Example: Wait for a dashboard element or a user-specific element
            // Replace with an actual locator for an element that appears after successful login
            WebElement dashboardElement = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//flt-semantics[contains(text(), 'Dashboard') or contains(text(), 'Welcome')]")));
            Assert.assertTrue(dashboardElement.isDisplayed(), "Login verification failed: Dashboard element not found.");
            System.out.println("Login successful! Dashboard element found: " + dashboardElement.getText());
        } catch (Exception e) {
            System.err.println("Login verification failed: " + e.getMessage());
            Assert.fail("Login was not successful or verification element not found.");
        }
        System.out.println("Test 'verifySuccessfulLogin' completed.");
    }

    // You might want to add an @AfterClass method to close the browser after all tests in this class run
    // @AfterClass
    // void tearDown() {
    //     if (driver != null) {
    //         driver.quit();
    //         System.out.println("Browser closed.");
    //     }
    // }
}