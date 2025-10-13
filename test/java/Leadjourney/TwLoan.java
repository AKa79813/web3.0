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
    private static WebDriverWait wait;
    private Actions actions;

    @BeforeMethod
    public void setUpBrowser() throws InterruptedException {
        Login login = new Login();
        driver = login.setup(); // ASSUMPTION: Login.setup() returns an initialized WebDriver instance
        driver.manage().window().maximize(); // Maximize browser window for better viewability
        wait = new WebDriverWait(driver, Duration.ofSeconds(30)); // Initialize WebDriverWait
        actions = new Actions(driver); // Initialize Actions object
        System.out.println("Browser setup and login complete.");
    }

@Test
    void LeadJourneys() throws InterruptedException{

    String twoWheelerLoanXPath = "//flt-semantics[@role='button' and text()='Two Wheeler Loan']";

    WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(15));

    try {
        // 1. Scroll the entire page down to ensure the element is in the viewport.
        // Adjust the value (e.g., 500) if the element is further down.
        JavascriptExecutor js = (JavascriptExecutor) driver;
        js.executeScript("window.scrollBy(0, 100)");
        System.out.println("Scrolled down 500 pixels.");

        // 2. Wait for the element to be clickable after scrolling.
        WebElement twoWheelerButton = wait.until(
                ExpectedConditions.elementToBeClickable(By.xpath(twoWheelerLoanXPath))
        );

        // 3. Click the element.
        twoWheelerButton.click();
        System.out.println("Successfully clicked 'Two Wheeler Loan' button.");

    } catch (Exception e) {
        System.err.println("Failed to scroll or click the 'Two Wheeler Loan' button.");
        e.printStackTrace();
    }

    WebElement TW = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//flt-semantics[text()='Two Wheeler Loan']")));
    TW.click();

    WebElement Apply = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//flt-semantics[text()='Apply Now']")));
    Apply.click();;

    WebElement Apply2 = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//flt-semantics[@role='button' and text()='Apply Now']")));
    Apply2.click();


    WebElement Firstname = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//input[@aria-label='Enter First Name']")));
    Firstname.sendKeys("Arun");


    WebElement Lastname = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//input[@aria-label='Enter Last Name']")));
    Lastname.sendKeys("Kumar");
 WebElement DOB = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//flt-semantics[contains(@style, 'matrix(1, 0, 0, 1, 360, 4)')]")));
  DOB.click();
  WebElement Year = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//flt-semantics[contains(text(), 'Select year')]")));
  Year.click();


//    WebElement year2 = wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector("flt-semantics[style*='transform: matrix(1, 0, 0, 1, 710, 357)']")));
//    year2.click();

    WebElement year3 = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//flt-semantics[normalize-space(.)='2014']")));
    year3.click();
    System.out.println("clicked on 2014 year ");

    WebElement year4 = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//flt-semantics[@role='button' and contains(., 'Select year')]")));
    year4.click();
    System.out.println("clicked on year selection year from 2014");

    WebElement year5 = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//flt-semantics[@role='button' and text()='2008']")));
    year5.click();
    System.out.println("clicked on 2008 year ");

    WebElement year6 = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//flt-semantics[@role='button' and contains(., 'Select year')]")));
    year6.click();
    System.out.println("clicked on year selection year from 2008 ");

    WebElement year7 = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//flt-semantics[@role='button' and text()='2002']")));
    year7.click();
    System.out.println("clicked on 2002 year ");

    WebElement Date = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//flt-semantics[@role='button' and contains(., 'October 13, 2002')]")));
    Date.click();
    System.out.println("entered date  ");


    WebElement OK  = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//flt-semantics[@role='button' and text()='OK']")));
    OK.click();


    WebElement Email = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//input[@aria-label='Enter Email Address']")));
    Email.sendKeys("gmail@12.com");

    WebElement Pan = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//input[@aria-label='Enter PAN']")));
    Pan.sendKeys("CKKPN8888R");

    WebElement Pincode = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//input[@aria-label='Enter Current Residence PIN Code']")));
    Pincode.sendKeys("562106");

    WebElement Yes  = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//flt-semantics[text()='Yes']")));
    Yes.click();

    WebElement Bike  = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//flt-semantics[text()='Select Bike Brand']")));
    Bike.click();


    WebElement Brand  = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//flt-semantics[text()='Ducati']")));
    Brand.click();

    WebElement Days  = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//flt-semantics[text()='In 30 Days']")));
    Days.click();

    WebElement Check  = wait.until(ExpectedConditions.elementToBeClickable(By.xpath(" //flt-semantics[@role='checkbox' and @aria-checked='false']")));
    Check.click();

    String submitButtonXPath = "//flt-semantics[@role='button' and text()='Submit']";

    // Initialize WebDriverWait (if not already managed by the calling class)
    JavascriptExecutor js = (JavascriptExecutor) driver;

    try {
        // 1. Scroll the entire page down.
        // Adjust the value (e.g., 500) based on how far down the "Submit" button is.
        // Using a large value ensures it's brought into the viewport.
        js.executeScript("window.scrollBy(0, 500)");
        System.out.println("Scrolled down to ensure 'Submit' button visibility.");

        // 2. Wait for the Submit button to be visible and clickable.
        WebElement submitButton = wait.until(
                ExpectedConditions.elementToBeClickable(By.xpath(submitButtonXPath))
        );

        // 3. Click the element.
        // Since it's an flt-semantics element, an additional JavaScript click can sometimes be more reliable.
        submitButton.click();
        // OR use JavaScript click if the standard click fails:
        // js.executeScript("arguments[0].click();", submitButton);

        System.out.println("Successfully clicked the 'Submit' button.");

    } catch (Exception e) {
        System.err.println("Failed to scroll or click the 'Submit' button.");
        e.printStackTrace();
    }



}

}
