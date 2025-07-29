package page;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.SelenideElement;
import data.DataHelper;
import lombok.Getter;

import static com.codeborne.selenide.Selenide.$;

public class LoginPage {
    private final SelenideElement loginField = $("[data-test-id=login-field]");
    private final SelenideElement passwordField = $("[data-test-id=password-field]");
    private final SelenideElement loginButton = $("[data-test-id=action-login]");
    @Getter
    private final SelenideElement errorMessage = $("[data-test-id=error-notification]");

    private void enterCredentials(DataHelper.AuthInfo info) {
        loginField.setValue(info.getLogin());
        passwordField.setValue(info.getPassword());
        loginButton.click();
    }

    public LoginPage invalidLogin(DataHelper.AuthInfo info) {
        enterCredentials(info);
        errorMessage.shouldBe(Condition.visible);
        return this;
    }

    public VerificationPage validLogin(DataHelper.AuthInfo info) {
        enterCredentials(info);
        return new VerificationPage();
    }

    public boolean isErrorDisplayed() {
        return errorMessage.isDisplayed();
    }

}

