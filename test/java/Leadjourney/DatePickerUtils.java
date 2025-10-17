package Leadjourney; // MUST be in the same package as SME

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

// -----------------------------------------------------
// This is the content of DatePickerUtils.java
// -----------------------------------------------------
public class DatePickerUtils {

    private final WebDriver driver;
    private final WebDriverWait wait;

    // Constructor to initialize the WebDriver and WebDriverWait
    public DatePickerUtils(WebDriver driver, Duration timeout) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, timeout);
    }

    /**
     * Clicks to select a specific Date of Birth (October 13, 2002) in the date picker.
     */
    public void selectDateOfBirth() {
        System.out.println("Starting DOB selection...");

        // 1. Click on the Date of Birth field to open the picker
        WebElement DOB = wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("//flt-semantics[contains(@style, 'matrix(1, 0, 0, 1, 360, 4)')]")
        ));
        DOB.click();
        System.out.println("Clicked on DOB field.");

        // 2. Click on the 'Select year' text to open the year view
        WebElement Year = wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("//flt-semantics[contains(text(), 'Select year')]")
        ));
        Year.click();
        System.out.println("Clicked 'Select year' to open year list.");

        // 3. Click 2014
        WebElement year3 = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//flt-semantics[normalize-space(.)='2014']")
        ));
        year3.click();
        System.out.println("Clicked on 2014 year.");

        // 4. Click 'Select year' again
        WebElement year4 = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//flt-semantics[@role='button' and contains(., 'Select year')]")
        ));
        year4.click();
        System.out.println("Clicked on year selection year from 2014.");

        // 5. Click 2008
        WebElement year5 = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//flt-semantics[@role='button' and text()='2008']")
        ));
        year5.click();
        System.out.println("Clicked on 2008 year.");

        // 6. Click 'Select year' again
        WebElement year6 = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//flt-semantics[@role='button' and contains(., 'Select year')]")
        ));
        year6.click();
        System.out.println("Clicked on year selection year from 2008.");

        // 7. Click 2002
        WebElement year7 = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//flt-semantics[@role='button' and text()='2002']")
        ));
        year7.click();
        System.out.println("Clicked on 2002 year.");

        // 8. Click the specific date (October 13, 2002)
        WebElement Date = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//flt-semantics[@role='button' and contains(., 'October 13, 2002')]")
        ));
        Date.click();
        System.out.println("Entered date: October 13, 2002.");

        // 9. Click the OK button
        WebElement OK  = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//flt-semantics[@role='button' and text()='OK']")
        ));
        OK.click();
        System.out.println("Clicked OK.");
        System.out.println("DOB selection complete.");
    }
}