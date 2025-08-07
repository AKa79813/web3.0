package onboarding;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test; // Added @Test import
import org.testng.Assert; // Added Assert import for potential future assertions

import com.jcraft.jsch.ChannelSftp; // JSch import for SFTP
import com.jcraft.jsch.JSch;       // JSch import for SSH
import com.jcraft.jsch.Session;    // JSch import for SSH Session

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties; // Required for JSch session configuration
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class NewUser {

    WebDriver driver;
    // The phone number used for login. Make sure this is a valid test number.
    private final String TEST_PHONE_NUMBER = "8885849489";

    // --- Remote Server Details (UPDATE THESE FOR YOUR ENVIRONMENT!) ---
    private static final String REMOTE_HOST = "172.30.90.131";
    private static final int REMOTE_PORT = 22;
    private static final String REMOTE_USERNAME = "ltfadmin.d2c";
    private static final String REMOTE_PASSWORD = "Ltfs@AUG2025D2C##";
    // Path to the log file ON THE REMOTE SERVER
    private static final String REMOTE_LOG_PATH = "/home/ltfadmin.d2c/Logs/planet-user/log4j.log";
    // Local path where the log file will be temporarily saved on the machine running this test
    private static final String LOCAL_TEMP_LOG_PATH = "D:\\Automation\\log4j_temp.log"; // Corrected to be a file path

    @BeforeClass
        // This method will run once before any test methods in this class
    void setup() throws InterruptedException {
        // Setup WebDriverManager to automatically download and configure ChromeDriver
        WebDriverManager.chromedriver().setup();

        // Create ChromeOptions object to configure browser behavior
        ChromeOptions options = new ChromeOptions();

        // Configure options to disable notifications and maximize window for a cleaner test environment
        Map<String, Object> prefs = new HashMap<>();
        prefs.put("profile.default_content_setting_values.notifications", 2); // 2 means BLOCK notifications
        options.setExperimentalOption("prefs", prefs);
        options.addArguments("--disable-notifications");
        options.addArguments("--disable-popup-blocking");
        options.setExperimentalOption("excludeSwitches", new String[]{"enable-automation"}); // Hides "Chrome is being controlled by automated test software"
        options.addArguments("--disable-infobars"); // Disables info bars
        options.addArguments("--disable-extensions"); // Disables browser extensions
        options.addArguments("--start-maximized"); // Maximize window on start
        options.addArguments("--force-device-scale-factor=0.9");

        // Initialize the ChromeDriver with all the configured options
        driver = new ChromeDriver(options);
        // Set an implicit wait for elements to be present before throwing NoSuchElementException
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));

        // Navigate to the application URL
        driver.get("https://uatd2cwbs.ltfinance.com/planet-web/");
        System.out.println("Chrome browser launched and navigated to login page.");

        // Using WebDriverWait for better synchronization, waiting up to 20 seconds for elements
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(20));

        // Wait for and enter the phone number into the input field
        WebElement phoneNumberField = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//input[@data-semantics-role='text-field' and @type='tel']")));
        phoneNumberField.click();
        phoneNumberField.sendKeys(TEST_PHONE_NUMBER);
        System.out.println("Entered phone number: " + TEST_PHONE_NUMBER);
        Thread.sleep(2000); // Small pause for visual confirmation, consider replacing with more explicit waits

        // Wait for and click the "Continue" button
        WebElement continueButton = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//flt-semantics[@role='button' and text()='Continue']")));
        continueButton.click();
        System.out.println("Clicked 'Continue' button.");
        Thread.sleep(4000); // Small pause for visual confirmation, consider replacing with more explicit waits

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
        mpin1.sendKeys("1234");

        WebElement mpin2 =  wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//input[@autocomplete='off' and @data-semantics-role='text-field']")));
        mpin2.click();
        mpin2.sendKeys("1234");

        driver.findElement(By.xpath("//flt-semantics[@role='button' and text()='Continue']")).click();


        Thread.sleep(10000);

//endKeys("1234");
        //WebElement home = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//flt-semantics[@role='button' and text()='Home']\n)")));
       // System.out.println("new user loggedin successfully");



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
    private String getOtpFromLocalLog(String localLogFilePath) throws IOException {
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
    private void downloadLogFileFromRemoteServer(
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