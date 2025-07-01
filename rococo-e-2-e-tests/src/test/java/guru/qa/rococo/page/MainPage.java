package guru.qa.rococo.page;

import com.codeborne.selenide.SelenideElement;
import guru.qa.rococo.config.Config;

import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selectors.byText;
import static com.codeborne.selenide.Selenide.$;

public class MainPage {
    public static final String URL = Config.getInstance().frontUrl();

    private final SelenideElement profileBtn = $("svg.avatar-initials");
    private final SelenideElement loginBtn = $(byText("Войти"));
    private final SelenideElement museumLink = $("a[href='/museum']");

    public MainPage checkThatPageLoaded() {
        $(byText("Ваши любимые картины и художники всегда рядом")).shouldBe(visible);
        return this;
    }

    public LoginPage clickLogin() {
        loginBtn.click();
        return new LoginPage();
    }

    public void profileAvatarShouldBeVisible() {
        profileBtn.shouldBe(visible);
    }

    public MuseumPage goToMuseum() {
        museumLink.click();
        return new MuseumPage();
    }
}
