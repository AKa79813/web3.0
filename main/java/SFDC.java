import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.time.Instant;
import java.util.List;

public class SFDC {
    public static void main(String[] args) throws InterruptedException {


        ChromeOptions options = new ChromeOptions();
        options.addArguments("--disable-notifications");
        WebDriver driver = null;
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(20));
        driver = new ChromeDriver(options);

        String url = "https://ltfs--sit.sandbox.my.salesforce.com/"; // Or any other URL you want to test
        System.out.println("Navigating to: " + url);
        driver.get(url);
        driver.findElement(By.xpath("//input[@id='username']")).sendKeys("wajid.khan1@ltfs.com.sit");
        driver.findElement(By.xpath("//input[@id='password']")).sendKeys("7020068681Wa#");
        driver.findElement(By.xpath("//input[@id='Login']")).click();

        Thread.sleep(6000);

        //  driver.switchTo().alert().accept();
        Thread.sleep(3000);


        WebElement searcbox = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//button[@type='button' and contains(@class, 'search-button')]")));
         searcbox.sendKeys("17184977");

    }
}
//button[@aria-label='Search']