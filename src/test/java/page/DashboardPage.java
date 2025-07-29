package page;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.SelenideElement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;

import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Condition.visible;

public class DashboardPage {

    private static final Logger logger = LoggerFactory.getLogger(DashboardPage.class);
    private static final String DASHBOARD_TITLE_DEFAULT = "Личный кабинет";

    private final SelenideElement heading = $("[data-test-id=dashboard]");

    public DashboardPage() {
        this(DASHBOARD_TITLE_DEFAULT);
    }

    public DashboardPage(String expectedText) {
        try {
            heading.shouldBe(Condition.and("Dashboard loaded", visible, text(expectedText)), Duration.ofSeconds(10));
            logger.info("Dashboard loaded successfully with title: {}", expectedText);
        } catch (AssertionError e) {
            logger.error("Failed to load Dashboard with title: {}", expectedText, e);
            throw new AssertionError("Failed to load Dashboard with title: " + expectedText, e);
        }
    }

    public boolean isDashboardVisible() {
        return heading.isDisplayed();
    }

    public String getDashboardTitle() {
        return heading.getText();
    }
}
