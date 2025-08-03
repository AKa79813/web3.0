package payments;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.apache.commons.io.FileUtils;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.annotations.Test;

import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Duration;
import java.time.LocalDateTime; // For handling timestamps
import java.time.format.DateTimeFormatter; // For formatting timestamps
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.testng.Assert;

// --- Database Imports ---
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class quikpDB {

    WebDriver driver;

    // --- Remote Server Details (YOU MUST CONFIGURE THESE!) ---
    private static final String REMOTE_HOST = "172.30.90.131";
    private static final int REMOTE_PORT = 22;
    private static final String REMOTE_USERNAME = "ltfadmin.d2c";
    private static final String REMOTE_PASSWORD = "Ltfs@AUG2025D2C##";
    private static final String REMOTE_LOG_PATH = "/home/ltfadmin.d2c/Logs/planet-user/log4j.log";
    private static final String LOCAL_TEMP_LOG_PATH = "D:\\Automation\\log4j_temp.log";

    // --- Database Connection Details for TB_ABCO_PAYMENTS ---
    // Make sure these match your database configuration for CustomLTFS
    private static final String DB_URL = "jdbc:sqlserver://172.30.91.45:5007;encrypt=true;trustServerCertificate=true;"; // Assuming same DB server
    private static final String DB_USERNAME = "d2c_testing_read"; // Use appropriate DB user
    private static final String DB_PASSWORD = "CC3#@aHxrscd"; // Use appropriate DB password

    private static final String PAYMENTS_TABLE_NAME = "[CustomLTFS].[dbo].[TB_ABCO_PAYMENTS]";
    private static final String LOAN_NUMBER = "BLHYBRID8713"; // The loan number used in the UI
    private static final String TEST_MOBILE_NUMBER = "8056420372"; // The mobile number used in the UI
    private static final String TEST_PAYMENT_AMOUNT = "500"; // The payment amount used in the UI


    @Test
    void setupAndValidateOTP() throws InterruptedException {
        WebDriverManager.chromedriver().setup();
        ChromeOptions options = new ChromeOptions();
        Map<String, Object> prefs = new HashMap<>();
        prefs.put("profile.default_content_setting_values.notifications", 2);
        options.setExperimentalOption("prefs", prefs);
        options.addArguments("--disable-notifications");
        options.addArguments("--disable-popup-blocking");
        options.addArguments("--start-maximized");
        options.setExperimentalOption("excludeSwitches", new String[]{"enable-automation"});
        options.addArguments("--disable-infobars");
        options.addArguments("--disable-extensions");

        driver = new ChromeDriver(options);
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(50)); // Increased timeout for initial page load

        driver.get("https://uatd2cwbs.ltfinance.com/planet-web/");
        System.out.println("Chrome browser launched with notification handling preferences and maximized.");

        try {
            WebElement quickpayButtonInitial = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//flt-semantics[contains(text(), \"Quick Pay\")]")));
            System.out.println("Page loaded successfully and Quick Pay button is visible.");
        } catch (Exception e) {
            System.err.println("Page did not load within expected time or Quick Pay button not found: " + e.getMessage());
            driver.quit();
            throw new RuntimeException("Page load failed.", e);
        }

        WebElement quickpay = driver.findElement(By.xpath("//flt-semantics[contains(text(), \"Quick Pay\")]"));
        quickpay.click();

        WebElement loanNumberInput = driver.findElement(By.xpath("//input[@type='text']"));
        loanNumberInput.click();
        loanNumberInput.sendKeys(LOAN_NUMBER);

        WebElement mobileNumberInput = driver.findElement(By.xpath("//input[contains(@aria-label, \"+91\")]"));
        mobileNumberInput.click();
        mobileNumberInput.sendKeys(TEST_MOBILE_NUMBER);

        WebElement proceedButton = driver.findElement(By.xpath("//flt-semantics[text()=\"Proceed\"]"));
        proceedButton.click();

        String extractedOtp = null;
        try {
            System.out.println("Waiting for OTP to be generated and logged on the remote server...");
            Thread.sleep(15000);

            downloadLogFileFromRemoteServer(REMOTE_HOST, REMOTE_PORT, REMOTE_USERNAME, REMOTE_PASSWORD, REMOTE_LOG_PATH, LOCAL_TEMP_LOG_PATH);
            System.out.println("Log file downloaded successfully to: " + LOCAL_TEMP_LOG_PATH);

            System.out.println("Attempting to read OTP from local file: " + LOCAL_TEMP_LOG_PATH);
            extractedOtp = getOtpFromLocalLog(LOCAL_TEMP_LOG_PATH);

            if (extractedOtp != null) {
                System.out.println("Successfully extracted OTP: " + extractedOtp);
            } else {
                System.out.println("OTP not found in the downloaded log file. Check log format and timing.");
                throw new RuntimeException("OTP could not be extracted from log.");
            }

        } catch (Exception e) {
            System.err.println("An error occurred during log download or OTP extraction: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Failed to get OTP from remote log.", e);
        } finally {
            try {
                Path localLogPath = Paths.get(LOCAL_TEMP_LOG_PATH);
                if (Files.exists(localLogPath)) {
                    Files.delete(localLogPath);
                    System.out.println("Cleaned up local temp log file: " + LOCAL_TEMP_LOG_PATH);
                }
            } catch (IOException e) {
                System.err.println("Failed to delete local temp log file: " + e.getMessage());
            }
        }

        WebElement otpInputField = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//input[contains(@aria-label, \"-\")]")));
        otpInputField.click();
        otpInputField.sendKeys(extractedOtp);
        System.out.println("Entered OTP into the input field.");

        WebElement verifyOtpButton = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//flt-semantics[@role=\"button\" and contains(text(), 'Continue')]")));
        verifyOtpButton.click();
        System.out.println("Clicked the Verify OTP button.");


        WebElement payEmifeild = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//input[@data-semantics-role='text-field']")));
        payEmifeild.click();
        payEmifeild.sendKeys(TEST_PAYMENT_AMOUNT);


        Thread.sleep(3000);
        WebElement paybutton = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//flt-semantics[text()='Pay ₹ 500']")));
        paybutton.click();
        System.out.println("Clicked the 'Pay' button, expecting a new tab.");


        String originalWindowHandle = driver.getWindowHandle();
        wait.until(ExpectedConditions.numberOfWindowsToBe(2));

        Set<String> allWindowHandles = driver.getWindowHandles();
        String payuWindowHandle = null;

        for (String handle : allWindowHandles) {
            if (!handle.equals(originalWindowHandle)) {
                payuWindowHandle = handle;
                break;
            }
        }

        if (payuWindowHandle != null) {
            driver.switchTo().window(payuWindowHandle);
            System.out.println("Switched to PayU tab. Current URL: " + driver.getCurrentUrl());

            System.out.println("\n--- Starting PayU Page Validations ---");

            try {
                String payuPageTitle = driver.getTitle();
                System.out.println("PayU Page Title: " + payuPageTitle);

                Thread.sleep(2000);
                WebElement cardsOption = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//div[@class='l1Item__content']/p[text()='Cards (Credit/Debit)']")));
                Assert.assertTrue(cardsOption.isDisplayed(), "Cards (Credit/Debit) option not displayed.");
                System.out.println("Validation: 'Cards (Credit/Debit)' option is displayed.");

                driver.findElement(By.xpath("//div[@class='l1Item__content']/p[text()='Cards (Credit/Debit)']")).click();

                driver.findElement(By.xpath("//input[@data-testid='cardNumber']")).click();
                driver.findElement(By.xpath("//input[@data-testid='cardNumber']")).sendKeys("5123456789012346");

                WebElement cardexpiry = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//input[@id='cardExpiry']")));
                cardexpiry.click();
                cardexpiry.sendKeys("1225");
                driver.findElement(By.xpath("//input[@id='cardCvv']")).sendKeys("123");
                driver.findElement(By.xpath("//input[@name='cardOwnerName']")).sendKeys("Arun kumar");
                Thread.sleep(1000);

                driver.findElement(By.xpath(" //span[contains(text(), 'PROCEED')]")).click();
                Thread.sleep(1000);
                driver.findElement(By.xpath(" //button[text()='Save and Continue']")).click();

                // These lines are redundant as options are set before driver initialization
                // options.addArguments("--disable-notifications");
                // options.addArguments("--disable-infobars");
                // options.addArguments("--disable-extensions");

                WebElement code = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//*[@id='password']")));
                code.sendKeys("123456");
                // options.addArguments("--disable-notifications"); // Redundant here

                driver.findElement(By.xpath("//input[@id='submitBtn']")).click();


            } catch (Exception e) {
                System.err.println("Error during PayU page validation: " + e.getMessage());
                e.printStackTrace();
                Assert.fail("PayU page validation failed: " + e.getMessage());
            }

            Thread.sleep(3000);

            // Close the current PayU tab
            driver.close();
            System.out.println("Closed PayU tab.");

            // Return to the original tab
            driver.switchTo().window(originalWindowHandle);

            Thread.sleep(7000);
            takeScreenshot(driver, "Quickpaysuccesspage.png");
            WebElement login = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//flt-semantics[@role='button' and text()='Go to Login']")));
            System.out.println("Back on main application tab, 'Go to Login' button visible.");

        } else {
            System.err.println("Could not find the new PayU window handle. Test Failed.");
            Assert.fail("Failed to switch to PayU window.");
        }


        // ***************************************************************
        // *** Start Database Validation for TB_ABCO_PAYMENTS here ***
        // ***************************************************************

        Connection connection = null;
        Statement statement = null;
        ResultSet resultSet = null;

        try {
            System.out.println("\n--- Starting Database Validation for Payments Table ---");
            connection = DriverManager.getConnection(DB_URL, DB_USERNAME, DB_PASSWORD);
            System.out.println("Database connection established successfully for payments table.");

            statement = connection.createStatement();

            // Get current time minus a few minutes to check for recent entries
            // Adjust the 'minusMinutes' value based on how quickly the DB entry is expected
            LocalDateTime now = LocalDateTime.now();
            LocalDateTime timeThreshold = now.minusMinutes(5); // Check for entries within the last 5 minutes
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            String formattedTimeThreshold = timeThreshold.format(formatter);

            // Construct the SQL query to find the payment record
            // We use MOBILE_NUM and CREATED_TS to identify the unique transaction.
            String sqlQuery = String.format(
                    "SELECT * FROM %s WHERE MOBILE_NUM = '%s' AND PAYMENT_AMT = %s AND CREATED_TS >= '%s' ORDER BY CREATED_TS DESC;",
                    PAYMENTS_TABLE_NAME,
                    TEST_MOBILE_NUMBER,
                    TEST_PAYMENT_AMOUNT, // Make sure this is a numeric value in DB or handle as string if CHAR/VARCHAR
                    formattedTimeThreshold
            );

            System.out.println("Executing SQL Query: " + sqlQuery);

            resultSet = statement.executeQuery(sqlQuery);

            if (resultSet.next()) {
                System.out.println("DB Validation: Found a recent payment entry for mobile number " + TEST_MOBILE_NUMBER);

                // *** RETRIEVE AND CHECK ALL REQUIRED FIELDS ***
                System.out.println("\n--- Retrieved Payment Data ---");

                // Retrieve all fields as String first, then parse numeric ones
                String appId = resultSet.getString("APP_ID");
                String paymentId = resultSet.getString("PAYMENT_ID");
                String regMobileNum = resultSet.getString("REG_MOBILE_NUM");
                String mobileNum = resultSet.getString("MOBILE_NUM");
                String emailId = resultSet.getString("EMAIL_ID");
                String channelId = resultSet.getString("CHANNEL_ID");
                String initiatedFrom = resultSet.getString("INITIATED_FROM");
                String paymentDt = resultSet.getString("PAYMENT_DT");

                // Handle numeric fields by getting as String and then parsing
                String paymentAmtStr = resultSet.getString("PAYMENT_AMT");
                double paymentAmt = 0.0;
                if (paymentAmtStr != null && !paymentAmtStr.trim().isEmpty()) {
                    try {
                        paymentAmt = Double.parseDouble(paymentAmtStr);
                    } catch (NumberFormatException e) {
                        System.err.println("Error parsing PAYMENT_AMT '" + paymentAmtStr + "' to Double: " + e.getMessage());
                        Assert.fail("Failed to parse PAYMENT_AMT to Double.");
                    }
                } else {
                    System.out.println("Warning: PAYMENT_AMT is null or empty, defaulting to 0.0.");
                }

                String paymentCcy = resultSet.getString("PAYMENT_CCY");
                String accountType = resultSet.getString("ACCOUNT_TYPE");
                String accountNo = resultSet.getString("ACCOUNT_NO");
                String productType = resultSet.getString("PRODUCT_TYPE");
                String merchantId = resultSet.getString("MERCHANT_ID");
                String paymentStatus = resultSet.getString("PAYMENT_STATUS");
                String pgFailureReason = resultSet.getString("PG_FAILURE_REASON");
                String paymentRefId = resultSet.getString("PAYMENT_REF_ID");
                String paymentMode = resultSet.getString("PAYMENT_MODE");
                String backendRefId = resultSet.getString("BACKEND_REF_ID");

                String emiAmountStr = resultSet.getString("EMI_AMOUNT");
                double emiAmount = 0.0;
                if (emiAmountStr != null && !emiAmountStr.trim().isEmpty()) {
                    try {
                        emiAmount = Double.parseDouble(emiAmountStr);
                    } catch (NumberFormatException e) {
                        System.err.println("Error parsing EMI_AMOUNT '" + emiAmountStr + "' to Double: " + e.getMessage());
                        Assert.fail("Failed to parse EMI_AMOUNT to Double.");
                    }
                } else {
                    System.out.println("Warning: EMI_AMOUNT is null or empty, defaulting to 0.0.");
                }

                String overdueAmountStr = resultSet.getString("OVERDUE_AMOUNT");
                double overdueAmount = 0.0;
                if (overdueAmountStr != null && !overdueAmountStr.trim().isEmpty()) {
                    try {
                        overdueAmount = Double.parseDouble(overdueAmountStr);
                    } catch (NumberFormatException e) {
                        System.err.println("Error parsing OVERDUE_AMOUNT '" + overdueAmountStr + "' to Double: " + e.getMessage());
                        Assert.fail("Failed to parse OVERDUE_AMOUNT to Double.");
                    }
                } else {
                    System.out.println("Warning: OVERDUE_AMOUNT is null or empty, defaulting to 0.0.");
                }

                String overdueChrgsStr = resultSet.getString("OVERDUE_CHRGS");
                double overdueChrgs = 0.0;
                if (overdueChrgsStr != null && !overdueChrgsStr.trim().isEmpty()) {
                    try {
                        overdueChrgs = Double.parseDouble(overdueChrgsStr);
                    } catch (NumberFormatException e) {
                        System.err.println("Error parsing OVERDUE_CHRGS '" + overdueChrgsStr + "' to Double: " + e.getMessage());
                        Assert.fail("Failed to parse OVERDUE_CHRGS to Double.");
                    }
                } else {
                    System.out.println("Warning: OVERDUE_CHRGS is null or empty, defaulting to 0.0.");
                }

                String chqBounceChrgsStr = resultSet.getString("CHQ_BOUNCE_CHRGS");
                double chqBounceChrgs = 0.0;
                if (chqBounceChrgsStr != null && !chqBounceChrgsStr.trim().isEmpty()) {
                    try {
                        chqBounceChrgs = Double.parseDouble(chqBounceChrgsStr);
                    } catch (NumberFormatException e) {
                        System.err.println("Error parsing CHQ_BOUNCE_CHRGS '" + chqBounceChrgsStr + "' to Double: " + e.getMessage());
                        Assert.fail("Failed to parse CHQ_BOUNCE_CHRGS to Double.");
                    }
                } else {
                    System.out.println("Warning: CHQ_BOUNCE_CHRGS is null or empty, defaulting to 0.0.");
                }

                String createdTs = resultSet.getString("CREATED_TS");
                String createdBy = resultSet.getString("CREATED_BY");
                String updatedTs = resultSet.getString("UPDATED_TS");
                String updateMode = resultSet.getString("UPDATE_MODE");
                String addnDebitInfo = resultSet.getString("ADDN_DEBIT_INFO");
                String addnCreditInfo = resultSet.getString("ADDN_CREDIT_INFO");
                String notiFlag = resultSet.getString("NOTI_FLAG");
                String deviceId = resultSet.getString("DEVICE_ID");
                String customerName = resultSet.getString("CUSTOMER_NAME");
                String payuStatus = resultSet.getString("PAYU_STATUS");
                String nocDownload = resultSet.getString("NOC_DOWNLOAD");
                String paymentType = resultSet.getString("PAYMENT_TYPE");
                String orderId = resultSet.getString("ORDER_ID");
                String paymentGateway = resultSet.getString("PAYMENT_GATEWAY");
                String isS2S = resultSet.getString("IS_S2S");
                String payuHash = resultSet.getString("PAYU_HASH");
                String reverseHash = resultSet.getString("REVERSE_HASH");
                String hashFields = resultSet.getString("HASH_FIELDS");
                String updatedTs1 = resultSet.getString("updated_ts1"); // Note: Column name is lowercase here
                String createdTs1 = resultSet.getString("created_ts1"); // Note: Column name is lowercase here

                String amcChrgsStr = resultSet.getString("AMC_CHRGS");
                double amcChrgs = 0.0;
                if (amcChrgsStr != null && !amcChrgsStr.trim().isEmpty()) {
                    try {
                        amcChrgs = Double.parseDouble(amcChrgsStr);
                    } catch (NumberFormatException e) {
                        System.err.println("Error parsing AMC_CHRGS '" + amcChrgsStr + "' to Double: " + e.getMessage());
                        Assert.fail("Failed to parse AMC_CHRGS to Double.");
                    }
                } else {
                    System.out.println("Warning: AMC_CHRGS is null or empty, defaulting to 0.0.");
                }

                String partPaymentEffect = resultSet.getString("PART_PAYMENT_EFFECT");
                String partPaymentSource = resultSet.getString("PART_PAYMENT_SOURCE");


                // Print all retrieved values
                System.out.println("APP_ID: " + appId);
                System.out.println("PAYMENT_ID: " + paymentId);
                System.out.println("REG_MOBILE_NUM: " + regMobileNum);
                System.out.println("MOBILE_NUM: " + mobileNum);
                System.out.println("EMAIL_ID: " + emailId);
                System.out.println("CHANNEL_ID: " + channelId);
                System.out.println("INITIATED_FROM: " + initiatedFrom);
                System.out.println("PAYMENT_DT: " + paymentDt);
                System.out.println("PAYMENT_AMT: " + paymentAmt);
                System.out.println("PAYMENT_CCY: " + paymentCcy);
                System.out.println("ACCOUNT_TYPE: " + accountType);
                System.out.println("ACCOUNT_NO: " + accountNo);
                System.out.println("PRODUCT_TYPE: " + productType);
                System.out.println("MERCHANT_ID: " + merchantId);
                System.out.println("PAYMENT_STATUS: " + paymentStatus);
                System.out.println("PG_FAILURE_REASON: " + pgFailureReason);
                System.out.println("PAYMENT_REF_ID: " + paymentRefId);
                System.out.println("PAYMENT_MODE: " + paymentMode);
                System.out.println("BACKEND_REF_ID: " + backendRefId);
                System.out.println("EMI_AMOUNT: " + emiAmount);
                System.out.println("OVERDUE_AMOUNT: " + overdueAmount);
                System.out.println("OVERDUE_CHRGS: " + overdueChrgs);
                System.out.println("CHQ_BOUNCE_CHRGS: " + chqBounceChrgs);
                System.out.println("CREATED_TS: " + createdTs);
                System.out.println("CREATED_BY: " + createdBy);
                System.out.println("UPDATED_TS: " + updatedTs);
                System.out.println("UPDATE_MODE: " + updateMode);
                System.out.println("ADDN_DEBIT_INFO: " + addnDebitInfo);
                System.out.println("ADDN_CREDIT_INFO: " + addnCreditInfo);
                System.out.println("NOTI_FLAG: " + notiFlag);
                System.out.println("DEVICE_ID: " + deviceId);
                System.out.println("CUSTOMER_NAME: " + customerName);
                System.out.println("PAYU_STATUS: " + payuStatus);
                System.out.println("NOC_DOWNLOAD: " + nocDownload);
                System.out.println("PAYMENT_TYPE: " + paymentType);
                System.out.println("ORDER_ID: " + orderId);
                System.out.println("PAYMENT_GATEWAY: " + paymentGateway);
                System.out.println("IS_S2S: " + isS2S);
                System.out.println("PAYU_HASH: " + payuHash);
                System.out.println("REVERSE_HASH: " + reverseHash);
                System.out.println("HASH_FIELDS: " + hashFields);
                System.out.println("updated_ts1: " + updatedTs1);
                System.out.println("created_ts1: " + createdTs1);
                System.out.println("AMC_CHRGS: " + amcChrgs);
                System.out.println("PART_PAYMENT_EFFECT: " + partPaymentEffect);
                System.out.println("PART_PAYMENT_SOURCE: " + partPaymentSource);


                // --- Add Assertions for these fields based on your test requirements ---
                // Example Assertions:
                Assert.assertEquals(regMobileNum, TEST_MOBILE_NUMBER, "REG_MOBILE_NUM mismatch");
                Assert.assertEquals(paymentAmt, Double.parseDouble(TEST_PAYMENT_AMOUNT), "PAYMENT_AMT mismatch");
                Assert.assertEquals(paymentStatus, "SUCCESS", "Payment Status should be SUCCESS"); // Example
                Assert.assertNotNull(paymentId, "PAYMENT_ID should not be null");
                Assert.assertFalse(paymentId.isEmpty(), "PAYMENT_ID should not be empty");
                // Assert.assertEquals(channelId, "WEB", "CHANNEL_ID mismatch"); // Adjust as per your expected value
                // Assert.assertEquals(initiatedFrom, "QP", "INITIATED_FROM mismatch for QuickPay"); // Adjust as per your expected value
                // Assert.assertEquals(paymentMode, "CARD", "PAYMENT_MODE mismatch");
                // Assert.assertEquals(paymentGateway, "PAYU", "PAYMENT_GATEWAY mismatch");


                Assert.assertTrue(true, "Database entry found and key fields validated for the payment.");
            } else {
                System.out.println("DB Validation: No recent payment entry found for mobile number " + TEST_MOBILE_NUMBER + " and amount " + TEST_PAYMENT_AMOUNT);
                Assert.fail("Database payment entry NOT found within the last 5 minutes.");
            }

        } catch (SQLException e) {
            System.err.println("Database error occurred during payments validation: " + e.getMessage());
            Assert.fail("Database connection or query failed for payments table: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("An unexpected error occurred during payments DB validation: " + e.getMessage());
            e.printStackTrace();
            Assert.fail("An unexpected error occurred during payments DB validation: " + e.getMessage());
        } finally {
            try {
                if (resultSet != null) resultSet.close();
                if (statement != null) statement.close();
                if (connection != null) connection.close();
                System.out.println("Database resources closed for payments validation.");
            } catch (SQLException e) {
                System.err.println("Error closing database resources for payments: " + e.getMessage());
            }
        }
        // ***************************************************************
        // *** End Database Validation for TB_ABCO_PAYMENTS here ***
        // ***************************************************************

        // Uncomment if you want to close browser at the end of the test
        // driver.quit();
    }


    private void scrollAndClickElement(By xpath, String s) {
        // This method was not fully implemented in your original code.
        // If you need it, you would add scrolling logic here, e.g.:
        // JavascriptExecutor js = (JavascriptExecutor) driver;
        // WebElement element = driver.findElement(xpath);
        // js.executeScript("arguments[0].scrollIntoView(true);", element);
        // element.click();
    }

    private void takeScreenshot(WebDriver driver, String Quickpaysucesspage) {
        try {
            if (driver instanceof TakesScreenshot) {
                File screenshotFile = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
                File screenshotDir = new File("./screenshots");
                if (!screenshotDir.exists()) {
                    screenshotDir.mkdirs();
                }
                File destinationFile = new File(screenshotDir, Quickpaysucesspage);
                FileUtils.copyFile(screenshotFile, destinationFile);
                System.out.println("Screenshot saved to: " + destinationFile.getAbsolutePath());
            } else {
                System.err.println("WebDriver does not support taking screenshots.");
            }
        } catch (IOException e) {
            System.err.println("Failed to take or save screenshot '" + Quickpaysucesspage + "': " + e.getMessage());
            e.printStackTrace();
        }
    }

    private String getOtpFromLocalLog(String localLogFilePath) throws IOException {
        String otp = null;
        Pattern pattern = Pattern.compile("OTP is :\\s*(\\d+)");

        try (BufferedReader reader = new BufferedReader(new FileReader(localLogFilePath))) {
            String line;
            String lastFoundOtp = null;
            while ((line = reader.readLine()) != null) {
                Matcher matcher = pattern.matcher(line);
                if (matcher.find()) {
                    lastFoundOtp = matcher.group(1);
                }
            }
            otp = lastFoundOtp;
        }
        return otp;
    }

    private void downloadLogFileFromRemoteServer(
            String host, int port, String username, String password, String remoteFilePath, String localSavePath) throws Exception {

        Session session = null;
        ChannelSftp channelSftp = null;
        try {
            JSch jsch = new JSch();
            session = jsch.getSession(username, host, port);
            session.setPassword(password);

            Properties config = new Properties();
            config.put("StrictHostKeyChecking", "no");
            session.setConfig(config);

            session.connect();
            System.out.println("SSH Session connected to: " + host + " on port " + port + " with user " + username);

            channelSftp = (ChannelSftp) session.openChannel("sftp");
            channelSftp.connect();
            System.out.println("SFTP Channel connected.");

            channelSftp.get(remoteFilePath, localSavePath);
            System.out.println("File downloaded from " + remoteFilePath + " to " + localSavePath);

        } finally {
            if (channelSftp != null && channelSftp.isConnected()) {
                channelSftp.disconnect();
                System.out.println("SFTP Channel disconnected.");
            }
            if (session != null && session.isConnected()) {
                session.disconnect();
                System.out.println("SSH Session disconnected.");
            }
        }
    }
}
