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
import org.testng.annotations.Test;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

public class HLpayment {

    WebDriver driver;

    @Test
    void setup() throws InterruptedException {
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

        // Initialize the ChromeDriver, passing the fully configured ChromeOptions
        driver = new ChromeDriver(options);

        // Now that the driver is initialized, set implicit wait and WebDriverWait
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(20));

        // Navigate to URL
        driver.get("https://uatd2cwbs.ltfinance.com/planet-web/");

        System.out.println("Chrome browser launched with notification handling preferences and maximized.");

        // It's generally better to use explicit waits instead of Thread.sleep()
        // For example, waiting for an element to be visible before interacting.
        // For demonstration, keeping Thread.sleep for initial page load, but aim to replace.
        try {
            Thread.sleep(6000); // Consider replacing with an explicit wait for a page element
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt(); // Re-interrupt the thread
            throw new RuntimeException("Thread interrupted during sleep", e);
        }

        // Wait for the input field to be clickable before interacting
        WebElement telInputField = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//input[@data-semantics-role='text-field' and @type='tel']")));
        telInputField.click();
        telInputField.sendKeys("9888484848");

        // Use explicit wait for the continue button
        WebElement continueButton = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//flt-semantics[@role='button' and text()='Continue']")));
        continueButton.click();

        // Use explicit wait for the password/OTP field
        WebElement otpField = wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector("input[type=\"password\"]")));
        otpField.click();
        otpField.sendKeys("1111");

        System.out.println("User logged in successfully");

        WebElement payemi = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//flt-semantics[@role='button'][@tabindex='0'][text()='Pay EMI']")));

        // 1. Scroll the element into view using JavascriptExecutor
        JavascriptExecutor js = (JavascriptExecutor) driver;
        js.executeScript("arguments[0].scrollIntoView(true);", payemi);
        System.out.println("Scrolled 'Pay EMI' element into view.");

        // 2. Re-confirm it's clickable (optional, but good for robustness after scrolling)
        wait.until(ExpectedConditions.elementToBeClickable(payemi));

        // 3. Click the element
        payemi.click();
        System.out.println("'Pay EMI' button clicked.");

        driver.findElement(By.xpath("//input[@type='text'][@spellcheck='false'][@autocorrect='off'][@autocomplete='off']")).click();

        driver.findElement(By.xpath("//input[@type='text'][@spellcheck='false'][@autocorrect='off'][@autocomplete='off']")).sendKeys("500");
        driver.findElement(By.xpath("//input[@type='text'][@spellcheck='false'][@autocorrect='off'][@autocomplete='off']")).click();


    }

//            @AfterMethod
//            void tearDown() {
//                if (driver != null) {
//                    driver.quit();
//                    System.out.println("Browser closed.");
//                }
//            }
}