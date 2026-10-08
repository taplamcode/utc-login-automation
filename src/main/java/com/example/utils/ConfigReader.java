package com.example.utils;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Properties;

public class ConfigReader {
    private static final Properties properties = new Properties();

    static {
        try (InputStream input = ConfigReader.class.getClassLoader().getResourceAsStream("config.properties")) {
            if (input != null) {
                try (InputStreamReader reader = new InputStreamReader(input, StandardCharsets.UTF_8)) {
                    properties.load(reader);
                }
            } else {
                throw new RuntimeException("Không tìm thấy file config.properties trong classpath!");
            }
        } catch (Exception e) {
            throw new RuntimeException("Lỗi khi đọc file config.properties: " + e.getMessage(), e);
        }
    }

    public static String getProperty(String key) {
        String systemValue = System.getProperty(key);
        if (systemValue != null && !systemValue.isBlank()) {
            return systemValue;
        }
        return properties.getProperty(key);
    }

    public static String getProperty(String key, String defaultValue) {
        String val = getProperty(key);
        return (val != null && !val.isBlank()) ? val : defaultValue;
    }

    public static int getIntProperty(String key, int defaultValue) {
        String val = getProperty(key);
        return val != null ? Integer.parseInt(val.trim()) : defaultValue;
    }

    public static boolean getBooleanProperty(String key, boolean defaultValue) {
        String val = getProperty(key);
        return val != null ? Boolean.parseBoolean(val.trim()) : defaultValue;
    }

    public static double getDoubleProperty(String key, double defaultValue) {
        String val = getProperty(key);
        return val != null ? Double.parseDouble(val.trim()) : defaultValue;
    }
}
