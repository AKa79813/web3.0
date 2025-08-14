package payments;

import onboarding.Login;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.testng.Assert;
import org.testng.annotations.Test;
// Import your BaseTest class

import java.time.Duration; // Make sure this is imported if used in this class
import java.time.Instant;
import java.util.Set;

public class EMI extends Login { // Extend BaseTest

    @Test
    void payEmiTest() throws InterruptedException { // Renamed method for clarity, @Test is sufficient
        // Login is already handled by setupBrowserAndLogin() in BaseTest
        // You can add a small wait here if the page takes a moment to load
        // after login before elements like "Pay EMI" are available.
        // wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//some-element-after-login")));


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
        wait.until(ExpectedConditions.numberOfWindowsToBe(2));

        Set<String> allWindowHandles = driver.getWindowHandles();
        String payuWindowHandle = null;

        for (String handle : allWindowHandles) {
            if (!handle.equals(originalWindowHandle)) {
                payuWindowHandle = handle;
                break;
            }
        }

        if (payuWindowHandle != null) {
            driver.switchTo().window(payuWindowHandle);
            System.out.println("Switched to PayU tab. Current URL: " + driver.getCurrentUrl());

            System.out.println("\n--- Starting PayU Page Validations ---");

            try {
                String payuPageTitle = driver.getTitle();
                System.out.println("PayU Page Title: " + payuPageTitle);

                wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//div[@class='l1Item__content']/p[text()='Cards (Credit/Debit)']")));
                WebElement cardsOption = driver.findElement(By.xpath("//div[@class='l1Item__content']/p[text()='Cards (Credit/Debit)']"));
                Assert.assertTrue(cardsOption.isDisplayed(), "Cards (Credit/Debit) option not displayed.");
                System.out.println("Validation: 'Cards (Credit/Debit)' option is displayed.");
                cardsOption.click();

                WebElement cardNumberField = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//input[@data-testid='cardNumber']")));
                cardNumberField.click();
                cardNumberField.sendKeys("5123456789012346");

                WebElement cardExpiry = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//input[@id='cardExpiry']")));
                cardExpiry.click();
                cardExpiry.sendKeys("1225");

                driver.findElement(By.xpath("//input[@id='cardCvv']")).sendKeys("123");
                driver.findElement(By.xpath("//input[@name='cardOwnerName']")).sendKeys("Arun kumar");

                wait.until(ExpectedConditions.elementToBeClickable(By.xpath(" //span[contains(text(), 'PROCEED')]"))).click();
                wait.until(ExpectedConditions.elementToBeClickable(By.xpath(" //button[text()='Save and Continue']"))).click();

                WebElement code = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//*[@id='password']")));
                code.sendKeys("123456");

                driver.findElement(By.xpath("//input[@id='submitBtn']")).click();


            } catch (Exception e) {
                System.err.println("Error during PayU page validation: " + e.getMessage());
                e.printStackTrace();
                Assert.fail("PayU page validation failed: " + e.getMessage());
            }

            // Close the current PayU tab
            driver.close();
            System.out.println("Closed PayU tab.");

            // Return to the original tab
            driver.switchTo().window(originalWindowHandle);

            wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//flt-semantics[@role='button' and text()='Go to Home']")));

            System.out.println("Test completed and browser closed.");
            System.out.println("-------------- PayEMI payment success-------------");

        } else {
            System.err.println("Could not find the new PayU window handle. Test Failed.");
            Assert.fail("Failed to switch to PayU window.");
        }
    }
}