package com.automation.base;
import com.automation.utils.ConfigReader;
import java.time.Duration;
import java.nio.file.*;
import org.junit.jupiter.api.*;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.*;
import org.openqa.selenium.support.ui.WebDriverWait;
public abstract class BaseTest {
    protected WebDriver driver;
    protected WebDriverWait wait;
    protected final String baseUrl = ConfigReader.get("base.url");
    @BeforeEach
    void setUp() {
        ChromeOptions options = new ChromeOptions();
        if (Boolean.parseBoolean(System.getProperty("headless", "false"))) options.addArguments("--headless=new");
        options.addArguments("--window-size=1440,900");
        driver = new ChromeDriver(options);
        driver.manage().timeouts().pageLoadTimeout(Duration.ofSeconds(ConfigReader.positiveInt("page.load.timeout.seconds")));
        wait = new WebDriverWait(driver, Duration.ofSeconds(ConfigReader.positiveInt("explicit.wait.seconds")));
    }
    protected void screenshot(String name) {
        try {
            Path directory = Paths.get("build", "screenshots");
            Files.createDirectories(directory);
            Path file = Files.createTempFile(directory, name + "-", ".png");
            Files.write(file, ((TakesScreenshot) driver).getScreenshotAs(OutputType.BYTES));
            System.out.println("Screenshot: " + file.toAbsolutePath());
        } catch (Exception e) { System.err.println("Screenshot failed: " + e.getMessage()); }
    }
    @AfterEach
    void tearDown() {
        if (driver != null) { try { driver.quit(); } finally { driver = null; } }
    }
}
