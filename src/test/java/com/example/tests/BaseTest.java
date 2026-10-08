package com.example.tests;

import com.example.base.DriverFactory;
import com.example.utils.ConfigReader;
import com.example.utils.TestWatcherExtension;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.openqa.selenium.WebDriver;

@ExtendWith(TestWatcherExtension.class)
public abstract class BaseTest {
    protected WebDriver driver;
    protected String baseUrl;

    @BeforeEach
    public void setUp() {
        driver = DriverFactory.createDriver();
        baseUrl = ConfigReader.getProperty("baseUrl", "https://vanphongdientu.utc.edu.vn");
        driver.get(baseUrl);
    }

    public WebDriver getDriver() {
        return driver;
    }
}
