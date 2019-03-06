package sendemail.util;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

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

    /**
     * Wait until the element is clickable
     * 10 seconds time out
     * @param driver  Web driver
     * @param element  The element to wait on
     */
    public static void waitUntilElementClickableAndClick(WebDriver driver, WebElement element) {
        (new WebDriverWait(driver, 10)).until(ExpectedConditions.elementToBeClickable(element));
        element.click();
    }

    /**
     * Wait for the web page to be fully loaded
     * 30s time out
     * @param driver  Web driver
     */
    public static void waitForPageLoaded(WebDriver driver) {
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
