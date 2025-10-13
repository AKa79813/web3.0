package payments;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.apache.commons.io.FileUtils;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.annotations.Test;

import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;

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
import java.util.Set; // Import Set for window handles
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.testng.Assert; // Import TestNG Assertions

public class QuickPay {

    WebDriver driver;

    // --- Remote Server Details (YOU MUST CONFIGURE THESE!) ---
    private static final String REMOTE_HOST = "172.30.90.131";
    private static final int REMOTE_PORT = 22;
    private static final String REMOTE_USERNAME = "ltfadmin.d2c";
    private static final String REMOTE_PASSWORD = "Ltfs@AUG2025D2C##";
    // Path to the log file ON THE REMOTE SERVER
    private static final String REMOTE_LOG_PATH = "/home/ltfadmin.d2c/Logs/planet-user/log4j.log";
    // Local path where the log file will be temporarily saved on the machine running this test
    private static final String LOCAL_TEMP_LOG_PATH = "D:\\Automation\\log4j_temp.log"; // Corrected to be a file path

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

       options.addArguments("--force-device-scale-factor=0.9");

        // Initialize the ChromeDriver, passing the fully configured ChromeOptions
        driver = new ChromeDriver(options);

        // Now that the driver is initialized, set implicit wait and WebDriverWait
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(40));

        // Navigate to URL
        driver.get("https://uatd2cwbs.ltfinance.com");

        System.out.println("Chrome browser launched with notification handling preferences and maximized.");

        // It's generally better to use explicit waits instead of Thread.sleep()
        // Wait for a significant element on the page to indicate it's loaded
        try {
            // Wait for the "Quick Pay" button to be visible and clickable, or another stable element
            WebElement quickpayButtonInitial = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//flt-semantics[contains(text(), \"Quick Pay\")]")));
            System.out.println("Page loaded successfully and Quick Pay button is visible.");
        } catch (Exception e) {
            System.err.println("Page did not load within expected time or Quick Pay button not found: " + e.getMessage());
            driver.quit(); // Quit driver if page fails to load
            throw new RuntimeException("Page load failed.", e);
        }

        // Click on Quick Pay
        WebElement quickpay = driver.findElement(By.xpath("//flt-semantics[contains(text(), \"Quick Pay\")]"));
        quickpay.click();

        // Enter the Loan Number
        WebElement loanNumberInput = driver.findElement(By.xpath("//input[@type='text']"));
        loanNumberInput.click();
        loanNumberInput.sendKeys("F0234K610802230656");

        // Enter the Mobile Number
        WebElement mobileNumberInput = driver.findElement(By.xpath("//input[contains(@aria-label, \"+91\")]"));
        mobileNumberInput.click();
        mobileNumberInput.sendKeys("6362285653");

        // Click the "Proceed" button
        WebElement proceedButton = driver.findElement(By.xpath("//flt-semantics[text()=\"Proceed\"]"));
        proceedButton.click();

        // --- OTP Extraction Logic ---
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

        // --- Now, pass the extracted OTP to the OTP input field on the web page ---
        // YOU MUST ADJUST THIS LOCATOR to find your OTP input field
        WebElement otpInputField = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//input[contains(@aria-label, \"-\")]")));
        otpInputField.click();
        otpInputField.sendKeys(extractedOtp);
        System.out.println("Entered OTP into the input field.");

        // Click the 'Verify OTP' or 'Submit' button
        // YOU MUST ADJUST THIS LOCATOR to find your OTP verification button
        WebElement verifyOtpButton = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//flt-semantics[@role=\"button\" and contains(text(), 'Continue')]")));
        verifyOtpButton.click();
        System.out.println("Clicked the Verify OTP button.");


        WebElement payEmifeild = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//input[@data-semantics-role='text-field']")));
        payEmifeild.click();
        payEmifeild.sendKeys("500");


         Thread.sleep(3000);
        WebElement paybutton = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//flt-semantics[text()='Pay ₹ 500']")));
        paybutton.click();;
        System.out.println("Clicked the 'Pay' button, expecting a new tab.");


        String originalWindowHandle = driver.getWindowHandle();
        // --- Handle Window Switching to PayU Tab ---
        // Wait for the new window/tab to appear
        wait.until(ExpectedConditions.numberOfWindowsToBe(2));

        // Get all current window handles
        Set<String> allWindowHandles = driver.getWindowHandles();
        String payuWindowHandle = null;

        // Iterate through the handles to find the new window handle (PayU tab)
        for (String handle : allWindowHandles) {
            if (!handle.equals(originalWindowHandle)) {
                payuWindowHandle = handle;
                break;
            }
        }

        if (payuWindowHandle != null) {
            driver.switchTo().window(payuWindowHandle);
            System.out.println("Switched to PayU tab. Current URL: " + driver.getCurrentUrl());

            // --- VALIDATE WEB ELEMENTS ON PAYU PAGE ---
            System.out.println("\n--- Starting PayU Page Validations ---");

            try {
                // 1. Validate PayU Page Title/URL (often a good first check)
                String payuPageTitle = driver.getTitle();
                System.out.println("PayU Page Title: " + payuPageTitle);

                Thread.sleep(2000);
                WebElement cardsOption = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//div[@class='l1Item__content']/p[text()='Cards (Credit/Debit)']")));
                Assert.assertTrue(cardsOption.isDisplayed(), "Cards (Credit/Debit) option not displayed.");
                System.out.println("Validation: 'Cards (Credit/Debit)' option is displayed.");

//

                driver.findElement(By.xpath("//div[@class='l1Item__content']/p[text()='Cards (Credit/Debit)']")).click();;

                driver.findElement(By.xpath("//input[@data-testid='cardNumber']")).click();
                driver.findElement(By.xpath("//input[@data-testid='cardNumber']")).sendKeys("5123456789012346");


                WebElement cardexpiry = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//input[@id='cardExpiry']")));

                cardexpiry.click();
                cardexpiry.sendKeys("1225");
                driver.findElement(By.xpath("//input[@id='cardCvv']")).sendKeys("123");
                driver.findElement(By.xpath("//input[@name='cardOwnerName']")).sendKeys("Arun kumar");
                Thread.sleep(1000);

                driver.findElement(By.xpath(" //span[contains(text(), 'PROCEED')]")).click();
                Thread.sleep(1000);
                driver.findElement(By.xpath(" //button[text()='Save and Continue']")).click();;


                options.addArguments("--disable-notifications");
                options.addArguments("--disable-infobars");
                options.addArguments("--disable-extensions");

                WebElement code = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//*[@id='password']")));
                code.sendKeys("123456");
                options.addArguments("--disable-notifications");

                driver.findElement(By.xpath("//input[@id='submitBtn']")).click();


            } catch (Exception e) {
                System.err.println("Error during PayU page validation: " + e.getMessage());
                e.printStackTrace();
                Assert.fail("PayU page validation failed: " + e.getMessage()); // Fail the test explicitly
            }


            Thread.sleep(3000);

            // Close the current PayU tab
            driver.close();
            System.out.println("Closed PayU tab.");

            // Return to the original tab
            driver.switchTo().window(originalWindowHandle);

            Thread.sleep(7000);
            takeScreenshot(driver, "Quickpaysuccesspage.png");
            WebElement login = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//flt-semantics[@role='button' and text()='Go to Login']")));

           // scrollAndClickElement(By.xpath("//flt-semantics[@role='button' and text()='Go to Login']"), "Go to Login Button");

           // System.out.println("Test completed and browser closed.");
           // System.out.println("--------------Quick pay payment success-------------");



        } else {
            System.err.println("Could not find the new PayU window handle. Test Failed.");
            Assert.fail("Failed to switch to PayU window.");
        }



        try {
            // Example: Wait for a success message or new page element after OTP validation
            // Replace By.id("paymentSuccessMessage") with the actual locator for your success indicator
            // WebElement successIndicator = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//div[contains(text(),'Payment Successful') or contains(text(),'OTP Verified')]")));
            //System.out.println("OTP validated successfully. Success message: " + successIndicator.getText());
        } catch (Exception e) {
            System.err.println("OTP validation might have failed or success message not found within timeout: " + e.getMessage());
            // Optionally, take a screenshot or fail the test here
            throw new RuntimeException("OTP validation failed or success not confirmed.", e);
        }

        // Keep the browser open for a few seconds for visual inspection if needed
        Thread.sleep(3000);

      //  driver.quit();

    }

    private void scrollAndClickElement(By xpath, String s) {
    }

    private void takeScreenshot(WebDriver driver, String Quickpaysucesspage) {

        try {
            // Ensure the driver supports taking screenshots
            if (driver instanceof TakesScreenshot) {
                // Cast the driver to TakesScreenshot and get the screenshot as a File
                File screenshotFile = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);

                // Define the directory where screenshots will be saved
                File screenshotDir = new File("./screenshots");
                if (!screenshotDir.exists()) {
                    screenshotDir.mkdirs(); // Create the directory if it doesn't exist
                }

                // Define the destination file path
                File destinationFile = new File(screenshotDir, Quickpaysucesspage);

                // Copy the screenshot file to the destination
                FileUtils.copyFile(screenshotFile, destinationFile);
                System.out.println("Screenshot saved to: " + destinationFile.getAbsolutePath());
            } else {
                System.err.println("WebDriver does not support taking screenshots.");
            }
        } catch (IOException e) {
            System.err.println("Failed to take or save screenshot '" + Quickpaysucesspage + "': " + e.getMessage());
            e.printStackTrace();
        }



    }

    /**
     * Helper method to read a LOCAL log file and extract the OTP.
     * This method assumes the OTP is on a new line and follows "OTP is : " or similar pattern.
     *
     * @param localLogFilePath The full path to the locally downloaded log file.
     * @return The extracted OTP string, or null if not found.
     * @throws IOException If there's an issue reading the file.
     */
    private String getOtpFromLocalLog(String localLogFilePath) throws IOException {
        String otp = null;
        // --- UPDATED REGEX: Matches "OTP is : " followed by digits ---
        Pattern pattern = Pattern.compile("OTP is :\\s*(\\d+)"); // Matches "OTP is : " followed by one or more digits

        // Read the file from the end, or iterate through recent entries if possible
        // For simplicity, this example reads the entire file.
        // For very large logs, consider reading only the last few lines or implementing a more efficient search.
        try (BufferedReader reader = new BufferedReader(new FileReader(localLogFilePath))) {
            String line;
            String lastFoundOtp = null; // Store the most recently found OTP
            while ((line = reader.readLine()) != null) {
                Matcher matcher = pattern.matcher(line);
                if (matcher.find()) {
                    // Group 1 contains the actual digits of the OTP
                    lastFoundOtp = matcher.group(1);
                    // If you only care about the *last* OTP in the file, continue reading.
                    // If you want the *first* one, you can 'break;' here.
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

            // Optional: If you use an SSH private key file instead of a password:
            // jsch.addIdentity("/path/to/your/private_key_file");
            // session = jsch.getSession(username, host, port);
            // (then remove session.setPassword(password);)


            session = jsch.getSession(username, host, port);
            session.setPassword(password);

            // IMPORTANT: For initial testing, you might need to set StrictHostKeyChecking to "no"
            // to automatically add the host key if it's new.
            // For production environments, it's better to explicitly add the host key to known_hosts
            // on the machine running the test for security.
            Properties config = new Properties();
            config.put("StrictHostKeyChecking", "no"); // Be cautious with this in production
            session.setConfig(config);

            session.connect();
            System.out.println("SSH Session connected to: " + host + " on port " + port + " with user " + username);

            channelSftp = (ChannelSftp) session.openChannel("sftp");
            channelSftp.connect();
            System.out.println("SFTP Channel connected.");

            // Download the file
            channelSftp.get(remoteFilePath, localSavePath);
            System.out.println("File downloaded from " + remoteFilePath + " to " + localSavePath);

        } finally {
            // Ensure SSH and SFTP connections are closed even if an error occurs
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

}