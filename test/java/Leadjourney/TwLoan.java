package Leadjourney;

import onboarding.Login;
import org.openqa.selenium.*;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.time.Duration;

public class TwLoan extends Login {
    private WebDriver driver;
    private WebDriverWait wait;
    private Actions actions;
    private JavascriptExecutor js;

    // ******************************************************
    // ✅ 1. DEFINE LOCATORS ONCE (Best practice for Page Objects)
    // ******************************************************
    private final By twoWheelerLoanButton = By.xpath("//flt-semantics[@role='button' and text()='Two Wheeler Loan']");
    private final By twLoanText = By.xpath("//flt-semantics[text()='Two Wheeler Loan']");
    private final By applyNowText = By.xpath("//flt-semantics[text()='Apply Now']");
    private final By applyNowButton = By.xpath("//flt-semantics[@role='button' and text()='Apply Now']");
    private final By firstNameInput = By.xpath("//input[@aria-label='Enter First Name']");
    private final By lastNameInput = By.xpath("//input[@aria-label='Enter Last Name']");
    private final By emailInput = By.xpath("//input[@aria-label='Enter Email Address']");
    private final By panInput = By.xpath("//input[@aria-label='Enter PAN']");
    private final By pincodeInput = By.xpath("//input[@aria-label='Enter Current Residence PIN Code']");
    private final By yesButton = By.xpath("//flt-semantics[text()='Yes']");
    private final By selectBikeBrand = By.xpath("//flt-semantics[text()='Select Bike Brand']");
    private final By ducatiBrand = By.xpath("//flt-semantics[text()='Ducati']");
    private final By in30DaysOption = By.xpath("//flt-semantics[text()='In 30 Days']");
    private final By checkbox = By.xpath("//flt-semantics[@role='checkbox' and @aria-checked='false']");


    @BeforeMethod
    public void setUpBrowser() throws InterruptedException {
        Login login = new Login();
        driver = login.setup();
        driver.manage().window().maximize();
        wait = new WebDriverWait(driver, Duration.ofSeconds(30));
        actions = new Actions(driver);
        js = (JavascriptExecutor) driver; // Initialize JavascriptExecutor
        System.out.println("Browser setup and login complete.");
    }

    // ******************************************************
    // ✅ 2. HELPER METHODS TO REDUCE REDUNDANCY IN @Test
    // ******************************************************
    private void clickElement(By locator) {
        wait.until(ExpectedConditions.elementToBeClickable(locator)).click();
    }

    private void typeText(By locator, String text) {
        WebElement element = wait.until(ExpectedConditions.elementToBeClickable(locator));
        element.clear(); // Good practice to clear first
        element.sendKeys(text);
    }

    private void clickVisibleElement(By locator) {
        wait.until(ExpectedConditions.visibilityOfElementLocated(locator)).click();
    }


    // ******************************************************
    // 3. CLEANED @Test METHOD
    // ******************************************************
    @Test
    public void LeadJourneys() throws InterruptedException{

        try {
            js.executeScript("window.scrollBy(0, 100)");
            System.out.println("Scrolled down 100 pixels.");

            clickElement(twoWheelerLoanButton);
            System.out.println("Successfully clicked 'Two Wheeler Loan' button.");

        } catch (Exception e) {
            System.err.println("Failed to scroll or click the 'Two Wheeler Loan' button.");
            e.printStackTrace();
        }

        // --- Form Filling Steps ---
        clickVisibleElement(twLoanText);
        clickVisibleElement(applyNowText);
        clickElement(applyNowButton);

        typeText(firstNameInput, "Arun");
        typeText(lastNameInput, "Kumar");

        // ******************************************************
        // CALL EXTERNAL UTILITY CLASS
        // ******************************************************
        DatePickerUtils dateActions = new DatePickerUtils(driver, Duration.ofSeconds(15));
        dateActions.selectDateOfBirth();
        System.out.println("Date selection completed via DatePickerUtils.");

        // Continue filling the form
        typeText(emailInput, "gmail@12.com");
        typeText(panInput, "CKKPN8888R");
        typeText(pincodeInput, "562106");

        clickVisibleElement(yesButton);
        clickElement(selectBikeBrand);
        clickElement(ducatiBrand);
        clickElement(in30DaysOption);
        clickElement(checkbox);
    }
}