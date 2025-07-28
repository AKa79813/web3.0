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

public class Cancelmandate {

    WebDriver driver;

    @Test
    void setup() throws Exception {
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

        // Initialize the ChromeDriver, PASSING THE CONFIGURED OPTIONS HERE
        driver = new ChromeDriver(options); // <--- Corrected line: pass 'options' here

        // Now you can set implicit wait
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));

        // No need for driver.manage().window().maximize() if using --start-maximized in options

        // Navigate to URL
        driver.get("https://uatd2cwbs.ltfinance.com/planet-web/");

        System.out.println("Chrome browser launched with notification handling preferences.");


        // Using WebDriverWait for better synchronization
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(20)); // Initialize here

        WebElement phoneNumberField = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//input[@data-semantics-role='text-field' and @type='tel']")));
        phoneNumberField.click();
        phoneNumberField.sendKeys("8277159501");
        Thread.sleep(2000);

        WebElement continueButton = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//flt-semantics[@role='button' and text()='Continue']")));
        continueButton.click();
        Thread.sleep(4000);

        WebElement passwordField = wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector("input[type=\"password\"]")));
        passwordField.click();
        Thread.sleep(1000); // Consider replacing Thread.sleep with WebDriverWait
        driver.findElement(By.xpath("//input[@name='one-time-code']")).sendKeys("1111");

        System.out.println(" user logged in successfully");

        WebElement Myloans = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//flt-semantics[text()=\"My Loans\"]")));
        Myloans.click();


driver.findElement(By.xpath("//flt-semantics[@role=\"tab\" and @aria-label=\"Closed\" and @aria-selected=\"false\"]")).click();

driver.findElement(By.xpath("//flt-semantics[@role=\"button\"][text()=\"View Details\"]")).click();;
driver.findElement(By.xpath("//flt-semantics[@role=\"button\" and contains(text(), \"BANK & MANDATE\")]")).click();

driver.findElement(By.xpath("//flt-semantics[@role=\"button\" and text()=\"Cancel Mandate\"]")).click();;
driver.findElement(By.xpath("//flt-semantics[@role=\"button\" and text()=\"Yes\"]")).click();;
driver.findElement(By.xpath("//flt-semantics[@role=\"radio\" and @aria-checked=\"false\"]")).click();

driver.findElement(By.xpath("//flt-semantics[@role=\"button\" and text()=\"Continue\"]")).click();

driver.findElement(By.xpath("//flt-semantics[@role=\"button\" and text()=\"Yes\"]")).click();



        //Keep this commented for now, but good practice to have for cleanup
 ///driver.quit();
      // System.out.println("Browser closed.");
    }
}

