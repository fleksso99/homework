package max.homework;

import io.github.bonigarcia.wdm.WebDriverManager;
import java.time.Duration;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeTest;

public class BasePage {

  static WebDriver driver;

  static {
    WebDriverManager.chromedriver().setup();
  }

  public void type(String xPath, String text) {
    waitForVisibility(xPath, 10);
    WebElement element = driver.findElement(By.xpath(xPath));
    element.clear();
    element.sendKeys(text);
  }

  public void click(String xPath) {
    waitForVisibility(xPath, 10);
    try {
      WebElement element = driver.findElement(By.xpath(xPath));
      element.click();
    } catch (Exception e) {
      Assert.fail("element not present: " + xPath);
    }
  }

  public void fluentWait(int milliseconds) {
    try {
      Thread.sleep(milliseconds); // I know thread sleep is bad, need to find better sleep method
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
  }

  public void waitForVisibility(String xPath, int seconds) {
    WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(seconds));
    wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath(xPath)));
  }

  public String getTextFrom(String xPath) {
    WebElement element = driver.findElement(By.xpath(xPath));
    return element.getText();
  }

  public String getValueFromAttribute(String xPath, String attribute) {
    WebElement element = driver.findElement(By.xpath(xPath));
    return element.getAttribute(attribute);
  }

  @BeforeTest
  void beforeTest() {

  }

  @AfterTest
  void afterTest() {
    if (driver != null) {
      driver.close();
    }
  }

  void openPage(String baseUrl) {
    driver = new ChromeDriver();
    driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));
    driver.get(baseUrl);
  }
}
