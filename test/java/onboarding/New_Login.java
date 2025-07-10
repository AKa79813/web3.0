package onboarding;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.annotations.Test;

import java.time.Duration;
import java.util.HashMap; // Required for the HashMap
import java.util.Map;     // Required for the Map interface

        public class New_Login {
            WebDriver driver;

            @Test
            void setup() throws InterruptedException {
                // Create ChromeOptions object
                // Setup WebDriverManager to automatically download and configure ChromeDriver
                WebDriverManager.chromedriver().setup();
                ChromeOptions options = new ChromeOptions();
                WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(60));


                // --- Method 1: Using preferences to explicitly block notifications (More robust) ---
                // Create a HashMap to store browser preferences
                Map<String, Object> prefs = new HashMap<>();
                // Set the preference for notifications: 2 means BLOCK
                // You can also add other preferences here, e.g., for geolocation, popups, etc.
                prefs.put("profile.default_content_setting_values.notifications", 2);
                // prefs.put("profile.default_content_setting_values.geolocation", 2); // To block geolocation prompts

                // Add the preferences to ChromeOptions
                options.setExperimentalOption("prefs", prefs);

                // --- Method 2: Command-line arguments (often works, good to keep as backup) ---
                // These can sometimes be redundant with the preferences above but don't hurt.
                options.addArguments("--disable-notifications"); // Standard argument
                options.addArguments("--disable-popup-blocking"); // Might help if it's categorized as a popup

                // --- Other useful options for automation (helps make browser cleaner) ---
                // Disables the "Chrome is being controlled by automated test software" infobar
                options.setExperimentalOption("excludeSwitches", new String[]{"enable-automation"});
                options.addArguments("--disable-infobars");
                options.addArguments("--disable-extensions"); // Disables extensions
                options.addArguments("--start-maximized"); // Ensure browser starts maximized




                // Initialize the ChromeDriver, passing the ChromeOptions


                // No need for driver.manage().window().maximize() if using --start-maximized in options

                // Navigate to URL

                driver = new ChromeDriver(options); // Pass the configured options here

                driver.get("https://uatd2cwbs.ltfinance.com/planet-web/");

                System.out.println("Chrome browser launched with notification handling preferences.");

                try {
                    Thread.sleep(6000);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                driver.findElement(By.xpath("//input[@data-semantics-role='text-field' and @type='tel']")).click();
                driver.findElement(By.xpath("//input[@data-semantics-role='text-field' and @type='tel']")).sendKeys("9888484848");
                Thread.sleep(2000);

               driver.findElement(By.xpath("//flt-semantics[@role='button' and text()='Continue']")).click();
               Thread.sleep(4000);

                driver.findElement(By.cssSelector("input[type=\"password\"]")).click();
                Thread.sleep(1000);
                driver.findElement(By.xpath("//input[@name='one-time-code']")).sendKeys("1111");

                System.out.println(" user logged in successfully");









            }



//            @AfterMethod
//            void tearDown() {
//                if (driver != null) {
//                    driver.quit();
//                    System.out.println("Browser closed.");
//                }
        }


