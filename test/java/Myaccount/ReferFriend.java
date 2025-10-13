package Myaccount;

import onboarding.Login;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions; // Required for Actions class
import org.openqa.selenium.Keys; // Required for Keys.PAGE_DOWN
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.time.Duration;

public class ReferFriend {
    // Declare driver, wait, and actions at the class level for accessibility
    private WebDriver driver;
    private WebDriverWait wait;
    private Actions actions;

    @BeforeMethod
    public void setUpBrowser() throws InterruptedException {
        Login login = new Login();
        driver = login.setup(); // ASSUMPTION: Login.setup() returns an initialized WebDriver instance
        driver.manage().window().maximize(); // Maximize browser window for better viewability
        wait = new WebDriverWait(driver, Duration.ofSeconds(20)); // Initialize WebDriverWait
        actions = new Actions(driver); // Initialize Actions object
        System.out.println("Browser setup and login complete.");
    }

    @Test
    void scrollAndClickReferAFriend() throws InterruptedException {
        JavascriptExecutor js = (JavascriptExecutor) driver;

        // 1. Click the profile button to open the sidebar.
        // Using a more robust XPath based on attributes like role, tabindex, aria-expanded.
        WebElement profileButton = wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("//flt-semantics[contains(@style, 'transform: matrix(1, 0, 0, 1, 2082.44, 55);')]")
                // If the above doesn't work consistently, and the matrix is stable for this element,
                // you could use: By.xpath("//flt-semantics[contains(@style, 'transform: matrix(1, 0, 0, 1, 2082.44, 55);')]")
        ));
        profileButton.click();
        System.out.println("Clicked on profile button to open sidebar.");

        // Add a sufficient wait for the sidebar to fully appear and its content to load.
        Thread.sleep(2500); // Increased sleep for robust loading

        // 2. Identify the scrollable sidebar container using the provided XPath.
        // We're assuming this element (despite overflow:visible) is what Flutter uses for scrolling.
        WebElement sidebarMainContainer = wait.until(ExpectedConditions.presenceOfElementLocated(
                By.xpath("//flt-semantics[@role='menuitem']")
        ));
        System.out.println("Located the main sidebar container using its style attribute.");

        // 3. Simulate mouse-over and PAGE_DOWN scroll on the sidebar container.
        actions.moveToElement(sidebarMainContainer).perform(); // Move mouse cursor to the sidebar
        System.out.println("Moved mouse cursor to sidebar container for scrolling.");

        // Press PAGE_DOWN multiple times to scroll down the sidebar.
        // Adjust the loop count (e.g., 2 or 3 times) based on how far down "Refer A Friend" is located.
        for (int i = 0; i < 4; i++) { // Start with 2 PageDown presses, adjust if needed
            actions.sendKeys(Keys.PAGE_DOWN).perform();
            Thread.sleep(1000); // Short pause for animation/rendering
            System.out.println("Simulated Page UP scroll on sidebar (iteration " + (i + 1) + ").");
        }

        // 4. Locate the "Refer A Friend" element after scrolling.
        // It should now be visible and clickable within the scrolled sidebar.
        WebElement referAFriendElement = wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("//flt-semantics[contains(@aria-label, 'Refer A Friend') and @role='menuitem')]")
        ));
        System.out.println("Located 'Refer A Friend' element after scrolling.");

        // 5. Click the "Refer A Friend" element using JavaScript Executor for reliability.
        js.executeScript("arguments[0].click();", referAFriendElement);
        System.out.println("'Refer A Friend' element clicked via JavaScript.");

        // Add any assertions or further actions here to verify the click effect.
        Thread.sleep(3000); // For observation
    }

//    @AfterMethod
//    public void tearDownBrowser() {
//        if (driver != null) {
//            driver.quit(); // Close the browser
//            System.out.println("Browser closed.");
//        }
//    }
}