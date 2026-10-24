package com.automation.tests;
import com.automation.base.BaseTest;
import com.automation.pages.LoginPage;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import static org.junit.jupiter.api.Assertions.*;
public class LoginTest extends BaseTest {
    @ParameterizedTest(name = "{index}: username={0}")
    @CsvSource({
        "tomsmith, SuperSecretPassword!, You logged into a secure area!",
        "wrongUser, SuperSecretPassword!, Your username is invalid!",
        "tomsmith, wrongPassword, Your password is invalid!"
    })
    void verifyLogin(String username, String password, String expectedMessage) {
        try {
            LoginPage page = new LoginPage(driver, wait);
            page.open(baseUrl);
            page.login(username, password);
            assertTrue(page.waitForMessage(expectedMessage).contains(expectedMessage));
            assertTrue(driver.getCurrentUrl().endsWith(expectedMessage.startsWith("You logged") ? "/secure" : "/login"));
        } catch (RuntimeException | AssertionError failure) {
            screenshot("login-failure");
            throw failure;
        }
    }
}
