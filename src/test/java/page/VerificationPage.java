package page;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.SelenideElement;
import org.openqa.selenium.NoSuchElementException;

import java.time.Duration;

import static com.codeborne.selenide.Selenide.$;

public class VerificationPage {

    private final SelenideElement codeField = $("[data-test-id=code] input");
    private final SelenideElement verifyButton = $("[data-test-id=action-verify]");
    private final SelenideElement errorNotification = $("[data-test-id='error-notification'] .notification__content");

    public VerificationPage() {
        codeField.shouldBe(Condition.visible, Duration.ofSeconds(10));
    }

    public void verifyErrorNotification(String expectedText) {
        errorNotification.shouldHave(Condition.exactText(expectedText), Duration.ofSeconds(10))
                .shouldBe(Condition.visible);
    }

    public void validVerify(String verificationCode) {
        verify(verificationCode);
    }

    public void verify(String verificationCode) {
        codeField.setValue(verificationCode);
        verifyButton.click();

        try {
            new DashboardPage();
            new DashboardPage();
        } catch (NoSuchElementException e) {
            throw new RuntimeException("Верификация не удалась. Не удалось загрузить страницу Dashboard.", e);
        }
    }
}