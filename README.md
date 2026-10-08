# utc-login-automation

Dự án tự động hóa kiểm thử (Automation Testing) chức năng Đăng nhập trên hệ thống Văn phòng điện tử Trường Đại học Giao thông Vận tải (UTC) tại địa chỉ: [https://vanphongdientu.utc.edu.vn](https://vanphongdientu.utc.edu.vn).

---

## 1. Giới thiệu

- **Mục đích**: Tự động hóa bộ 21 test case kiểm thử toàn diện chức năng đăng nhập:
  - `TC00_PreCheck`: Ca kiểm tra điều kiện tiên quyết (Pre-flight check) trạng thái hoạt động của tài khoản kiểm thử hợp lệ.
  - `TC01` đến `TC20`: Bộ ca kiểm thử chức năng, giá trị biên, tính năng giao diện và bảo mật khớp theo tài liệu đặc tả kiểm thử (`login_test_case.xlsx`).
- **Phạm vi & Miễn trừ trách nhiệm**:
  - Dự án được xây dựng phục vụ mục đích học tập, nghiên cứu và chỉ được phép thực thi trên môi trường kiểm thử được ủy quyền.
  - Các ca kiểm thử tấn công giả lập SQL Injection (`TC14` – `TC20`) chỉ nhằm đánh giá cơ chế phòng thủ (sanitization/parameterization) của ứng dụng; tuyệt đối không sử dụng công cụ để thực hiện tấn công phá hoại, fuzzing hoặc khai thác trên hệ thống production khi chưa có sự đồng ý chính thức từ đơn vị quản trị.

---

## 2. Yêu cầu hệ thống

Hệ thống đã được kiểm chứng hoạt động ổn định trên môi trường:
- **Hệ điều hành**: Windows 11 (64-bit) / macOS / Linux.
- **Java Development Kit (JDK)**: JDK 17 trở lên (đã kiểm thử và chạy tương thích trên JDK 25, mã nguồn cấu hình biên dịch mục tiêu `release 17`).
- **Apache Maven**: Phiên bản 3.8.x trở lên.
- **Trình duyệt**: Google Chrome (phiên bản ổn định, đã kiểm chứng trên Chrome 154).
- **Kết nối mạng**: Kết nối Internet ổn định để `WebDriverManager` tự động tải binary `chromedriver` khớp với phiên bản Chrome máy cục bộ trong lần chạy đầu tiên.

---

## 3. Cấu trúc thư mục

```text
utc-login-automation/
├── pom.xml                               # File cấu hình Maven, quản lý dependencies và Surefire plugin UTF-8
├── login_test_case.xlsx                  # Bảng thiết kế ca kiểm thử gốc của dự án
├── test.bat                              # File script chạy nhanh trên Windows (tự động bật chcp 65001)
├── run-test.ps1                          # File script chạy nhanh trên PowerShell
├── src/
│   ├── main/java/com/example/
│   │   ├── base/
│   │   │   └── DriverFactory.java        # Khởi tạo và quản lý vòng đời WebDriver (ThreadLocal) cho Chrome/Edge/Firefox
│   │   ├── pages/
│   │   │   ├── LoginPage.java            # Page Object Model cho trang Đăng nhập (locators, actions, JS executor)
│   │   │   └── HomePage.java             # Page Object Model cho Trang chủ sau khi đăng nhập thành công
│   │   └── utils/
│   │       └── ConfigReader.java         # Tiện ích đọc dữ liệu cấu hình từ file config.properties
│   └── test/
│       ├── java/com/example/
│       │   ├── tests/
│       │   │   ├── BaseTest.java         # Lớp cơ sở JUnit 5 khởi tạo driver trước mỗi test
│       │   │   └── LoginTest.java        # Định nghĩa 21 test case thực thi theo thứ tự @Order an toàn
│       │   └── utils/
│       │       └── TestWatcherExtension.java # JUnit Extension chụp ảnh màn hình khi fail rồi mới đóng driver
│       └── resources/
│           ├── config.properties         # File cấu hình URL, tài khoản, timeout và kỳ vọng chuỗi lỗi
│           └── simplelogger.properties   # Cấu hình lọc log gọn gàng của SLF4J, ẩn log tải webdriver
└── target/
    ├── surefire-reports/                 # Báo cáo kết quả kiểm thử định dạng TXT và XML
    └── screenshots/                      # Thư mục tự động lưu ảnh chụp màn hình khi test thất bại
```

---

## 4. Cấu hình

Toàn bộ thông số vận hành kiểm thử được quản lý tập trung tại tệp: `src/test/resources/config.properties`.

### Bảng giải thích các khóa cấu hình

| Khóa (Key) | Giá trị mặc định | Giải thích |
| :--- | :--- | :--- |
| `browser` | `chrome` | Trình duyệt thực thi (`chrome`, `firefox`, `edge`). |
| `baseUrl` | `https://vanphongdientu.utc.edu.vn` | Địa chỉ URL của hệ thống cần kiểm thử. |
| `headless` | `false` | Chạy ẩn danh không mở cửa sổ UI (`true`/`false`). |
| `implicitWaitSeconds` | `10` | Thời gian chờ ngầm định khi tìm phần tử DOM (giây). |
| `explicitWaitSeconds` | `15` | Thời gian chờ tường minh cho các thao tác tải trang/chuyển hướng (giây). |
| `validUsername` | `huongnt` | Tên tài khoản kiểm thử hợp lệ. |
| `validPassword` | `123456@utc` | Mật khẩu tài khoản kiểm thử hợp lệ. |
| `msgEmptyUsername` | `Bạn chưa nhập tên đăng nhập` | Thông báo lỗi kỳ vọng khi bỏ trống Username. |
| `msgEmptyPassword` | `Bạn chưa nhập mật khẩu` | Thông báo lỗi kỳ vọng khi bỏ trống Password. |
| `msgInvalidAccount` | `không đúng` | Chuỗi ký tự kỳ vọng trong thông báo khi nhập sai tài khoản/mật khẩu. |
| `sqlTimeoutThresholdSeconds` | `4.0` | Ngưỡng thời gian tối đa cho phép ở test Time-based SQLi (giây). |

### Hướng dẫn cập nhật tài khoản
Khi nhà trường/quản trị viên cập nhật lại mật khẩu hoặc đổi tài khoản kiểm thử, bạn **chỉ cần sửa trực tiếp giá trị** `validUsername` và `validPassword` trong file `src/test/resources/config.properties`, hoàn toàn **không cần chỉnh sửa hay biên dịch lại mã nguồn Java**.

> [!WARNING]
> **Cảnh báo an toàn tài khoản**: Việc chạy lặp lại liên tục nhiều lần các test case sai mật khẩu (`TC03`, `TC04`, `TC10`) hoặc các test SQL Injection có thể kích hoạt cơ chế khóa tài khoản hoặc hiện CAPTCHA trên hệ thống UTC. Mã nguồn đã được sắp xếp thứ tự thực thi (`@Order`) để chạy nhóm test hợp lệ trước, nhằm hạn chế rủi ro này.

---

## 5. Cách chạy

Mở cửa sổ dòng lệnh (Terminal / Command Prompt / PowerShell) tại thư mục gốc của dự án:

### Chạy toàn bộ 21 test case
```bash
mvn clean test
```

### Chạy một test case cụ thể
```bash
mvn test -Dtest=LoginTest#TC03_ValidUsernameWrongPassword
```

### Chạy ở chế độ không mở giao diện (Headless Mode)
Mở tệp `src/test/resources/config.properties` và đổi:
```properties
headless=true
```
Sau đó thực thi lệnh `mvn test`.

### Lưu ý quan trọng trên Windows (Khắc phục lỗi font tiếng Việt `??`)
Trên Windows Command Prompt hoặc PowerShell, bảng mã mặc định thường là `CP1252` hoặc `CP437`, gây lỗi hiển thị ký tự tiếng Việt có dấu thành `??`. Hãy chuyển trang mã console sang UTF-8 trước khi gọi Maven:

```cmd
chcp 65001
mvn clean test
```

*Hoặc bạn chỉ cần kích đúp chuột vào file `test.bat` (đã tích hợp sẵn lệnh `chcp 65001`).*

---

## 6. Danh sách test case

Dưới đây là bảng đặc tả 21 ca kiểm thử được tự động hóa trong lớp `LoginTest.java`:

| ID | Mô tả ca kiểm thử | Dữ liệu đầu vào (Input) | Kết quả mong đợi (Expected Output) | Ghi chú kỹ thuật |
| :--- | :--- | :--- | :--- | :--- |
| **TC00** | Kiểm tra trạng thái tài khoản kiểm thử | User: `huongnt`<br>Pass: `123456@utc` | Đăng nhập thành công vào Trang chủ. | Chạy đầu tiên để phát hiện sớm nếu tài khoản bị đổi pass/bị khóa. |
| **TC01** | Để trống Username | User: (rỗng)<br>Pass: `1256` | Báo lỗi: *"Bạn chưa nhập tên đăng nhập"*. | Kiểm tra validation client/server. |
| **TC02** | Để trống Password | User: `huongnt`<br>Pass: (rỗng) | Báo lỗi: *"Bạn chưa nhập mật khẩu"*. | Kiểm tra trường bắt buộc. |
| **TC03** | Đúng tên, sai mật khẩu | User: `huongnt`<br>Pass: `utc@235` | Báo lỗi: *"Tài khoản hoặc mật khẩu không đúng."* | Khớp chuỗi chứa *"không đúng"*. |
| **TC04** | Sai tên, đúng mật khẩu | User: `huongthunguyen`<br>Pass: `123456@utc` | Báo lỗi: *"Tài khoản hoặc mật khẩu không đúng."* | Khớp chuỗi chứa *"không đúng"*. |
| **TC05** | Đăng nhập đúng + Chọn *'Giữ tôi luôn đăng nhập'* | User: `huongnt`<br>Pass: `123456@utc`<br>Checkbox: Checked | Đăng nhập thành công; mở lại trình duyệt vẫn ở Trang chủ. | **Mở lại trình duyệt**: Lưu session cookies và khôi phục trên instance mới. |
| **TC06** | Đăng nhập đúng + Không chọn *'Giữ tôi luôn đăng nhập'* | User: `huongnt`<br>Pass: `123456@utc`<br>Checkbox: Unchecked | Đăng nhập thành công; mở lại trình duyệt quay về trang Đăng nhập. | **Mở lại trình duyệt**: Đóng browser và mở lại trang gốc để kiểm tra session bị hủy. |
| **TC07** | Bỏ trống cả Tên đăng nhập và Mật khẩu | User: (rỗng)<br>Pass: (rỗng) | Báo lỗi cảnh báo chưa nhập thông tin. | Bắt cảnh báo tại ô đầu tiên hoặc thông báo chung. |
| **TC08** | Nhập khoảng trắng (Space) vào ô Username | User: `'   '`<br>Pass: `123456@utc` | Hệ thống coi như bỏ trống hoặc báo lỗi không hợp lệ. | Kiểm tra cơ chế trim khoảng trắng. |
| **TC09** | Nhập khoảng trắng (Space) vào ô Password | User: `huongnt`<br>Pass: `'   '` | Hệ thống báo lỗi chưa nhập Mật khẩu hoặc sai mật khẩu. | Kiểm tra không chấp nhận mật khẩu toàn khoảng trắng. |
| **TC10** | Cả Tên đăng nhập và Mật khẩu đều sai | User: `sai_user`<br>Pass: `sai_pass` | Báo lỗi: *"Tài khoản hoặc mật khẩu không đúng."* | Xác nhận thông báo chuẩn bảo mật (không lộ user tồn tại). |
| **TC11** | Phân biệt hoa/thường ở Username | User: `HUONGNT`<br>Pass: `123456@utc` | Đăng nhập thành công (nếu case-insensitive) hoặc ở lại trang login an toàn. | Kiểm tra tính nhất quán case-sensitivity. |
| **TC12** | Tính năng ẩn chuỗi mật khẩu (Masked text) | Pass: `123456@utc` | Ký tự hiển thị dưới dạng dấu chấm tròn (`••••••`) hoặc sao (`******`). | Kiểm tra thuộc tính HTML `type="password"`. |
| **TC13** | Đăng nhập bằng phím Enter | User: `huongnt`<br>Pass: `123456@utc` | Tự động submit form và chuyển hướng vào Trang chủ. | Sử dụng `Keys.ENTER` thay vì thao tác click chuột. |
| **TC14** | SQLi cơ bản (Bypass Authentication) | User: `' or 1=1 --`<br>Pass: bất kỳ | Báo sai thông tin tài khoản; tuyệt đối không vào được hệ thống. | Xác thực chống bypass xác thực. |
| **TC15** | Ký tự nháy đơn ngắt chuỗi (Single Quote) | User: `huongnt'`<br>Pass: `123456` | Xử lý an toàn chuỗi đầu vào; không lỗi HTTP 500 hay lộ lỗi cú pháp SQL. | Kiểm tra sanitize ký tự đặc biệt. |
| **TC16** | Biểu thức logic luôn đúng ở cả 2 trường | User: `' OR '1'='1`<br>Pass: `' OR '1'='1` | Từ chối đăng nhập và hiển thị thông báo lỗi an toàn. | Ngăn chặn tấn công logic kép. |
| **TC17** | Chú thích SQL ngắt kiểm tra pass | User: `huongnt'--`<br>Pass: bất kỳ | Không bỏ qua bước kiểm tra mật khẩu; từ chối đăng nhập. | Kiểm tra xử lý ký hiệu comment SQL (`--`). |
| **TC18** | Union-based SQL Injection | User: `' UNION SELECT NULL--`<br>Pass: `test` | Hiển thị lỗi thông thường; không rò rỉ tên bảng, cấu trúc DB hay stack trace. | Kiểm tra rò rỉ thông tin nội bộ. |
| **TC19** | Time-based Blind SQL Injection | User: `huongnt'; WAITFOR DELAY '0:0:5'--`<br>Pass: `123456` | Thời gian phản hồi bình thường, không bị treo trễ (< 4.0 giây). | **Đo thời gian**: Dùng `System.nanoTime()` đo độ trễ thực tế. |
| **TC20** | SQL Injection trực tiếp vào trường Password | User: `huongnt`<br>Pass: `' OR 'a'='a` | Mật khẩu được băm/xử lý an toàn; từ chối đăng nhập. | Kiểm tra injection tại ô mật khẩu. |

---

## 7. Kết quả và báo cáo

### 1. Đọc kết quả trong Console
- Khi thực thi lệnh `mvn clean test`, Maven Surefire sẽ in trạng thái từng test case:
  - `[INFO] Running com.example.tests.LoginTest`
  - Kết quả tổng kết cuối phiên: `Tests run: 21, Failures: X, Errors: 0, Skipped: 0`.
- Nếu có test case thất bại (ví dụ: do mật khẩu tài khoản `huongnt` đã bị đổi), hệ thống sẽ in trực tiếp khung **CHẨN ĐOÁN TEST THẤT BẠI** với thông tin rõ ràng: URL hiện tại, Tiêu đề trang, Nội dung lỗi hiển thị trên trang và đường dẫn ảnh chụp màn hình.

### 2. Báo cáo Surefire Reports
Sau khi kiểm thử hoàn tất, các tệp báo cáo chi tiết được sinh tự động tại:
- `target/surefire-reports/com.example.tests.LoginTest.txt`: Báo cáo chi tiết định dạng văn bản thô.
- `target/surefire-reports/TEST-com.example.tests.LoginTest.xml`: Báo cáo chuẩn JUnit XML (dùng tích hợp CI/CD như Jenkins, GitHub Actions).

### 3. Ảnh chụp màn hình khi thất bại (Screenshots)
- Khi bất kỳ test case nào gặp lỗi hoặc assertion không đạt, `TestWatcherExtension` sẽ tự động kích hoạt thao tác chụp ảnh giao diện trình duyệt tại đúng thời điểm lỗi xảy ra.
- Ảnh được lưu tự động vào thư mục: `target/screenshots/` với định dạng tên:
  ```text
  target/screenshots/{Tên_Test_Case}_{NămThángNgày_GiờPhútGiây}.png
  ```
- Thông báo lỗi (`FAILURE`) trong báo cáo nêu rõ nguyên nhân thất bại (ví dụ: *Đăng nhập thất bại! Tài khoản [huongnt] có thể đã bị đổi mật khẩu hoặc bị khóa. Vui lòng cập nhật mật khẩu mới trong config.properties.*) giúp người xem nắm ngay căn nguyên mà không cần mở code debug lại.

---

## 8. Xử lý sự cố thường gặp

### 1. Tài khoản bị đổi mật khẩu hoặc bị khóa
- **Hiện tượng**: `TC00`, `TC05`, `TC06`, `TC13` bị fail; console xuất hiện cảnh báo tài khoản không đúng hoặc xuất hiện CAPTCHA.
- **Cách xử lý**: Kiểm tra lại thông tin đăng nhập thực tế trên trình duyệt thủ công, sau đó cập nhật thông tin mới vào `validUsername` và `validPassword` trong file `src/test/resources/config.properties`.

### 2. Console hiển thị ký tự `??` hoặc lỗi font tiếng Việt
- **Nguyên nhân**: Bảng mã của terminal Windows không phải là UTF-8 (mặc định là Windows-1252 hoặc CP437).
- **Cách xử lý**: Gõ lệnh `chcp 65001` trước khi chạy `mvn clean test` hoặc chạy qua file `test.bat`. Cấu hình Maven Surefire và POM đều đã được thiết lập UTF-8 chuẩn.

### 3. Lỗi không tải được ChromeDriver
- **Nguyên nhân**: Máy tính mất kết nối Internet hoặc tường lửa chặn tải file từ kho lưu trữ Google Chrome for Testing / Bonigarcia.
- **Cách xử lý**: Đảm bảo máy tính có kết nối mạng ổn định và Google Chrome trên máy đã được cài đặt phiên bản mới nhất.

### 4. Lỗi không tìm thấy phần tử DOM (NoSuchElementException)
- **Nguyên nhân**: Giao diện website UTC có sự thay đổi cấu trúc HTML, tên class hoặc ID của trường đăng nhập.
- **Cách xử lý**: Kiểm tra lại mã nguồn HTML thực tế của trang login và cập nhật lại các selector tại lớp `LoginPage.java`.

### 5. Lỗi `NoSuchSessionException: Session ID is null` khi chụp màn hình
- **Nguyên nhân**: Driver bị tắt (`quit()`) trước khi tiến hành chụp ảnh màn hình lỗi.
- **Cách xử lý**: Vấn đề này **đã được xử lý triệt để** trong dự án bằng cách sử dụng `TestWatcherExtension` (implements `AfterEachCallback`). Extension sẽ kiểm tra ngoại lệ thực thi, chụp ảnh màn hình trước, sau đó mới gọi `quit()` trong khối `finally`. Nếu bạn có tùy biến mã nguồn, hãy đảm bảo không gọi `driver.quit()` trong `@AfterEach` của `BaseTest`.

