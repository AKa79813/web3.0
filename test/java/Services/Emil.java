package Services;

import com.jcraft.jsch.*; // Import JSch classes
import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.NoSuchElementException; // Import for catching NoSuchElementException
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
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

public class Emil {

    private static final String REMOTE_HOST = "172.30.90.131";
    private static final int REMOTE_PORT = 22;
    private static final String REMOTE_USERNAME = "ltfadmin.d2c";
    private static final String REMOTE_PASSWORD = "Ltfs@AUG2025D2C##";
    // Path to the log file ON THE REMOTE SERVER
    private static final String REMOTE_LOG_PATH = "/home/ltfadmin.d2c/Logs/planet-user/log4j.log";
    // Local path where the log file will be temporarily saved on the machine running this test
    private static final String LOCAL_TEMP_LOG_PATH = "D:\\Automation\\log4j_temp.log";

    WebDriver driver;
    WebDriverWait wait;
    JavascriptExecutor js; // Declare JavascriptExecutor as a class member

    @BeforeMethod
    void setup() {
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
        options.addArguments("--force-device-scale-factor=0.9"); // Be cautious with this, it can affect element locations

        driver = new ChromeDriver(options);
        wait = new WebDriverWait(driver, Duration.ofSeconds(30));
        js = (JavascriptExecutor) driver; // Initialize JavascriptExecutor here

        // Set implicit wait (though explicit waits are generally preferred)
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));

        System.out.println("Chrome browser launched with notification handling preferences.");
    }

    @Test
    void updateEmailTest() throws InterruptedException {
        // Navigate to URL
        driver.get("https://uatd2cwbs.ltfinance.com/planet-web/");
        System.out.println("Navigated to URL: " + driver.getCurrentUrl());

        // Wait for page to load or specific element to be present
        wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//input[@data-semantics-role='text-field' and @type='tel']")));
        System.out.println("Page loaded, phone number field is present.");

        // Interact with Phone Number Field
        WebElement phoneNumberField = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//input[@data-semantics-role='text-field' and @type='tel']")));
        phoneNumberField.click();
        phoneNumberField.sendKeys("9888484848");
        wait.until(ExpectedConditions.attributeToBe(phoneNumberField, "value", "9888484848")); // Wait for value to be set

        // Click Continue Button
        WebElement continueButton = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//flt-semantics[@role='button' and text()='Continue']")));
        continueButton.click();
        wait.until(ExpectedConditions.invisibilityOfElementLocated(By.xpath("//flt-semantics[@role='button' and text()='Continue']"))); // Wait for button to disappear/page change

        // Interact with Password/OTP Field
        WebElement passwordField = wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector("input[type=\"password\"]")));
        passwordField.click();
        WebElement otpInput = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//input[@name='one-time-code']")));
        otpInput.sendKeys("1111");

        System.out.println("User logged in successfully.");

        // Click Services
        WebElement services = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//flt-semantics[text()='Services']")));
        services.click();
        System.out.println("Clicked 'Services'.");

        // Click Email ID
        WebElement emailid = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//flt-semantics[text()='Email ID']")));
        emailid.click();
        System.out.println("Clicked 'Email ID'.");

        // Click the radio button
        WebElement hl = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//flt-semantics[@role='radio' and @aria-checked='false']")));
        hl.click();
        System.out.println("Clicked radio button.");

        // Click Continue (after radio button selection)
        WebElement pop = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//flt-semantics[text()='Continue']")));
        pop.click();
        System.out.println("Clicked 'Continue' after radio button selection.");

        // Click Pencil icon
        // This XPath is highly brittle due to transform matrix. Prioritize other locators if possible.
        WebElement pencil = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//flt-semantics[contains(@style, 'transform: matrix(1, 0, 0, 1, 1488.22, 262.5);')]")));
        pencil.click();
        System.out.println("Clicked pencil icon (using brittle transform matrix XPath).");

        // Click Continue (after editing, presumably)
        wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//flt-semantics[text()='Continue']"))).click();
        System.out.println("Clicked 'Continue' after pencil icon action.");

        // Enter new Email Address
        WebElement emailAddressInput = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//input[@aria-label='Enter email address']")));
        emailAddressInput.clear(); // Clear existing email if any
        emailAddressInput.sendKeys("mkmk@gmail.com");
        System.out.println("Entered new email address.");

        // Click Date of Birth field to open calendar/picker
        // This XPath is also based on transform matrix, highly brittle.
        WebElement dobField = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//flt-semantics[contains(@style, 'transform: matrix(1, 0, 0, 1, 360, 28);')]")));
        dobField.click();
        System.out.println("Clicked DOB field to open calendar.");

        // Click "Select year" to switch to the year selection view
        WebElement selectYearButton = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//flt-semantics[contains(text(), 'Select year')]")));
        selectYearButton.click();
        System.out.println("Clicked on year selection icon to open year list.");

        // --- Start of Year Scrolling Logic ---
        // Identify the scrollable container within the date picker.
        // THIS XPATH IS CRUCIAL. YOU MUST VERIFY IT IN YOUR BROWSER'S DEV TOOLS.
        // Look for a div with `overflow: auto;` or `overflow: scroll;`
        WebElement scrollableYearContainer = wait.until(ExpectedConditions.presenceOfElementLocated(
                By.xpath("//div[contains(@class, 'mat-calendar-content')]") // Common for Material Design calendar content
        ));
        System.out.println("Identified the scrollable year container.");

        // Target XPath for the year 1900
        String targetYearXPath = "//flt-semantics[text()='1900']"; // Using flt-semantics based on your latest snippets

        int attempts = 0;
        int maxScrollAttempts = 200; // Safety break, adjust based on how many years to scroll
        int scrollAmount = 200;     // Pixels to scroll each time (adjust as needed for your UI)

        while (attempts < maxScrollAttempts) {
            try {
                // Check if 1900 is visible (or present in the current DOM view of the scrollable container)
                WebElement year1900 = scrollableYearContainer.findElement(By.xpath(targetYearXPath)); // IMPORTANT: find within the container
                // If element is found, it means it's now in the DOM and potentially visible.
                System.out.println("Year 1900 is visible. Clicking...");
                year1900.click();
                System.out.println("Clicked on 1900.");
                Thread.sleep(500); // Small pause for selection to register
                break; // Exit loop after clicking
            } catch (NoSuchElementException e) {
                // If 1900 is not found, scroll the container UP.
                // This moves the view down the list, revealing content that was previously above (older years).
                // To move the scrollbar "up" visually to reveal older content, you decrease scrollTop.
                js.executeScript("arguments[0].scrollTop -= arguments[1];", scrollableYearContainer, scrollAmount);
                System.out.println("Scrolled container UP by " + scrollAmount + " pixels. Attempt " + (attempts + 1));
                Thread.sleep(200); // Small pause for the scroll animation/render
            }
            attempts++;
        }

        if (attempts >= maxScrollAttempts) {
            System.out.println("Could not find year 1900 after " + maxScrollAttempts + " scroll attempts. It might not be reachable by scrolling or XPath is wrong.");
            throw new RuntimeException("Target year 1900 not found or reachable via scrolling.");
        }
        // --- End of Year Scrolling Logic ---

        // After selecting year, proceed with month selection
        for (int i = 0; i < 4; i++) {
            try {
                WebElement previousMonthButton = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//flt-semantics[text()='Previous month']")));
                previousMonthButton.click();
                System.out.println("Clicked 'Previous month' (Attempt " + (i + 1) + "/4)");
                Thread.sleep(500); // Adjust delay as needed, or use a more specific wait
            } catch (Exception e) {
                System.err.println("Failed to click 'Previous month' on attempt " + (i + 1) + ": " + e.getMessage());
                break; // Stop if the element becomes unclickable or disappears
            }
        }
        System.out.println("'Previous month' clicks completed.");

        // Click a specific date (e.g., 27, April 27, 1900)
        // Ensure this XPath is accurate for the date format displayed after year and month selection
        wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//flt-semantics[contains(text(), '27, April 27, 1900')]"))).click();
        System.out.println("Clicked '27, April 27, 1900'.");

        // Click OK
        wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//flt-semantics[text()='OK']"))).click();
        System.out.println("Clicked 'OK' on date picker.");

        // Click Continue (after date selection)
        wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//flt-semantics[@role='button' and text()='Continue']"))).click();
        System.out.println("Clicked 'Continue' after date selection.");


        String extractedOtp = null;
        try {
            System.out.println("Waiting for OTP to be generated and logged on the remote server...");
            Thread.sleep(15000); // Adjust as needed

            downloadLogFileFromRemoteServer(REMOTE_HOST, REMOTE_PORT, REMOTE_USERNAME, REMOTE_PASSWORD, REMOTE_LOG_PATH, LOCAL_TEMP_LOG_PATH);
            System.out.println("Log file downloaded successfully to: " + LOCAL_TEMP_LOG_PATH);
            System.out.println("Attempting to read OTP from local file: " + LOCAL_TEMP_LOG_PATH);

            extractedOtp = getOtpFromLocalLog(LOCAL_TEMP_LOG_PATH);

            if (extractedOtp != null) {
                System.out.println("Successfully extracted OTP: " + extractedOtp);
                // Locate the OTP input field and send keys
                WebElement finalOtpInput = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//input[@name='one-time-code']"))); // Adjust if this is a different OTP field
                finalOtpInput.sendKeys(extractedOtp);
                System.out.println("Entered extracted OTP into the field.");

            } else {
                System.out.println("OTP not found in the downloaded log file. Check log format and timing.");
                throw new RuntimeException("OTP could not be extracted from log.");
            }

        } catch (Exception e) {
            System.err.println("An error occurred during log download or OTP extraction: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Failed to get OTP from remote log.", e);
        } finally {
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

        // Final Continue button after OTP
        wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//flt-semantics[@flt-tappable and text()='Continue']"))).click();
        System.out.println("Clicked final 'Continue' button after OTP.");
    }


    // This method is for general page-level scrollIntoView, not for internal container scrolling.
    // It is no longer used for the year selection, but kept if you use it elsewhere.
    private void scrollAndClickElement(By by) {
        try {
            WebElement element = wait.until(ExpectedConditions.presenceOfElementLocated(by));
            ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", element);
            System.out.println("Scrolled element located by '" + by.toString() + "' into view.");
            wait.until(ExpectedConditions.elementToBeClickable(by)).click();
            System.out.println("Successfully clicked on element located by: " + by.toString());
        } catch (Exception e) {
            System.err.println("Failed to scroll to and click element located by '" + by.toString() + "': " + e.getMessage());
            throw e;
        }
    }

    private void downloadLogFileFromRemoteServer(String host, int port, String username, String password, String remoteFilePath, String localFilePath) throws JSchException, SftpException {
        JSch jsch = new JSch();
        Session session = null;
        ChannelSftp channelSftp = null;

        try {
            session = jsch.getSession(username, host, port);
            session.setPassword(password);

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

    private String getOtpFromLocalLog(String logFilePath) throws IOException {
        String otp = null;
        Pattern pattern = Pattern.compile("(?<!\\d)\\b(\\d{6})\\b(?!\\d)");

        try (BufferedReader reader = new BufferedReader(new FileReader(logFilePath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                Matcher matcher = pattern.matcher(line);
                if (matcher.find()) {
                    otp = matcher.group(1);
                    break;
                }
            }
        }
        return otp;
    }

    @AfterMethod
    void tearDown() {
        if (driver != null) {
            driver.quit();
            System.out.println("Browser closed.");
        }
    }
}