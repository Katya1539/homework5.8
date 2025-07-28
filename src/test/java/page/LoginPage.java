package page;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.SelenideElement;
import data.DataHelper;

import static com.codeborne.selenide.Condition.exactText;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;

public class LoginPage {
    private final SelenideElement loginField = $("[data-test-id=login-field]");
    private final SelenideElement passwordField = $("[data-test-id=password-field]");
    private final SelenideElement loginButton = $("[data-test-id=action-login]");
    public final SelenideElement errorMessage = $("[data-test-id=error-notification]");

    public LoginPage invalidLogin(DataHelper.AuthInfo info) {
        loginField.setValue(info.getLogin());
        passwordField.setValue(info.getPassword());
        loginButton.click();
        errorMessage.shouldBe(Condition.visible); // Проверяем, что сообщение об ошибке отображается
        return this; // Возвращаем LoginPage, чтобы можно было продолжить работу на этой странице
    }

    public VerificationPage validLogin(DataHelper.AuthInfo info) {
        loginField.setValue(info.getLogin());
        passwordField.setValue(info.getPassword());
        loginButton.click();
        return new VerificationPage();
    }

    public boolean isErrorDisplayed() {
        return errorMessage.isDisplayed();
    }

    public void login(DataHelper.AuthInfo info) {
        loginField.setValue(info.getLogin());
        passwordField.setValue(info.getPassword());
        loginButton.click();
    }
}

