package onboarding;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.annotations.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Driver;
import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

public class Lockcheck extends Restepin{

    protected WebDriver driver;
    private static final String REMOTE_HOST = "172.30.90.131";
    private static final int REMOTE_PORT = 22;
    private static final String REMOTE_USERNAME = "ltfadmin.d2c";
    private static final String REMOTE_PASSWORD = "Ltfs@AUG2025D2C##";
    // Path to the log file ON THE REMOTE SERVER
    private static final String REMOTE_LOG_PATH = "/home/ltfadmin.d2c/Logs/planet-user/log4j.log";
    // Local path where the log file will be temporarily saved on the machine running this test
    private static final String LOCAL_TEMP_LOG_PATH = "D:\\Automation\\log4j_temp.log";
    // Use 'protected' so subclasses can access it
    protected WebDriverWait wait;
    @Test
    void setup() throws InterruptedException {
        // Setup WebDriverManager to automatically download and configure ChromeDriver
        WebDriverManager.chromedriver().setup();

        // Create ChromeOptions object FIRST
        ChromeOptions options = new ChromeOptions();

        // --- Method 1: Using preferences to explicitly block notifications (More robust) ---
        Map<String, Object> prefs = new HashMap<>();
        prefs.put("profile.default_content_setting_values.notifications", 2);
        options.setExperimentalOption("prefs", prefs);

        // --- Method 2: Command-line arguments (often works, good to keep as backup) ---
        options.addArguments("--disable-notifications");
        options.addArguments("--disable-popup-blocking");

        // --- Other useful options for automation ---
        options.setExperimentalOption("excludeSwitches", new String[]{"enable-automation"});
        options.addArguments("--disable-infobars");
        options.addArguments("--disable-extensions");

        // --- This is the key for maximizing ---
        options.addArguments("--start-maximized"); // Add this argument to options
        // options.addArguments("--window-size=412,915");

        options.addArguments("--force-device-scale-factor=0.9");

        // Initialize the ChromeDriver, PASSING THE CONFIGURED OPTIONS HERE
        driver = new ChromeDriver(options); // <--- Corrected line: pass 'options' here

        // Now you can set implicit wait
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));

        // No need for driver.manage().window().maximize() if using --start-maximized in options

        // Navigate to URL
        driver.get("https://uatd2cwbs.ltfinance.com/");

        System.out.println("Chrome browser launched with notification handling preferences.");

        try {
            Thread.sleep(6000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        // Using WebDriverWait for better synchronization
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(20)); // Initialize here

        WebElement phoneNumberField = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//input[@data-semantics-role='text-field' and @type='tel']")));
        phoneNumberField.click();
        phoneNumberField.sendKeys("6362285653");
        Thread.sleep(2000);

        WebElement continueButton = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//flt-semantics[@role='button' and text()='Continue']")));
        continueButton.click();
        Thread.sleep(4000);

        WebElement passwordField = wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector("input[type=\"password\"]")));
        passwordField.click();

        // Wait for the one-time code field to be visible and interactable
        WebElement oneTimeCodeField = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//input[@name='one-time-code']")));
        oneTimeCodeField.sendKeys("5678");

        for (int i = 0; i < 3; i++) {
            try {
                // Wait for the password field to be clickable (if it's related to the flow)
                // This line seems extraneous if the one-time code is a separate field
                // If it's indeed part of the flow where the password field leads to the OTP, keep it.
                // Otherwise, you might remove it or adjust the selector to be more relevant.


                // You might need a small pause or another wait condition here
                // if there's an immediate UI update or redirection after entering the code.
                // For instance, if clicking a "Verify" button is needed after each entry:
                 WebElement verifyButton = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//input[@spellcheck='false' and @autocorrect='off']")));
                 verifyButton.click();

                verifyButton.sendKeys("5678");

                System.out.println("Attempt " + (i + 1) + ": Entered '1111' into one-time code field.");

                // If each entry leads to a new one-time code field or a reset,
                // you might need to re-locate the element in the next iteration.
                // If the field remains the same but its state changes, the above
                // element location might still be valid.

                // If there's a requirement for a delay between each attempt, use WebDriverWait
                // or a very short Thread.sleep, but favor explicit waits for conditions.
                // For example, if you need to wait for a success message or a new input to appear:
                // wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("nextInputOrSuccessMessage")));

            } catch (Exception e) {
                System.err.println("Error during attempt " + (i + 1) + ": " + e.getMessage());
                // Handle the exception, e.g., take a screenshot, log details, or break the loop
                break; // Exit the loop on error
            }
        }

       WebElement reset= wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//flt-semantics[text()='Reset PIN']")));
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
        mpin1.sendKeys("7777");

        WebElement mpin2 =  wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//input[@autocomplete='off' and @data-semantics-role='text-field']")));
        mpin2.click();
        mpin2.sendKeys("7777");

        driver.findElement(By.xpath("//flt-semantics[@role='button' and text()='Continue']")).click();


        Thread.sleep(30000);


        driver.findElement(By.xpath("//flt-semantics[@role='button' and @tabindex='0']")).click();


}}
