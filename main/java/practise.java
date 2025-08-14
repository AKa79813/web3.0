import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;

import java.time.Duration;

public class practise {

    public static void main(String[] args) {
WebDriverManager.chromedriver().setup();

WebDriver driver = new ChromeDriver();

driver.get("");
driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(20));





    }
}
