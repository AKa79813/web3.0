import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

import java.util.List;

public class SFDC {
    public static void main(String[] args) throws InterruptedException {


        ChromeOptions options = new ChromeOptions();
        options.addArguments("--disable-notifications");
        WebDriver driver = new ChromeDriver(options);

        String url = "https://ltfs--sit.sandbox.my.salesforce.com/"; // Or any other URL you want to test
        System.out.println("Navigating to: " + url);
        driver.get(url);
         driver.findElement(By.xpath("//input[@id='username']")).sendKeys("wajid.khan1@ltfs.com.sit");
        driver.findElement(By.xpath("//input[@id='password']")).sendKeys("7020068681Wa#");
        driver.findElement(By.xpath("//input[@id='Login']")).click();

        Thread.sleep(6000);

      //  driver.switchTo().alert().accept();
        Thread.sleep(3000);

        driver.findElement(By.xpath("//button[@type='button' and contains(@class, 'search-button')]")).click();
        Thread.sleep(3000);





    }
}
//button[@aria-label='Search']