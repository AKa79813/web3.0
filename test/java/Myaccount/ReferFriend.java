package Myaccount;

import onboarding.Login; // Assuming onboarding.Login exists and handles WebDriver setup
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.annotations.AfterMethod; // For TestNG cleanup
import org.testng.annotations.BeforeMethod; // For TestNG setup
import org.testng.annotations.Test;

import java.time.Duration;

public class ReferFriend {
    // Declare driver at the class level so it's accessible to all methods
    private WebDriver driver;
    private WebDriverWait wait;

    // It's good practice to encapsulate common setup steps, especially WebDriver initialization.
    // If Login.setup() returns WebDriver, we can use that.
    // Otherwise, you'd initialize WebDriver directly here.
    @BeforeMethod
    public void setUpBrowser() throws InterruptedException {
        // Option 1: If Login.setup() creates and returns a WebDriver instance
        // Assuming Login.setup() returns WebDriver. If not, modify Login.setup()
        // to return WebDriver or initialize WebDriver directly here.
        Login login = new Login();
        driver = login.setup(); // Assuming Login.setup() initializes and returns WebDriver

        // Option 2: Initialize WebDriver directly here if Login.setup() doesn't return it
        // System.setProperty("webdriver.chrome.driver", "path/to/chromedriver.exe"); // Uncomment and set your path
        // driver = new ChromeDriver();

        wait = new WebDriverWait(driver, Duration.ofSeconds(20)); // Initialize WebDriverWait

    }

    @Test
    void clickReferAFriend() throws InterruptedException { // Renamed method for clarity

        // Wait for the profile button to be clickable and click it
        // Note: The XPath for profile was missing "//" at the beginning if it's an absolute path
        // If 'flt-semantics' is the root, use just the tag. If it's anywhere in the DOM, use //.
        // Assuming it's anywhere in the DOM for robustness.
        WebElement profile = wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("//flt-semantics[contains(@style, 'transform: matrix(1, 0, 0, 1, 2082.44, 55);')]")
        ));
        profile.click();
        System.out.println("Clicked on profile button.");

        // Locate the "Refer A Friend" element
        WebElement referAFriendElement = wait.until(ExpectedConditions.presenceOfElementLocated(
                By.xpath("//flt-semantics[contains(@aria-label, 'Refer A Friend') and @role='menuitem']")
        ));

        // Scroll the element into view
        ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", referAFriendElement);
        System.out.println("Scrolled element into view.");

        // Optional: Small wait after scrolling if there are animations or rendering delays
        Thread.sleep(1000);

        // Click the element using JavaScript Executor
        wait.until(ExpectedConditions.elementToBeClickable(referAFriendElement));
        ((JavascriptExecutor) driver).executeScript("arguments[0].click();", referAFriendElement);
        System.out.println("'Refer A Friend' element clicked via JavaScript.");

        // Add any assertions or further actions here to verify the click effect
        // For example, verify navigation to the referral page, or a success message
        // wait.until(ExpectedConditions.urlContains("referral"));
    }

//    @AfterMethod
//    public void tearDownBrowser() {
//        if (driver != null) {
//            driver.quit(); // Close the browser
//            System.out.println("Browser closed.");
        }

