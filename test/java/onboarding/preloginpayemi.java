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

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

public class preloginpayemi {

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
        WebElement quickpay = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//flt-semantics[contains(text(), \"Quick Pay\")]")));
        quickpay.click();
     driver.findElement(By.xpath("//input[@type='text']")).click();
      driver.findElement(By.xpath("//input[@type='text']")).sendKeys("CLLOAN220105");
      WebElement no = driver.findElement(By.xpath("//input[contains(@aria-label, \"+91\")]"));
     no.click();


        driver.findElement(By.xpath("//input[contains(@aria-label, \"+91\")]")).sendKeys("6362285653");
        driver.findElement(By.xpath("//flt-semantics[text()=\"Proceed\"]")).click();



        
        //flt-semantics[@role='button'][@tabindex='0']
}}
