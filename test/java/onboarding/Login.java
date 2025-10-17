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

public class Login {
    protected   WebDriver driver;
// Use 'protected' so subclasses can access it
    protected WebDriverWait wait;
    @Test
    public WebDriver setup() throws InterruptedException {
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
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(15));

        // No need for driver.manage().window().maximize() if using --start-maximized in options

        // Navigate to URL
        driver.get("https://uatd2cwbs.ltfinance.com"); //

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
        phoneNumberField.sendKeys("7474467640");
        Thread.sleep(2000);

        WebElement continueButton = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//flt-semantics[@role='button' and text()='Continue']")));
        continueButton.click();
        Thread.sleep(4000);

        WebElement passwordField = wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector("input[type=\"password\"]")));
        passwordField.click();
        Thread.sleep(1000); // Consider replacing Thread.sleep with WebDriverWait
        driver.findElement(By.xpath("//input[@name='one-time-code']")).sendKeys("1234");

        System.out.println(" user logged in successfully");
        return driver;


//        String scrollableXPath = "//flt-semantics[@role='group' and contains(@style, 'overflow-x: scroll')]";
//        WebElement scrollableElement = driver.findElement(By.xpath(scrollableXPath));
//
//        // 5. Scroll the element using JavascriptExecutor
//        JavascriptExecutor js = (JavascriptExecutor) driver;
//
//        // Scroll to the right
//        js.executeScript("arguments[0].scrollLeft += 1000;", scrollableElement);
//        // The value '1000' is the number of pixels to scroll. Adjust as needed.
//
//        Thread.sleep(2000); // Wait to observe the scroll action
//
//        // Scroll to the left
//        js.executeScript("arguments[0].scrollLeft -= 500;", scrollableElement);
//
//        //Keep this commented for now, but good practice to have for cleanup
//         //driver.quit();
//        //System.out.println("Browser closed.")
//        return driver;

    }
}