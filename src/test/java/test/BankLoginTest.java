package test;

import com.codeborne.selenide.Condition;
import data.DataHelper;
import data.SQLHelper;
import org.junit.jupiter.api.*;
import page.LoginPage;


import java.sql.SQLException;

import static com.codeborne.selenide.Selenide.open;

public class BankLoginTest {
    private LoginPage loginPage;
    private static final String BASE_URL = System.getProperty("baseUrl", "http://localhost:9999"); // Получаем URL из системного свойства
    private String validLogin;
    private String validPassword;
    private static SQLHelper sqlHelper;

    @AfterAll
    static void tearDownAll() {
        try {
            sqlHelper.cleanDatabase();
        } catch (SQLException e) {
            System.err.println("Ошибка при очистке базы данных: " + e.getMessage());
        }
    }

    @AfterEach
    void tearDown() {
        try {
            sqlHelper.cleanAuthCodes();
        } catch (SQLException e) {
            System.err.println("Ошибка при очистке кодов аутентификации: " + e.getMessage());
        }
    }

    @BeforeAll
    static void beforeAll() {
        String dbUrl = System.getProperty("db.url", "jdbc:mysql://localhost:3306/app");
        String dbUser = System.getProperty("db.user", "app");
        String dbPassword = System.getProperty("db.password", "pass");
        sqlHelper = new SQLHelper(dbUrl, dbUser, dbPassword);
    }

    @BeforeEach
    void setUp() {
        loginPage = open(BASE_URL, LoginPage.class);
        validLogin = System.getProperty("valid.login", "vasya");
        validPassword = System.getProperty("valid.password", "qwerty123");
    }

    @Test
    @DisplayName("Successful login")
    void shouldSuccessfulLogin() throws SQLException {
        var authInfo = new DataHelper.AuthInfo(validLogin, validPassword);
        var verificationPage = loginPage.validLogin(authInfo);
        var verificationCode = sqlHelper.getVerificationCode();
        verificationPage.validVerify(verificationCode);
    }

    @Test
    @DisplayName("Error on invalid login")
    void shouldGetErrorNotificationIfLoginWithRandomUser() {
        var authInfo = DataHelper.generateRandomUser();
        loginPage.invalidLogin(authInfo)
                .getErrorMessage().shouldHave(Condition.text("Неверно указан логин или пароль"));
    }

    @Test
    @DisplayName("Error on invalid verification code")
    void ShouldGetErrorNotificationIfLoginWithExistUserAndRandomVerificationCode() {
        var authInfo = new DataHelper.AuthInfo(validLogin, validPassword);
        var verificationPage = loginPage.validLogin(authInfo);
        var verificationCode = DataHelper.generateRandomVerificationCode();
        verificationPage.verify(verificationCode.getCode());
        verificationPage.verifyErrorNotification("Неверно указан код! Попробуйте ещё раз.");
    }
}
