import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;

import java.util.concurrent.TimeUnit;

import static org.junit.Assert.fail;

public class ChallengeTest {
    private WebDriver driver;
    private String baseUrl;
    private boolean acceptNextAlert = true;
    private StringBuffer verificationErrors = new StringBuffer();

    @Before
    public void setUp() throws Exception {
        System.setProperty("webdriver.chrome.driver", "lib\\chromedriver.exe");
        driver = new ChromeDriver();
        baseUrl = "http://localhost:8080/";
        driver.manage().timeouts().implicitlyWait(30, TimeUnit.SECONDS);
    }

    @Test
    public void testCreateNewChallengeWithBadDate() throws Exception {
        driver.get(baseUrl);
        driver.findElement(By.cssSelector("button.btn.btn-default")).click();
        driver.findElement(By.xpath("//div[4]/a/i")).click();
        driver.findElement(By.id("login_field")).clear();
        driver.findElement(By.id("login_field")).sendKeys("saenghwal");
        driver.findElement(By.id("password")).clear();
        driver.findElement(By.id("password")).sendKeys("bill102030");
        driver.findElement(By.name("commit")).click();

        Thread.sleep(2000);

        boolean clicked = false;
        do{
            try {
                WebElement element = driver.findElement(By.name("submit3"));
                element.click();
            } catch (WebDriverException e) {
                continue;
            } finally {
                clicked = true;
            }
        } while (!clicked);

        driver.findElement(By.id("name")).clear();
        driver.findElement(By.id("name")).sendKeys("Make a picture of your cat");
        driver.findElement(By.id("description")).clear();
        driver.findElement(By.id("description")).sendKeys("Take your cat and make a selfie");
        driver.findElement(By.id("date")).clear();
        driver.findElement(By.id("date")).sendKeys("20/02/2017 10:23");
        driver.findElement(By.name("k")).click();
    }

    @After
    public void tearDown() throws Exception {
        driver.quit();
        String verificationErrorString = verificationErrors.toString();
        if (!"".equals(verificationErrorString)) {
            fail(verificationErrorString);
        }
    }

    private boolean isElementPresent(By by) {
        try {
            driver.findElement(by);
            return true;
        } catch (NoSuchElementException e) {
            return false;
        }
    }

    private boolean isAlertPresent() {
        try {
            driver.switchTo().alert();
            return true;
        } catch (NoAlertPresentException e) {
            return false;
        }
    }

    private String closeAlertAndGetItsText() {
        try {
            Alert alert = driver.switchTo().alert();
            String alertText = alert.getText();
            if (acceptNextAlert) {
                alert.accept();
            } else {
                alert.dismiss();
            }
            return alertText;
        } finally {
            acceptNextAlert = true;
        }
    }
}

