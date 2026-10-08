package com.example.utils;

import com.example.base.DriverFactory;
import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.extension.AfterEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * JUnit 5 Extension quản lý chụp ảnh màn hình khi test thất bại và đóng WebDriver an toàn.
 * Sử dụng AfterEachCallback để đảm bảo chụp screenshot TRƯỚC KHI driver.quit() được gọi.
 */
public class TestWatcherExtension implements AfterEachCallback {

    private static final Path SCREENSHOT_DIR = Paths.get("target", "screenshots");

    static {
        try {
            Files.createDirectories(SCREENSHOT_DIR);
        } catch (Exception ignored) {
        }
    }

    @Override
    public void afterEach(ExtensionContext context) {
        WebDriver driver = DriverFactory.getDriver();
        try {
            // Kiểm tra xem test case có ném ngoại lệ (thất bại) hay không
            if (context.getExecutionException().isPresent() && driver != null) {
                captureScreenshot(driver, context);
            }
        } catch (Throwable t) {
            System.err.println("[SCREENSHOT ERROR] Lỗi không mong muốn khi chụp screenshot: " + t.getMessage());
        } finally {
            // Luôn đảm bảo đóng driver trong finally và dọn dẹp ThreadLocal
            DriverFactory.quitDriver();
        }
    }

    private void captureScreenshot(WebDriver driver, ExtensionContext context) {
        try {
            String methodName = context.getRequiredTestMethod().getName();
            String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
            String fileName = methodName + "_" + timestamp + ".png";

            Path targetDir = Paths.get("target", "screenshots");
            Files.createDirectories(targetDir);

            File scrFile = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
            File destFile = targetDir.resolve(fileName).toFile();
            FileUtils.copyFile(scrFile, destFile);

            System.err.println("[SCREENSHOT] Đã lưu ảnh chụp màn hình khi test thất bại: " + destFile.getAbsolutePath());
        } catch (Exception e) {
            System.err.println("[SCREENSHOT ERROR] Không thể lưu ảnh chụp màn hình: " + e.getMessage());
        }
    }
}
