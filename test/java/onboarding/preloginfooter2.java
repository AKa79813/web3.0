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

import java.io.File; // Import for File operations
import java.io.IOException; // Import for IOException handling
import org.openqa.selenium.OutputType; // Import for screenshot output type
import org.openqa.selenium.TakesScreenshot; // Import for TakesScreenshot interface
import org.apache.commons.io.FileUtils; // Import for FileUtils to copy files

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

public class preloginfooter2 {

    WebDriver driver;
    WebDriverWait wait; // Declare WebDriverWait at class level for reusability

    @Test
    void setup() {
        // Setup WebDriverManager to automatically download and configure ChromeDriver
        WebDriverManager.chromedriver().setup();

        // Create ChromeOptions object to configure browser behavior
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
        options.addArguments("--start-maximized");

        // Initialize the ChromeDriver
        driver = new ChromeDriver(options);

        // Initialize WebDriverWait with a maximum timeout of 40 seconds
        wait = new WebDriverWait(driver, Duration.ofSeconds(40));

        try {
            // Navigate to the initial URL
            driver.get("https://uatd2cwbs.ltfinance.com");
            System.out.println("Chrome browser launched with notification handling preferences.");


            // Example usage of the new scrollAndClickElement method:
            // First, click on "flt-semantic-node-23" as per your original logic
            scrollAndClickElement(By.xpath("//*[@id=\"flt-semantic-node-23\"]"), "flt-semantic-node-23");
            Thread.sleep(4000);




            WebElement homeloan = wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("a.breadcrumb__item-link")));

            Thread.sleep(4000);
            takeScreenshot(driver, "home loan navigation"); // Screenshot after click
//
//            Thread.sleep(2000);

            driver.navigate().to("https://uatd2cwbs.ltfinance.com");

            scrollAndClickElement(By.xpath("//*[@id=\"flt-semantic-node-24\"]"), "flt-semantic-node-24");
            System.out.println("Navigated personal loan page.");
            Thread.sleep(4000);
            takeScreenshot(driver, "personal_loan_page.png"); // Screenshot after navigation and click

            Thread.sleep(2000);
            driver.navigate().to("https://uatd2cwbs.ltfinance.com");
            //takeScreenshot(driver, "homepage_before_node_25_click.png");
            scrollAndClickElement(By.xpath("//*[@id=\"flt-semantic-node-25\"]"), "flt-semantic-node-25");
            Thread.sleep(4000);
            System.out.println("Navigated Two wheeler loan page.");
            takeScreenshot(driver, "two_wheeler_loan_page.png");

            Thread.sleep(2000);
            driver.navigate().to("https://uatd2cwbs.ltfinance.com");
          //  takeScreenshot(driver, "homepage_before_node_26_click.png");
            scrollAndClickElement(By.xpath("//*[@id=\"flt-semantic-node-26\"]"), "flt-semantic-node-26");
            System.out.println("Navigated Business loan page.");
            Thread.sleep(4000);
            takeScreenshot(driver, "business_loan_page.png");

            Thread.sleep(2000);
            driver.navigate().to("https://uatd2cwbs.ltfinance.com");
           // takeScreenshot(driver, "homepage_before_node_27_click.png");
            scrollAndClickElement(By.xpath("//*[@id=\"flt-semantic-node-27\"]"), "flt-semantic-node-27");
            System.out.println("Navigated LAP loan page.");
            Thread.sleep(4000);
            takeScreenshot(driver, "lap_loan_page.png");

            Thread.sleep(2000);
            driver.navigate().to("https://uatd2cwbs.ltfinance.com");
           // takeScreenshot(driver, "homepage_before_node_28_click.png");
            scrollAndClickElement(By.xpath("//*[@id=\"flt-semantic-node-28\"]"), "flt-semantic-node-28");
            System.out.println("Navigated Rural loan page.");
            Thread.sleep(4000);
            takeScreenshot(driver, "rural_loan_page.png");

            Thread.sleep(2000);
            driver.navigate().to("https://uatd2cwbs.ltfinance.com");
           // takeScreenshot(driver, "homepage_before_node_29_click.png");
            scrollAndClickElement(By.xpath("//*[@id=\"flt-semantic-node-29\"]"), "flt-semantic-node-29");
            System.out.println("Navigated Tractor loan page.");
            Thread.sleep(4000);
            takeScreenshot(driver, "tractor_loan_page.png");

