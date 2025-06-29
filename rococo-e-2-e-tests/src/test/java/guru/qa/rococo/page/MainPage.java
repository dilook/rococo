package guru.qa.rococo.page;

import guru.qa.rococo.config.Config;

import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selectors.byText;
import static com.codeborne.selenide.Selenide.$;

public class MainPage {
    public static final String URL = Config.getInstance().frontUrl();

    public void checkThatPageLoaded() {
        $(byText("Ваши любимые картины и художники всегда рядом")).shouldBe(visible);
    }
}
