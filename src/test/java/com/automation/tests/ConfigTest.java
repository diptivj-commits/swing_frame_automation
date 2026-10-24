package com.automation.tests;
import com.automation.utils.ConfigReader;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
public class ConfigTest {
    @Test void loadsApplicationUrl() { assertEquals("https://the-internet.herokuapp.com", ConfigReader.get("base.url")); }
    @Test void loadsTimeout() { assertTrue(ConfigReader.positiveInt("explicit.wait.seconds") > 0); }
    @Test void rejectsMissingProperty() { assertThrows(IllegalArgumentException.class, () -> ConfigReader.get("missing.key")); }
}
