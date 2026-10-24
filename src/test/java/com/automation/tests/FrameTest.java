package com.automation.tests;
import com.automation.base.BaseTest;
import com.sun.net.httpserver.HttpServer;
import java.net.InetSocketAddress;
import java.io.InputStream;
import org.junit.jupiter.api.*;
import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.ExpectedConditions;
import static org.junit.jupiter.api.Assertions.*;
public class FrameTest extends BaseTest {
    private HttpServer server;
    @BeforeEach void openLab() throws Exception {
        server = HttpServer.create(new InetSocketAddress("127.0.0.1", 0), 0);
        server.createContext("/", exchange -> {
            String name = exchange.getRequestURI().getPath();
            if (name.equals("/")) name = "/index.html";
            if (!java.util.Set.of("/index.html", "/profile.html", "/workspace.html", "/nested.html").contains(name)) {
                exchange.sendResponseHeaders(404, -1); exchange.close(); return;
            }
            try (InputStream input = getClass().getResourceAsStream("/fixtures/frames" + name)) {
                if (input == null) { exchange.sendResponseHeaders(404, -1); return; }
                byte[] bytes = input.readAllBytes();
                exchange.getResponseHeaders().set("Content-Type", "text/html; charset=UTF-8");
                exchange.sendResponseHeaders(200, bytes.length);
                exchange.getResponseBody().write(bytes);
            } finally { exchange.close(); }
        });
        server.start();
        driver.get("http://127.0.0.1:" + server.getAddress().getPort() + "/");
    }
    @AfterEach void stopLab() { if (server != null) server.stop(0); }
    @Test public void frameByName() {
        wait.until(ExpectedConditions.frameToBeAvailableAndSwitchToIt("profile"));
        driver.findElement(By.id("name")).sendKeys("Dipti");
        driver.findElement(By.id("save")).click();
        assertEquals("Saved: Dipti", driver.findElement(By.id("result")).getText());
    }
    @Test public void frameByIndex() {
        wait.until(ExpectedConditions.frameToBeAvailableAndSwitchToIt(0));
        assertEquals("Profile frame", driver.findElement(By.tagName("h2")).getText());
    }
    @Test public void frameByElement() {
        WebElement frame = wait.until(ExpectedConditions.presenceOfElementLocated(By.id("profile-frame")));
        driver.switchTo().frame(frame);
        assertTrue(driver.findElement(By.id("save")).isDisplayed());
    }
    @Test public void nestedFramesAndParent() {
        wait.until(ExpectedConditions.frameToBeAvailableAndSwitchToIt("workspace"));
        wait.until(ExpectedConditions.frameToBeAvailableAndSwitchToIt("nested"));
        driver.findElement(By.id("notifications")).click();
        assertTrue(driver.findElement(By.id("notifications")).isSelected());
        driver.switchTo().parentFrame();
        assertEquals("Workspace frame", driver.findElement(By.id("workspace-title")).getText());
    }
    @Test public void returnToDefaultContent() {
        wait.until(ExpectedConditions.frameToBeAvailableAndSwitchToIt("workspace"));
        wait.until(ExpectedConditions.frameToBeAvailableAndSwitchToIt("nested"));
        assertEquals("Nested frame ready", driver.findElement(By.id("nested-status")).getText());
        driver.switchTo().defaultContent();
        assertEquals("Top document ready", driver.findElement(By.id("top-status")).getText());
        assertEquals(2, driver.findElements(By.tagName("iframe")).size());
    }
}
