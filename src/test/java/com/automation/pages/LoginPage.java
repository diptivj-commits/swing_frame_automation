package com.automation.pages;
import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.*;
public class LoginPage {
    private final WebDriver driver;
    private final WebDriverWait wait;
    private final By username = By.id("username");
    private final By password = By.id("password");
    private final By submit = By.cssSelector("button[type='submit']");
    private final By flash = By.id("flash");
    public LoginPage(WebDriver driver, WebDriverWait wait) { this.driver = driver; this.wait = wait; }
    public void open(String baseUrl) { driver.get(baseUrl + "/login"); wait.until(ExpectedConditions.visibilityOfElementLocated(username)); }
    public void login(String user, String pass) {
        WebElement userInput = wait.until(ExpectedConditions.visibilityOfElementLocated(username));
        userInput.clear(); userInput.sendKeys(user);
        WebElement passInput = driver.findElement(password);
        passInput.clear(); passInput.sendKeys(pass);
        wait.until(ExpectedConditions.elementToBeClickable(submit)).click();
    }
    public String waitForMessage(String expected) {
        wait.until(ExpectedConditions.textToBePresentInElementLocated(flash, expected));
        return driver.findElement(flash).getText();
    }
}
