package sendemail;

import static org.junit.jupiter.api.Assertions.assertEquals;


import org.openqa.selenium.By;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import cucumber.api.java8.En;

import java.awt.Robot;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Clipboard;
import java.awt.Toolkit;

import com.sun.glass.events.KeyEvent;

public class stepdefs implements En {

   private WebDriver driver;
   private final String path = System.getProperty("user.dir");
   private String imageAttachmentName;


   public stepdefs() {
      Before(() ->{
         System.setProperty("webdriver.chrome.driver",path+"\\chromedriver\\chromedriver.exe");
      });

      After(() -> {
         driver.close();
      });

      Given("^Open Google Chrome$", () -> {
         driver = new ChromeDriver();
         driver.manage().window().maximize();
      });

      And("^login to gmail", () -> {
         driver.navigate().to("https://www.google.com/gmail/");
         getWaitOnElement(driver, By.id("identifierId")).sendKeys("mcgill.chungus");
         driver.findElement(By.id("identifierNext")).click();
         getWaitOnElement(driver, By.name("password")).sendKeys(",bG7=n|e}+]:");
         driver.findElement(By.id("passwordNext")).click();
      });

      When("^I compose an email$", () -> {
         getWaitOnElement(driver, By.cssSelector(".z0 > div")).click();
      });

      And("^enter a valid email as \"([^\"]*)\"$", (String email) -> {
         getWaitOnElement(driver, By.name("to")).sendKeys(email);
         driver.findElement(By.className("aoT")).sendKeys("from gherk");
         driver.findElement(By.cssSelector(".Am.Al.editable.LW-avf")).sendKeys("yours truly");
      });

      And("^attach an image as \"([^\"]*)\"$", (String imgName) -> {
         imageAttachmentName = imgName;
         driver.findElement(By.cssSelector(".wG.J-Z-I")).click();
         Thread.sleep(7000);
         String text = path+"\\assets\\" + imgName;
         StringSelection stringSelection = new StringSelection(text);
         Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
         clipboard.setContents(stringSelection, stringSelection);

         Robot robot = new Robot();
         robot.keyPress(KeyEvent.VK_CONTROL);
         robot.keyPress(KeyEvent.VK_V);
         robot.keyRelease(KeyEvent.VK_V);
         robot.keyRelease(KeyEvent.VK_CONTROL);
         robot.keyPress(KeyEvent.VK_ENTER);
      });

      And("^send$", () -> {
         //wait for attachment to finish loading
          getWaitOnElement(driver, By.partialLinkText(imageAttachmentName));
          
          getWaitOnElement(driver, By.cssSelector(".T-I.J-J5-Ji.aoO.T-I-atl.L3")).click();
      });
      
      Then("^the email should be sent successfully$", () -> {
         assertEquals(true, checkEmailSent(driver));
      });
   }

   private WebElement getWaitOnElement(WebDriver driver, By selector ){
      return (new WebDriverWait(driver, 10)).until(ExpectedConditions.presenceOfElementLocated(selector));
   }

   private boolean checkEmailSent(WebDriver driver){
      try {
         getWaitOnElement(driver, By.cssSelector(".vh"));
      } catch (TimeoutException e){
         return false;
      }
      return true;
   }
}
