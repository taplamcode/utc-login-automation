package com.example.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

/**
 * Page Object Model cho Trang Chủ sau khi đăng nhập thành công (Văn phòng điện tử UTC).
 */
public class HomePage {
    private final WebDriver driver;
    private final WebDriverWait wait;

    // =========================================================================
    // LOCATORS TRANG CHỦ SAU ĐĂNG NHẬP
    // =========================================================================
    // Khu vực hiển thị thông tin / tên người dùng
    private static final By USER_PROFILE_HEADER = By.cssSelector(".user-profile, .user-info, .profile-name, #username-display, .user-name, .account-info");
    
    // Nút hoặc link Đăng xuất đặc trưng
    private static final By LOGOUT_LINK = By.xpath("//a[contains(@href,'logout') or contains(@href,'Logout') or contains(text(),'Đăng xuất') or contains(text(),'Thoát')]");
    
    // Thanh menu điều hướng chính của portal
    private static final By MAIN_NAVIGATION = By.cssSelector(".main-menu, .sidebar-menu, .nav-dashboard, .navbar-nav, .dashboard-menu");

    // Dấu hiệu nhận biết đang ở trang Đăng nhập
    private static final By LOGIN_USERNAME_FIELD = By.cssSelector("input[name='username']");

    // Dấu hiệu trang có CAPTCHA hoặc xác thực 2 bước
    private static final By CAPTCHA_CONTAINER = By.cssSelector("img[src*='captcha'], [id*='captcha'], [class*='captcha']");

    public HomePage(WebDriver driver) {
        this.driver = driver;
        // Chờ tối đa 15 giây vì hệ thống chuyển hướng và tải session có thể chậm
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(15));
    }

    /**
     * Kiểm tra xem Trang Chủ đã được tải thành công hay chưa.
     * Alias: isLoaded()
     */
    public boolean isHomePageDisplayed() {
        try {
            // 1. Nếu ô nhập username vẫn hiển thị thì chắc chắn chưa rời khỏi màn hình đăng nhập
            if (!driver.findElements(LOGIN_USERNAME_FIELD).isEmpty() && driver.findElement(LOGIN_USERNAME_FIELD).isDisplayed()) {
                return false;
            }

            // 2. Chờ một trong các phần tử trang chủ hiển thị hoặc URL đã chuyển khỏi /Login
            return wait.until(d -> {
                boolean hasUserHeader = !d.findElements(USER_PROFILE_HEADER).isEmpty();
                boolean hasLogout = !d.findElements(LOGOUT_LINK).isEmpty();
                boolean hasMenu = !d.findElements(MAIN_NAVIGATION).isEmpty();
                boolean redirectedAwayFromLogin = !d.getCurrentUrl().toLowerCase().contains("/login")
                        && d.findElements(LOGIN_USERNAME_FIELD).isEmpty();

                return hasUserHeader || hasLogout || hasMenu || redirectedAwayFromLogin;
            });
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Alias method cho isHomePageDisplayed()
     */
    public boolean isLoaded() {
        return isHomePageDisplayed();
    }

    /**
     * Kiểm tra xem trang có đang yêu cầu mã CAPTCHA hay không.
     */
    public boolean isCaptchaDisplayed() {
        return !driver.findElements(CAPTCHA_CONTAINER).isEmpty();
    }

    public void clickLogout() {
        try {
            WebElement logoutBtn = wait.until(ExpectedConditions.elementToBeClickable(LOGOUT_LINK));
            logoutBtn.click();
        } catch (Exception ignored) {
        }
    }
}
