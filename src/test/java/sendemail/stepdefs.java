package sendemail;


import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import cucumber.api.java8.En;

import java.awt.Robot;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Clipboard;
import java.awt.Toolkit;
import java.util.List;
import java.util.UUID;

import com.sun.glass.events.KeyEvent;

import static org.junit.jupiter.api.Assertions.*;

public class stepdefs implements En {

   private WebDriver driver;
   private final String path = System.getProperty("user.dir");
   private String imageAttachmentName;

   private final String subjectLine = "Email test, please find attached doggo";
   private String emailText;


   public stepdefs() {
      Before(() ->{
         // Set the path to the chrome driver
         System.setProperty("webdriver.chrome.driver",path+"\\chromedriver\\chromedriver.exe");

         // Generate a random string for the subject line of the test
         emailText = UUID.randomUUID().toString();
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
         waitForPageLoaded();
         getWaitOnElement(driver, By.id("identifierId")).sendKeys("mcgill.chungus");
         driver.findElement(By.id("identifierNext")).click();
         WebElement passwordTextBox = getWaitOnElement(driver, By.name("password"));
         waitUntilElementClickableAndClick(passwordTextBox);
         passwordTextBox.sendKeys(",bG7=n|e}+]:");

         driver.findElement(By.id("passwordNext")).click();
      });

      When("^I compose an email$", () -> {
         getWaitOnElement(driver, By.cssSelector(".z0 > div")).click();
      });

      And("^enter a valid email as \"([^\"]*)\"$", (String email) -> {
         getWaitOnElement(driver, By.name("to")).sendKeys(email);
         driver.findElement(By.className("aoT")).sendKeys(subjectLine);
         driver.findElement(By.cssSelector(".Am.Al.editable.LW-avf")).sendKeys(emailText);
      });

      And("^attach an image as \"([^\"]*)\"$", (String imgName) -> {
         imageAttachmentName = imgName;
         driver.findElement(By.cssSelector(".wG.J-Z-I")).click();
         Thread.sleep(4000);
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

         // Make sure that the pop up indicating that the email was sent shows up
         assertSentPopUp(driver);

         // Make sure that the email that was just sent can be found in the sent folder
         assertSentFolder(driver);

      });
   }

   private void assertSentPopUp(WebDriver driver){
      try {
         getWaitOnElement(driver, By.cssSelector(".vh"));
      } catch (TimeoutException e){
         fail("The pop up that confirms the email was sent did not appear");
      }
   }

   private void assertSentFolder(WebDriver driver) {

      // Navigate to the sent folder
      driver.navigate().to("https://mail.google.com/mail/#sent");
      waitForPageLoaded();

      // Make sure that the 'No sent messages! Send one now!' is not present


      // Find the email with the corresponding subject line
      WebElement emailInInbox = getWaitOnElementWithText(By.cssSelector(".y2"), emailText);

      // If the email we sent is not found, something went wrong
      if (emailInInbox == null) {
         fail("The email could not be found in the 'Sent' folder");
      }

      // Click on the email to confirm that the attachment is there
      emailInInbox.click();

      // Find the image attachment
      WebElement attachment = getWaitOnElementWithText(By.cssSelector(".brg"), imageAttachmentName);

      if (attachment == null) {
         fail("The attachment sent could not be found in the email");
      }

   }

   private WebElement getWaitOnElement(WebDriver driver, By selector ){
      return (new WebDriverWait(driver, 10)).until(ExpectedConditions.presenceOfElementLocated(selector));
   }

   private WebElement getWaitOnElementWithText(By selector, String text) {
      long now = System.currentTimeMillis();
      while (System.currentTimeMillis() - now < 10000) {

         List<WebElement> unreadEmailsSubjectLine = driver.findElements(selector);

         for (WebElement element : unreadEmailsSubjectLine) {
            // Find the element with the subject line we sent
            if (element.getText().contains(text)) {
               return element;
            }
         }
      }

      return null;
   }

   private void waitUntilElementClickableAndClick(WebElement element) {
      (new WebDriverWait(driver, 10)).until(ExpectedConditions.elementToBeClickable(element));
      element.click();
   }

   private void waitForPageLoaded() {
      ExpectedCondition<Boolean> expectation = new
              ExpectedCondition<Boolean>() {
                 public Boolean apply(WebDriver driver) {
                    return ((JavascriptExecutor) driver).executeScript("return document.readyState").toString().equals("complete");
                 }
              };
      try {
         Thread.sleep(500);
         WebDriverWait wait = new WebDriverWait(driver, 30);
         wait.until(expectation);
      } catch (Throwable error) {
         fail("Page not loaded in time");
      }
   }
}
