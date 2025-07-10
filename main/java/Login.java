import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Login {
    public static void main(String[] args) throws InterruptedException {

        ChromeOptions options=new ChromeOptions();
        options.addArguments("--disable-popup-blocking");
        options.addArguments("--disable-notifications");
        WebDriver driver = new ChromeDriver(options);



        try {
            driver.get("https://uatd2cwbs.ltfinance.com/planet-web/"); // Replace with the actual URL

            // Use a simpler CSS selector to find the element by its type
            By simpleTelInputLocator = By.xpath("//input[@data-semantics-role='text-field' and @type='tel']");

            // Use WebDriverWait to wait for the element to be present
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(20));
            WebElement telInput = wait.until(ExpectedConditions.presenceOfElementLocated(simpleTelInputLocator));

            // Verify the aria-label attribute to confirm it's the correct element
            String actualAriaLabel = telInput.getAttribute("aria-label");
            String expectedAriaLabel = "+91 Enter here"; // Use the exact string including newline

            if (expectedAriaLabel.equals(actualAriaLabel)) {
                System.out.println("Found the correct element by type and verified aria-label.");
                // Now you can interact with telInput
                telInput.sendKeys("6362285653");
                System.out.println("Successfully interacted with the element.");
            } else {
                System.out.println("Found an element with type='tel', but the aria-label was incorrect.");
                System.out.println("Expected aria-label: '" + expectedAriaLabel + "'");
                System.out.println("Actual aria-label:   '" + actualAriaLabel + "'");
            }

        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Could not find the element using the simple selector or verification failed.");
        } finally {
            driver.quit();
        }
    }
}