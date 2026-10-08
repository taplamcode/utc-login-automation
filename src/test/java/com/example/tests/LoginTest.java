package com.example.tests;

import com.example.base.DriverFactory;
import com.example.pages.HomePage;
import com.example.pages.LoginPage;
import com.example.utils.ConfigReader;
import org.junit.jupiter.api.Assumptions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.openqa.selenium.Cookie;
import org.openqa.selenium.NoSuchElementException;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * QUAN TRỌNG: Các test case kiểm thử SQL Injection (TC14 - TC20) CHỈ ĐƯỢC CHẠY
 * trên hệ thống/môi trường kiểm thử được cấp phép chính thức.
 * Không thực hiện pentest hoặc fuzzing trên hệ thống production chưa được ủy quyền.
 */
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class LoginTest extends BaseTest {

    private LoginPage loginPage;
    private HomePage homePage;

    private String validUsername;
    private String validPassword;

    private String msgEmptyUsername;
    private String msgEmptyPassword;
    private String msgInvalidAccount;

    @BeforeEach
    public void initPage() {
        loginPage = new LoginPage(driver);
        homePage = new HomePage(driver);

        validUsername = ConfigReader.getProperty("validUsername", "huongnt");
        validPassword = ConfigReader.getProperty("validPassword", "123456@utc");

        msgEmptyUsername = ConfigReader.getProperty("msgEmptyUsername", "Bạn chưa nhập tên đăng nhập");
        msgEmptyPassword = ConfigReader.getProperty("msgEmptyPassword", "Bạn chưa nhập mật khẩu");
        msgInvalidAccount = ConfigReader.getProperty("msgInvalidAccount", "không đúng");
    }

    /**
     * Chuẩn hoá chuỗi: loại bỏ khoảng trắng thừa, tab, xuống dòng.
     */
    private String normalize(String str) {
        return str == null ? "" : str.replaceAll("\\s+", " ").trim();
    }

    /**
     * Thu thập và in thông tin chẩn đoán ra console khi đăng nhập thất bại.
     */
    private void diagnoseLoginFailure(String testName) {
        String currentUrl = driver.getCurrentUrl();
        String title = driver.getTitle();
        String errorMsg = "";
        try {
            errorMsg = loginPage.getErrorMessage();
        } catch (Exception ignored) {
        }

        System.err.println("\n================================================================================");
        System.err.println("[CHẨN ĐOÁN TEST THẤT BẠI] " + testName);
        System.err.println("  1. URL hiện tại : " + currentUrl);
        System.err.println("  2. Tiêu đề trang: " + title);
        System.err.println("  3. Thông báo lỗi: " + (errorMsg.isBlank() ? "(Không tìm thấy text lỗi trên trang)" : errorMsg));

        if (homePage.isCaptchaDisplayed()) {
            System.err.println("  4. Cảnh báo     : Phát hiện CAPTCHA xuất hiện trên trang đăng nhập!");
        } else if (errorMsg.contains("không đúng") || errorMsg.contains("Tài khoản")) {
            System.err.println("  4. Kết luận     : Tài khoản hợp lệ [" + validUsername + "] có thể ĐÃ BỊ ĐỔI MẬT KHẨU hoặc KHÔNG TỒN TẠI!");
        } else if (errorMsg.contains("khóa") || errorMsg.contains("khoá")) {
            System.err.println("  4. Kết luận     : Tài khoản [" + validUsername + "] ĐÃ BỊ KHÓA do đăng nhập sai nhiều lần!");
        }
        System.err.println("  5. Ảnh chụp lỗi : target/screenshots/" + testName + "_*.png");
        System.err.println("================================================================================\n");
    }

}
