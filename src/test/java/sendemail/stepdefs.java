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
import java.util.UUID;

import com.sun.glass.events.KeyEvent;

import static org.junit.jupiter.api.Assertions.*;
import static sendemail.util.TestUtils.*;

public class stepdefs implements En {

   private WebDriver driver;
   private final String path = System.getProperty("user.dir");
   private String imageAttachmentName;

   // Identification information to log into the email
   private final String loginEmail = "mcgill.chungus";
   private final String loginPass = ",bG7=n|e}+]:";

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
         // Open the chrome driver
         driver = new ChromeDriver();
         driver.manage().window().maximize();
      });

      And("^login to gmail", () -> {
         // Navigate to gmail
         driver.navigate().to("https://www.google.com/gmail/");
         waitForPageLoaded(driver);

         // Find the identification textbox and write login
         getWaitOnElement(driver, By.id("identifierId")).sendKeys(loginEmail);

         // Click on the next button
         driver.findElement(By.id("identifierNext")).click();

         // Find the password textbox and wait till we can interact with it
         WebElement passwordTextBox = getWaitOnElement(driver, By.name("password"));
         waitUntilElementClickableAndClick(driver, passwordTextBox);

         // Write the password
         passwordTextBox.sendKeys(loginPass);

         // Click on the next button
         driver.findElement(By.id("passwordNext")).click();
      });

      When("^I compose an email$", () -> {
         // Click on the compose button
         getWaitOnElement(driver, By.cssSelector(".z0 > div")).click();
      });

      And("^enter a valid email as \"([^\"]*)\"$", (String email) -> {
         // Write the recipient email
         getWaitOnElement(driver, By.name("to")).sendKeys(email);
         driver.findElement(By.className("aoT")).sendKeys(subjectLine);
         driver.findElement(By.cssSelector(".Am.Al.editable.LW-avf")).sendKeys(emailText);
      });

      And("^attach an image as \"([^\"]*)\"$", (String imgName) -> {
         imageAttachmentName = imgName;

         // Click on the add attachment button
         driver.findElement(By.cssSelector(".wG.J-Z-I")).click();

         // Wait till the file explorer pops up
         Thread.sleep(4000);

         // Construct the filepath to the attachment
         String text = path+"\\assets\\" + imgName;

         // Put the image path in the clipboard
         StringSelection stringSelection = new StringSelection(text);
         Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
         clipboard.setContents(stringSelection, stringSelection);

         // Paste the path and hit enter
         Robot robot = new Robot();
         robot.keyPress(KeyEvent.VK_CONTROL);
         robot.keyPress(KeyEvent.VK_V);
         robot.keyRelease(KeyEvent.VK_V);
         robot.keyRelease(KeyEvent.VK_CONTROL);
         robot.keyPress(KeyEvent.VK_ENTER);
      });

      And("^send$", () -> {
         // wait for attachment to finish loading
          getWaitOnElement(driver, By.partialLinkText(imageAttachmentName));

          // Click on the send button
          getWaitOnElement(driver, By.cssSelector(".T-I.J-J5-Ji.aoO.T-I-atl.L3")).click();
      });
      
      Then("^the email should be sent successfully$", () -> {

         // Make sure that the pop up indicating that the email was sent shows up
         assertSentPopUp(driver);

         // Make sure that the email that was just sent can be found in the sent folder
         assertSentFolder(driver);

      });
   }

   /**
    * Wait for the pop up that confirms that the email was sent appears.
    * @param driver  Web driver
    */
   private void assertSentPopUp(WebDriver driver){
      try {
         getWaitOnElement(driver, By.cssSelector(".vh"));
      } catch (TimeoutException e){
         fail("The pop up that confirms the email was sent did not appear");
      }
   }

   /**
    * Navigate to the 'sent' folder, find the email with the body corresponding to what was sent and confirms
    * that the attachment is present
    * @param driver  Web Driver
    */
   private void assertSentFolder(WebDriver driver) {

      // Navigate to the sent folder
      driver.navigate().to("https://mail.google.com/mail/#sent");
      waitForPageLoaded(driver);

      // Make sure that the 'No sent messages! Send one now!' is not present
      //TODO;

      // Find the email with the corresponding subject line
      WebElement emailInInbox = getWaitOnElementWithText(driver, By.cssSelector(".y2"), emailText);

      // If the email we sent is not found, something went wrong
      if (emailInInbox == null) {
         fail("The email could not be found in the 'Sent' folder");
      }

      // Click on the email to confirm that the attachment is there
      emailInInbox.click();

      // Find the image attachment
      WebElement attachment = getWaitOnElementWithText(driver, By.cssSelector(".brg"), imageAttachmentName);

      if (attachment == null) {
         fail("The attachment sent could not be found in the email");
      }

   }


}
