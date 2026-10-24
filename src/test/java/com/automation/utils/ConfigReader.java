package com.automation.utils;
import java.io.*;
import java.util.Properties;
public final class ConfigReader {
    private static final Properties CONFIG = load();
    private ConfigReader() {}
    private static Properties load() {
        Properties config = new Properties();
        try (InputStream input = ConfigReader.class.getClassLoader().getResourceAsStream("config.properties")) {
            if (input == null) throw new IllegalStateException("config.properties not found on classpath");
            config.load(input);
            return config;
        } catch (IOException e) { throw new IllegalStateException("Cannot read config.properties", e); }
    }
    public static String get(String key) {
        String value = CONFIG.getProperty(key);
        if (value == null || value.isBlank()) throw new IllegalArgumentException("Missing property: " + key);
        return value.trim();
    }
    public static int positiveInt(String key) {
        int number = Integer.parseInt(get(key));
        if (number <= 0) throw new IllegalArgumentException("Property must be positive: " + key);
        return number;
    }
}
