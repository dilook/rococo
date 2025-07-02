package guru.qa.rococo.page;

import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.SelenideElement;
import guru.qa.rococo.config.Config;
import guru.qa.rococo.page.component.HeaderComponent;
import lombok.Getter;

import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selectors.byText;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.$$;

public class MainPage extends BasePage<MainPage> {
    public static final String URL = Config.getInstance().frontUrl();

    private final SelenideElement profileBtn = $("svg.avatar-initials");
    private final SelenideElement loginBtn = $(byText("Войти"));
    private final ElementsCollection navigationList = $$("main nav li");
    @Getter
    private final HeaderComponent header = new HeaderComponent();

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

    private void goToItem(String name) {
        navigationList.find(text(name)).click();
    }

    public MuseumPage goToMuseums() {
        goToItem("Музеи");
        return new MuseumPage();
    }

    public ArtistPage goToArtists() {
        goToItem("Художники");
        return new ArtistPage();
    }

    public PaintingPage goToPaintings() {
        goToItem("Картины");
        return new PaintingPage();
    }
}
