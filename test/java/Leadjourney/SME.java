package Leadjourney;

import onboarding.Login;
import org.openqa.selenium.*;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.time.Duration;

public class SME extends Login{

    private WebDriver driver;
    // NOTE: Changed to non-static as it's better practice for instance variables
    private WebDriverWait wait;
    private Actions actions;

    @BeforeMethod
    public void setUpBrowser() throws InterruptedException {
        Login login = new Login();
        driver = login.setup();
        driver.manage().window().maximize();
        wait = new WebDriverWait(driver, Duration.ofSeconds(30));
        actions = new Actions(driver);
        System.out.println("Browser setup and login complete.");
    }

    @Test
    public void LeadJourneys() throws InterruptedException{ // Made public for TestNG

        String businessLoanXPath = "//flt-semantics[@role='button' and text()='Business Loan']";

        // Use the class-level 'this.wait' (initialized to 30s)
        // No need for a redundant local WebDriverWait initialization here.

        try {
            // 1. Scroll the entire page down to ensure the element is in the viewport.
            JavascriptExecutor js = (JavascriptExecutor) driver;
            js.executeScript("window.scrollBy(0, 100)");
            System.out.println("Scrolled down 100 pixels.");

            // 2. Wait for the element to be clickable after scrolling.
            WebElement businessButton = wait.until(
                    ExpectedConditions.elementToBeClickable(By.xpath(businessLoanXPath))
            );

            // 3. Click the element.
            businessButton.click();
            System.out.println("Successfully clicked 'Business Loan' button.");

        } catch (Exception e) {
            System.err.println("Failed to scroll or click the 'Business Loan' button.");
            e.printStackTrace();
        }

        WebElement SME_Text = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//flt-semantics[text()='Business Loan']")));
        SME_Text.click();


        WebElement apply = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//flt-semantics[@role='button' and text()='Apply Now']")));
        apply.click();

        WebElement apply2 = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//flt-semantics[@role='button' and text()='Apply Now']")));
        apply2.click();



        WebElement input1 = wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//input[@data-semantics-role='text-field' and @aria-invalid='false']")));
        input1.click();
        input1.sendKeys("jeevan");

        WebElement LN = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//input[@aria-label='Enter Last Name']")));
        LN.click();
        LN.sendKeys("jeena");


        // ******************************************************
        // 🚨 CALLING THE EXTERNAL METHOD
        // 1. Instantiate the utility class
        DatePickerUtils dateActions = new DatePickerUtils(driver, Duration.ofSeconds(15));

        // 2. Call the method
        dateActions.selectDateOfBirth();
        // ******************************************************

WebElement eml= wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//input[@aria-label='Enter Email Address']")));
eml.click();
eml.sendKeys("jeevan@gmail.com");




        // You can continue with the rest of your test steps here, after DOB selection.
        // For example:
        // WebElement Email = wait.until(...);
        // Email.sendKeys("...");
    }
}