            Thread.sleep(2000);
            driver.navigate().to("https://uatd2cwbs.ltfinance.com");
           // takeScreenshot(driver, "homepage_before_node_30_click.png");
            scrollAndClickElement(By.xpath("//*[@id=\"flt-semantic-node-30\"]"), "flt-semantic-node-30");
            System.out.println("Navigated WRF loan page.");
            Thread.sleep(4000);
            takeScreenshot(driver, "wrf_loan_page.png");

            Thread.sleep(2000);
            driver.navigate().to("https://uatd2cwbs.ltfinance.com");
          //  takeScreenshot(driver, "homepage_before_node_33_click.png");
            scrollAndClickElement(By.xpath("//*[@id=\"flt-semantic-node-33\"]"), "flt-semantic-node-33");
            System.out.println("Navigated locate us page.");
            Thread.sleep(4000);
            takeScreenshot(driver, "locate_us_page.png");

            Thread.sleep(2000);
            driver.navigate().to("https://uatd2cwbs.ltfinance.com");
          //  takeScreenshot(driver, "homepage_before_node_34_click.png");
            scrollAndClickElement(By.xpath("//*[@id=\"flt-semantic-node-34\"]"), "flt-semantic-node-34");
            System.out.println("Navigated contact us page.");
            Thread.sleep(4000);
            takeScreenshot(driver, "contact_us_page.png");

            Thread.sleep(2000);
            driver.navigate().to("https://uatd2cwbs.ltfinance.com");
          //  takeScreenshot(driver, "homepage_before_node_35_click.png");
            scrollAndClickElement(By.xpath("//*[@id=\"flt-semantic-node-35\"]"), "flt-semantic-node-35");
            System.out.println("Navigated About us page.");
            Thread.sleep(4000);
            takeScreenshot(driver, "about_us_page.png");

            Thread.sleep(2000);
            driver.navigate().to("https://uatd2cwbs.ltfinance.com");
          //  takeScreenshot(driver, "homepage_before_node_46_click.png");
            scrollAndClickElement(By.xpath("//*[@id=\"flt-semantic-node-46\"]"), "flt-semantic-node-46");
            System.out.println("Navigated L&T page.");
            Thread.sleep(4000);
            takeScreenshot(driver, "l_and_t_page.png");

            Thread.sleep(1000);
            driver.navigate().to("https://uatd2cwbs.ltfinance.com");
          //  takeScreenshot(driver, "homepage_before_node_48_click.png");
            scrollAndClickElement(By.xpath("//*[@id=\"flt-semantic-node-48\"]"), "flt-semantic-node-48");
            System.out.println("Navigated L&T business page.");
            Thread.sleep(4000);
            takeScreenshot(driver, "l_and_t_business_page.png");

            Thread.sleep(1000);
            driver.navigate().to("https://uatd2cwbs.ltfinance.com");
         //   takeScreenshot(driver, "homepage_before_node_49_click.png");
            scrollAndClickElement(By.xpath("//*[@id=\"flt-semantic-node-49\"]"), "flt-semantic-node-49");
            System.out.println("Navigated consultant page.");
            Thread.sleep(4000);
            takeScreenshot(driver, "consultant_page.png");

            Thread.sleep(1000);
            driver.navigate().to("https://uatd2cwbs.ltfinance.com");
          //  takeScreenshot(driver, "homepage_before_node_50_click.png");
            scrollAndClickElement(By.xpath("//*[@id=\"flt-semantic-node-50\"]"), "flt-semantic-node-50");
            System.out.println("Navigated merged page.");
            Thread.sleep(4000);
            takeScreenshot(driver, "merged_page.png");

            Thread.sleep(1000);
            driver.navigate().to("https://uatd2cwbs.ltfinance.com");
          //  takeScreenshot(driver, "homepage_before_node_55_click.png");
            scrollAndClickElement(By.xpath("//*[@id=\"flt-semantic-node-55\"]"), "flt-semantic-node-55");
            System.out.println("Navigated Disclaimer page.");
            Thread.sleep(4000);
            takeScreenshot(driver, "disclaimer_page.png");

