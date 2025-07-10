package onboarding;

import org.openqa.selenium.By;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.io.FileHandler;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import ru.yandex.qatools.ashot.AShot;
import ru.yandex.qatools.ashot.Screenshot;
import ru.yandex.qatools.ashot.shooting.ShootingStrategies;

import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;
import java.time.Duration;

public class screenshot {
    public static void main(String[] args) {


            ChromeOptions options = new ChromeOptions();
            // options.addArguments("--headless"); // Uncomment to run without a visible browser UI
            // If running headless, consider setting a larger window size to ensure content renders properly
            // options.addArguments("--window-size=1920,1080");

            WebDriver driver = new ChromeDriver(options);

            try {
                String url = "https://uatd2cwbs.ltfinance.com/planet-web/"; // Or any other URL you want to test
                System.out.println("Navigating to: " + url);
                driver.get(url);

                // 1. Maximize the window (important, especially for dynamic layouts)
               driver.manage().window().maximize();

                // 2. Implicit Wait (general wait for elements to appear, less precise for rendering)
                // It's still good to have, but explicit waits are better for specific conditions.
                driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));

                // --- IMPROVED WAITING STRATEGY ---
                // 3. Explicit Wait: Wait for a specific element on the page to be visible.
                // This is the most reliable way to ensure the page content is loaded.
                System.out.println("Waiting for page content to load...");
                WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(30)); // Wait up to 20 seconds

                // Example 1: Wait for the Google search box to be visible
                // Replace By.id("APjFqb") with an appropriate locator for your target page's main content element.
                wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("#flt-semantic-node-13")));
                System.out.println("Main element is visible.");

                // Example 2: You could also wait for the page title to contain a certain text
                // wait.until(ExpectedConditions.titleContains("Google"));

                // Example 3: Or wait for the entire page to be "ready" (though less reliable for all sites)
                // This checks if document.readyState is 'complete' via JavaScript execution
                // wait.until(webDriver -> ((JavascriptExecutor) webDriver).executeScript("return document.readyState").equals("complete"));


                // Optional: A small, hardcoded pause for very complex or slow-rendering pages.
                // Use sparingly, as it's inefficient and not robust. Prefer explicit waits.
                // System.out.println("Adding a small Thread.sleep for debugging (remove in production)...");
                // Thread.sleep(2000); // Wait for 2 seconds (2000 milliseconds)

                // --- Take the screenshot ---
                TakesScreenshot ts = (TakesScreenshot) driver;
                File sourceFile = ts.getScreenshotAs(OutputType.FILE);

                String userDir = System.getProperty("user.dir");
                String screenshotDir = userDir + File.separator + "screenshots";

                File destDir = new File(screenshotDir);
                if (!destDir.exists()) {
                    destDir.mkdirs();
                }

                File destinationFile = new File(screenshotDir + File.separator + "google_screenshot_with_wait.png");
                FileHandler.copy(sourceFile, destinationFile);

                System.out.println("Screenshot saved to: " + destinationFile.getAbsolutePath());

            } catch (Exception e) { // Catching a general Exception for demonstration of issue
                System.err.println("An error occurred during screenshot process: " + e.getMessage());
                e.printStackTrace();
            } finally {
                if (driver != null) {
                    driver.quit();
                    System.out.println("Browser closed.");
                }
            }
        }
    }