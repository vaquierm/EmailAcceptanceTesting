package sendemail;


import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.interactions.Actions;

import cucumber.api.java8.En;
import sendemail.util.TestUtils;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static sendemail.util.TestUtils.*;

public class SendEmailWithImageSteps implements En {

   private WebDriver driver = null;
   private String imageAttachmentName;

   // Identification information to log into the email
   private final String loginEmail = "mcgill.chungus";
   private final String loginPass = ",bG7=n|e}+]:";

   private String uniqueText;

   private String sysPath = // Set the path to the chrome driver
           System.setProperty("webdriver.chrome.driver",TestUtils.getPathName("chromedriver", "chromedriver.exe"));


   public SendEmailWithImageSteps() {
      //check initial state before each test
      Before(() ->{
         //check if no open Chrome window exists
         assertNull(driver);
         // Generate a random string for the subject line of the test
         uniqueText = UUID.randomUUID().toString();
      });

      //return state to initial state
      After(() -> {
         driver.quit();
         driver = null;
      });

      Given("^Open Google Chrome$", () -> {
         // Open the chrome driver
         driver = new ChromeDriver();
         driver.manage().window().maximize();
      });

      And("^login to gmail", () -> {
         // Navigate to gmail
         driver.navigate().to("https://www.google.com/gmail/");

         // Find the identification textbox and wait till we can interact with it
         WebElement usernameBox = getWaitOnElement(driver, By.id("identifierId"));
         waitUntilElementClickable(driver, usernameBox);

         //write email
         usernameBox.sendKeys(loginEmail);

         // Click on the next button
         driver.findElement(By.id("identifierNext")).click();

         // Find the password textbox and wait till we can interact with it
         WebElement passwordTextBox = getWaitOnElement(driver, By.name("password"));
         waitUntilElementClickable(driver, passwordTextBox);

         // Write the password
         passwordTextBox.sendKeys(loginPass);

         // Click on the next button
         driver.findElement(By.id("passwordNext")).click();
      });

      When("^I compose an email$", () -> {
         // Click on the compose button
         getWaitOnElement(driver, By.xpath("//*[text() = 'Compose']")).click();
      });

      And("^enter an email as \"([^\"]*)\"$", (String email) -> {
         // Write the recipient email
         getWaitOnElement(driver, By.name("to")).sendKeys(email);
      });

      And("^enter a email subject$", () -> {
         // Write the subject line
         driver.findElement(By.className("aoT")).sendKeys(uniqueText);
      });

      And("^enter a body to the email$", () -> {
         // Write the email text
         driver.findElement(By.cssSelector(".Am.Al.editable.LW-avf")).sendKeys(uniqueText);
      });

      And("^attach an image as \"([^\"]*)\"$", (String imgName) -> {
         imageAttachmentName = imgName;

         // Construct the filepath to the attachment
         String text = getPathName("assets", imgName);

         //attach image
         WebElement attachFile = getWaitOnElement(driver,By.name("Filedata"));
         attachFile.sendKeys(text);
      });

      And("^send$", () -> {
         // wait for attachment to finish loading
          getWaitOnElement(driver, By.partialLinkText(imageAttachmentName));

          // Click on the send button
          getWaitOnElement(driver, By.cssSelector(".T-I.J-J5-Ji.aoO.T-I-atl.L3")).click();
      });


      Then("^The 'Message Sent' popup appears$", () -> {
         // Make sure that the pop up indicating that the email was sent shows up
         assertSentPopUp(driver);
      });

      And("^The email can be found in the 'Sent' tab$", () -> {
         // Make sure that the email that was just sent can be found in the sent folder
         assertSentFolder(driver);
      });
      And("^confirm sending the email without a subject line or body$", () -> {
         // Wait for the pop up to show and accept
         checkAndConfirmAlert(driver);
      });
      And("^I navigate to my empty 'Sent' folder$", () -> {
         //wait to finish sign in
         getWaitOnElement(driver, By.xpath("//*[text() = 'Compose']"));
         driver.navigate().to("https://mail.google.com/mail/#sent");

         // Delete all emails
         WebElement selectAll = getWaitOnElement(driver, By.cssSelector(".T-Jo.J-J5-Ji"));
         selectAll.click();
         try {
            getWaitOnElement(driver, By.cssSelector(".T-I.J-J5-Ji.nX.T-I-ax7.T-I-Js-Gs.mA")).click();
         } catch (Exception e) {
            //The element is not interactable
            selectAll.click();
         }


      });
      When("^I compose from the sent page$", () -> {
         // Find the button 'Send one now'
         WebElement e = getWaitOnElement(driver,By.xpath("//*[text() = 'Send']"));

         WebElement popUp = null;
         long now = System.currentTimeMillis();
         while (popUp == null && System.currentTimeMillis() - now < 5000) {
            e.click();

            try {
               popUp = driver.findElement(By.name("to"));
            } catch (Exception ex) {

            }

         }
      });

      Then("^I should be notified that email \"([^\"]*)\" is invalid$", (String email) -> {
         //wait for OK in error modal to show up
         try{
            waitUntilElementVisible(driver, By.xpath("//*[text() = 'The address \""+ email + "\" in the \"To\" field was not recognized. Please make sure that all addresses are properly formed.']"));
         } catch (TimeoutException e) {
            fail("Error modal should be shown");
         }
      });

      Then("^remove the image$", () -> getWaitOnElement(driver, By.cssSelector(".vq")).click());

   }

   /**
    * Wait for the pop up that confirms that the email was sent appears.
    * @param driver  Web driver
    */
   private void assertSentPopUp(WebDriver driver){
      try {
         WebElement e = getWaitOnElement(driver, By.xpath("//*[text() = 'Message sent.']"));
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

//       Make sure that the 'No sent messages! Send one now!' is not present
      try {
         // Try to find the button
         waitUntilElementVisible(driver, By.cssSelector(".x0"));

         //If no exceptions were thrown then it is there. This is not expected
         fail("No emails were found in the sent folder");
      } catch (Exception e) {

      }

      // Find the email with the corresponding subject line
      WebElement emailInSent = getWaitOnElementWithText(driver, By.cssSelector(".y2"), uniqueText);

      if (emailInSent == null) {
         emailInSent = getWaitOnElementWithText(driver, By.cssSelector(".bqe"), uniqueText);
      }

      // If the email we sent is not found, something went wrong
      if (emailInSent == null) {
         fail("The email could not be found in the 'Sent' folder");
      }

      // Click on the email to confirm that the attachment is there
      emailInSent.click();

      // Find the image attachment
      WebElement attachment = getWaitOnElement(driver,By.xpath("//*[text() = 'Preview attachment "+imageAttachmentName+"']"));

      if (attachment == null) {
         fail("The attachment sent could not be found in the email");
      }


   }


}