            Thread.sleep(1000);
            driver.navigate().to("https://uatd2cwbs.ltfinance.com");
            //takeScreenshot(driver, "homepage_before_node_57_click.png");
            scrollAndClickElement(By.xpath("//*[@id=\"flt-semantic-node-57\"]"), "flt-semantic-node-57");
            System.out.println("Navigated privacy policy page.");
            Thread.sleep(4000);
            takeScreenshot(driver, "privacy_policy_page.png");

            Thread.sleep(1000);
            driver.navigate().to("https://uatd2cwbs.ltfinance.com");
            //takeScreenshot(driver, "homepage_before_node_59_click.png");
            scrollAndClickElement(By.xpath("//*[@id=\"flt-semantic-node-59\"]"), "flt-semantic-node-59");
            System.out.println("Navigated cutomer advisory page.");
            Thread.sleep(4000);
            takeScreenshot(driver, "customer_advisory_page.png");

            Thread.sleep(1000);
            driver.navigate().to("https://uatd2cwbs.ltfinance.com");
            //takeScreenshot(driver, "homepage_before_node_61_click.png");
            scrollAndClickElement(By.xpath("//*[@id=\"flt-semantic-node-61\"]"), "flt-semantic-node-61");
            System.out.println("Navigated Greivance redressel page.");
            Thread.sleep(4000);
            takeScreenshot(driver, "grievance_redressal_page.png");


        } catch (Exception e) {
            System.err.println("An error occurred during navigation or element interaction: " + e.getMessage());
            e.printStackTrace();
        } finally {
            if (driver != null) {
                driver.quit();
                System.out.println("Browser closed.");
            }
        }
    }

    /**
     * Scrolls to a specified element until it is visible and then clicks it.
     * This method uses JavascriptExecutor to bring the element into the viewport
     * and then waits for the element to be clickable before performing the click.
     *
     * @param by The By locator for the element to be scrolled to and clicked.
     * @param elementName A descriptive name for the element (for logging purposes).
     */
    private void scrollAndClickElement(By by, String elementName) {
        try {
            // Find the element first
            WebElement element = wait.until(ExpectedConditions.presenceOfElementLocated(by));

            // Scroll the element into view using JavaScript
            // This ensures the element is visible in the viewport before attempting to click
            ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", element);
            System.out.println("Scrolled element '" + elementName + "' into view.");

            // Wait until the element is clickable after scrolling
            // This is crucial as scrolling might not immediately make it interactive
            wait.until(ExpectedConditions.elementToBeClickable(by)).click();
            System.out.println("Successfully clicked on element: " + elementName);

        } catch (Exception e) {
            System.err.println("Failed to scroll to and click element '" + elementName + "': " + e.getMessage());
            throw e; // Re-throw the exception to indicate failure in the test
        }
    }

    /**
     * Takes a screenshot of the current browser window and saves it to a specified file.
     * Screenshots are saved in a 'screenshots' folder within the project directory.
     * If the 'screenshots' directory does not exist, it will be created.
     *
     * @param driver The WebDriver instance.
     * @param fileName The name of the file to save the screenshot as (e.g., "my_page_screenshot.png").
     */
    private void takeScreenshot(WebDriver driver, String fileName) {
        try {
            // Ensure the driver supports taking screenshots
            if (driver instanceof TakesScreenshot) {
                // Cast the driver to TakesScreenshot and get the screenshot as a File
                File screenshotFile = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);

                // Define the directory where screenshots will be saved
                File screenshotDir = new File("./screenshots");
                if (!screenshotDir.exists()) {
                    screenshotDir.mkdirs(); // Create the directory if it doesn't exist
                }

                // Define the destination file path
                File destinationFile = new File(screenshotDir, fileName);

                // Copy the screenshot file to the destination
                FileUtils.copyFile(screenshotFile, destinationFile);
                System.out.println("Screenshot saved to: " + destinationFile.getAbsolutePath());
            } else {
                System.err.println("WebDriver does not support taking screenshots.");
            }
        } catch (IOException e) {
            System.err.println("Failed to take or save screenshot '" + fileName + "': " + e.getMessage());
            e.printStackTrace();
        }
    }
}
