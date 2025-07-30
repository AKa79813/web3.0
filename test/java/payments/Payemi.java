package payments;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class Payemi {

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
        Thread.sleep(2000);
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

      WebElement textbox = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//input[@data-semantics-role='text-field']")));
      textbox.click();

       textbox.sendKeys("500");
       WebElement pay = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//flt-semantics[@role='button' and text()='Pay ₹ 500']")));
       pay.click();


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
           // takeScreenshot(driver, "Quickpaysuccesspage.png");
            WebElement login = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//flt-semantics[@role='button' and text()='Go to Home']")));

          //  scrollAndClickElement(By.xpath("//flt-semantics[@role='button' and text()='Go to Login']"), "Go to Login Button");

            System.out.println("Test completed and browser closed.");
            System.out.println("-------------- PayEMI payment success-------------");



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

        driver.quit();

    }






    }

//            @AfterMethod
//            void tearDown() {
//                if (driver != null) {
//                    driver.quit();
//                    System.out.println("Browser closed.");
//                }
//            }
