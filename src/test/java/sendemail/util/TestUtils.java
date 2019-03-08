package sendemail.util;

import org.openqa.selenium.By;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import static org.junit.jupiter.api.Assertions.fail;

public class TestUtils {

    /**
     * Wait on an element with the corresponding selector to appear and returns the element
     * Times out after 10 seconds
     * @param driver Web driver
     * @param selector Selector of the element to find
     * @return The element
     */
    public static WebElement getWaitOnElement(WebDriver driver, By selector ){
        return (new WebDriverWait(driver, 10)).until(ExpectedConditions.presenceOfElementLocated(selector));
    }

    /**
     * Find and returns an element with the corresponding selector that contains the specified text in the element
     * Times out after 10 seconds
     * @param driver  Web driver
     * @param selector  Selector of the element to find
     * @param text  Text that should be contained in the element
     * @return  The element
     */
    public static WebElement getWaitOnElementWithText(WebDriver driver, By selector, String text) {

            List<WebElement> unreadEmailsSubjectLine = new WebDriverWait(driver, 10).until(ExpectedConditions.presenceOfAllElementsLocatedBy(selector));

            for (WebElement element : unreadEmailsSubjectLine) {
                // Find the element with the subject line we sent
                if (element.getText().contains(text)) {
                    return element;
                }
            }

        return null;
    }

    /**
     * Wait until the element is clickable
     * 10 seconds time out
     * @param driver  Web driver
     * @param element  The element to wait on
     */
    public static void waitUntilElementClickable(WebDriver driver, WebElement element) {
        (new WebDriverWait(driver, 10)).until(ExpectedConditions.elementToBeClickable(element));
    }

   /**
    * Wait for element to be visible
    * @param driver
    * @param selector
    * @return
    */
    public static WebElement waitUntilElementVisible(WebDriver driver, By selector) {
       return new WebDriverWait(driver, 10).until(ExpectedConditions.visibilityOfElementLocated(selector));
    }

   /**
    * Gets file path regardless of OS
    * @param endFilePath folder(s) and image names
    * @return
    */

    public static String getPathName(String... endFilePath){
       String path = System.getProperty("user.dir");
       Path currPath = Paths.get(path);
       Path filePath = Paths.get(currPath.toString(),endFilePath);
       return filePath.toString();

   }

   /**
    * Checks for alert and clicks ok
    * @param driver
    */
   public static void checkAndConfirmAlert(WebDriver driver){
      try {
         WebDriverWait wait = new WebDriverWait(driver, 5);
         wait.until(ExpectedConditions.alertIsPresent());
         driver.switchTo().alert().accept();
         
      } catch(TimeoutException e){
         fail("alert expected");
      }

   }
}
