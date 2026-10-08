package com.example.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Keys;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

/**
 * Page Object Model cho trang Đăng nhập (https://vanphongdientu.utc.edu.vn)
 * Toàn bộ Locator được khai báo tập trung tại các hằng số By để dễ cập nhật.
 */
public class LoginPage {
    private final WebDriver driver;
    private final WebDriverWait wait;

    // =========================================================================
    // LOCATORS
    // =========================================================================
    // Ô nhập Tên đăng nhập (name="username")
    private static final By USERNAME_INPUT = By.cssSelector("#username, input[name='username']");
    
    // Ô nhập Mật khẩu (name="userpwd")
    private static final By PASSWORD_INPUT = By.cssSelector("#userpwd, input[name='userpwd']");
    
    // Checkbox "Giữ tôi luôn đăng nhập" (id/name="persistent")
    private static final By REMEMBER_ME_CHECKBOX = By.cssSelector("#persistent, input[name='persistent'], input[type='checkbox']");
    
    // Nút "Đăng nhập" (class="submit_login")
    private static final By LOGIN_BUTTON = By.cssSelector(".submit_login, input.submit_login, button.submit_login");
    
    // Link "Bạn quên mật khẩu đăng nhập ?"
    private static final By FORGOT_PASSWORD_LINK = By.xpath("//a[contains(@href,'GetPass') or contains(text(),'quên mật khẩu')]");
    
    // Vùng thông báo lỗi: Trang web thực tế dùng <div class="error">
    private static final By ERROR_MESSAGE = By.cssSelector("div.error, .error, .alert-danger, .error-message, #lblError, #errorMessage");
    private static final By ERROR_MESSAGE_XPATH = By.xpath("//div[contains(@class,'error')] | //*[contains(text(),'không đúng') or contains(text(),'chưa nhập')]");

    // Container của form đăng nhập (dùng để in outerHTML khi debug)
    private static final By FORM_CONTAINER = By.cssSelector(".form, form, .main");

    public LoginPage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(10));
    }

    public void enterUsername(String username) {
        WebElement element = wait.until(ExpectedConditions.visibilityOfElementLocated(USERNAME_INPUT));
        element.clear();
        if (username != null) {
            element.sendKeys(username);
        }
    }

    public void enterPassword(String password) {
        WebElement element = wait.until(ExpectedConditions.visibilityOfElementLocated(PASSWORD_INPUT));
        element.clear();
        if (password != null) {
            element.sendKeys(password);
        }
    }

    public void setRememberMe(boolean check) {
        WebElement element = wait.until(ExpectedConditions.presenceOfElementLocated(REMEMBER_ME_CHECKBOX));
        if (element.isSelected() != check) {
            try {
                element.click();
            } catch (Exception e) {
                // Trang web dùng jQuery ẩn input checkbox thật, click qua JS executor để đảm bảo tương tác
                ((JavascriptExecutor) driver).executeScript("arguments[0].click();", element);
            }
        }
    }

    public boolean isRememberMeSelected() {
        WebElement element = wait.until(ExpectedConditions.presenceOfElementLocated(REMEMBER_ME_CHECKBOX));
        return element.isSelected();
    }

    public void clickLogin() {
        WebElement button = wait.until(ExpectedConditions.elementToBeClickable(LOGIN_BUTTON));
        button.click();
    }

    public void submitWithEnterKey() {
        WebElement passwordElem = wait.until(ExpectedConditions.visibilityOfElementLocated(PASSWORD_INPUT));
        passwordElem.sendKeys(Keys.ENTER);
    }

    /**
     * Phương thức tạm thời in ra outerHTML của vùng form/thông báo phục vụ debug DOM thực tế.
     */
    public String getErrorContainerOuterHtml() {
        try {
            WebElement container = driver.findElement(FORM_CONTAINER);
            return (String) ((JavascriptExecutor) driver).executeScript("return arguments[0].outerHTML;", container);
        } catch (Exception e) {
            return "[Không thể lấy outerHTML: " + e.getMessage() + "]";
        }
    }

    /**
     * Lấy nội dung thông báo lỗi trên giao diện.
     * Sử dụng WebDriverWait, fallback sang textContent qua JavascriptExecutor nếu getText() rỗng.
     * Nếu không tìm thấy, ném ngoại lệ rõ ràng thay vì trả về chuỗi rỗng.
     */
    public String getErrorMessage() {
        WebElement errorElem = null;
        try {
            errorElem = wait.until(ExpectedConditions.visibilityOfElementLocated(ERROR_MESSAGE));
        } catch (Exception ex) {
            // Thử fallback sang locator XPath dựa trên nội dung text
            try {
                errorElem = wait.until(ExpectedConditions.visibilityOfElementLocated(ERROR_MESSAGE_XPATH));
            } catch (Exception ignored) {
                // Tiếp tục xử lý ném ngoại lệ chi tiết ở bên dưới
            }
        }

        if (errorElem != null) {
            String text = errorElem.getText();
            // Dự phòng: Nếu getText() rỗng (do style ẩn, inline hoặc DOM đặc thù), dùng JavascriptExecutor đọc textContent
            if (text == null || text.isBlank()) {
                Object jsText = ((JavascriptExecutor) driver).executeScript("return arguments[0].textContent;", errorElem);
                text = jsText != null ? jsText.toString() : "";
            }
            if (!text.isBlank()) {
                return text.trim();
            }
        }

        // Ném lỗi rõ ràng kèm outerHTML phục vụ việc phân tích DOM nếu không tìm thấy
        throw new NoSuchElementException("Không tìm thấy thông báo lỗi hiển thị trên trang đăng nhập sau thời gian chờ. "
                + "DOM outerHTML form hiện tại:\n" + getErrorContainerOuterHtml());
    }

    public String getPasswordFieldType() {
        WebElement element = wait.until(ExpectedConditions.presenceOfElementLocated(PASSWORD_INPUT));
        return element.getDomAttribute("type");
    }

    public boolean isForgotPasswordLinkDisplayed() {
        try {
            return driver.findElement(FORGOT_PASSWORD_LINK).isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }

    public boolean isLoginPageDisplayed() {
        try {
            return driver.findElement(USERNAME_INPUT).isDisplayed() 
                && driver.findElement(LOGIN_BUTTON).isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }
}
