package Services;

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

import java.io.File;
import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

public class Addressupdate {


    WebDriver driver;

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
        driver.get("https://uatd2cwbs.ltfinance.com");

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
        phoneNumberField.sendKeys("9888484848");
        Thread.sleep(2000);

        WebElement continueButton = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//flt-semantics[@role='button' and text()='Continue']")));
        continueButton.click();
        Thread.sleep(4000);

        WebElement passwordField = wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector("input[type=\"password\"]")));
        passwordField.click();
        Thread.sleep(1000); // Consider replacing Thread.sleep with WebDriverWait
        driver.findElement(By.xpath("//input[@name='one-time-code']")).sendKeys("1111");

        System.out.println(" user logged in successfully");


        WebElement services = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//flt-semantics[@role='button' and @tabindex='0' and text()='Services']")));

        services.click();

        WebElement add = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//flt-semantics[@role='button' and @tabindex='0' and text()='Address Details']")));
        add.click();

        WebElement p = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//flt-semantics[@aria-checked='false']")));
        p.click();
        System.out.println("clicked on lan");

        WebElement con = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//flt-semantics[@role='button' and @tabindex='0' and text()='Continue']")));
        con.click();

        WebElement pen =   wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//flt-semantics[contains(@style, 'transform: matrix(1, 0, 0, 1, 1488.22, 498.5);')]")));
        pen.click();
Thread.sleep(3000);
        WebElement conn =  wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//flt-semantics[@role='button' and @tabindex='0' and text()='Continue']")));
        conn.click();

        WebElement line1 = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//input[@aria-label='Enter address line 1']")));

        line1.sendKeys("Mumbai city Dolakpur ");


        driver.findElement(By.xpath("//input[@aria-label='Enter address line 2']")).sendKeys("Thane");

        WebElement pin = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//input[@aria-label='Enter Pincode']")));
        pin.sendKeys("562106");


        WebElement proof = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//flt-semantics[@role='button' and text()='Select Address Proof']")));
        proof.click();

        driver.findElement(By.xpath("//flt-semantics[@flt-tappable='' and text()='Driving License']")).click();

        try {

            String filePath = new File("C:\\Users\\ven06482\\OneDrive\\Pictures\\Screenshots\\Screenshot 2025-03-17 111651.png").getAbsolutePath();
            System.out.println("Attempting to upload file from: " + filePath);

            // Find the hidden input field and send the file path directly
            // You MUST find the correct locator for the input[type='file'] element
            WebElement fileInput = driver.findElement(By.xpath("//input[@type='text' and @data-semantics-role='text-field' and contains(@aria-label, 'Upload Address Proof')]")); // THIS IS A PLACEHOLDER. FIND THE CORRECT XPATH.
            fileInput.sendKeys(filePath);

            System.out.println("File path sent to the input field.");
            wait.until(ExpectedConditions.textToBePresentInElementLocated(By.xpath("//div[contains(text(), 'Screenshot 2025-03-17')]"), "Screenshot 2025-03-17"));
            System.out.println("File uploaded successfully.");

        } catch (Exception e) {
            e.printStackTrace();
        }



        WebElement c = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//flt-semantics[@role='button' and @tabindex='0' and text()='Continue']")));
        c.click();

Thread.sleep(7000);
        driver.findElement(By.xpath("//flt-semantics[@role='button' and @tabindex='0' and text()='Go to home']")).click();
}}
