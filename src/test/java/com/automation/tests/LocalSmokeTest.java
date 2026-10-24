package com.automation.tests;
import com.automation.base.BaseTest;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.support.ui.ExpectedConditions;
import static org.junit.jupiter.api.Assertions.*;
public class LocalSmokeTest extends BaseTest {
    @Test
    void submitLocalForm() {
        var fixture = getClass().getClassLoader().getResource("fixtures/form.html");
        assertNotNull(fixture, "Local fixture must be on the classpath");
        driver.get(fixture.toExternalForm());
        driver.findElement(By.id("username")).sendKeys("Dipti");
        driver.findElement(By.id("submit")).click();
        wait.until(ExpectedConditions.textToBePresentInElementLocated(By.id("message"), "Hello, Dipti"));
        assertEquals("Hello, Dipti", driver.findElement(By.id("message")).getText());
    }
}
