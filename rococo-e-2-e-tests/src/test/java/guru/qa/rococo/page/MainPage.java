package guru.qa.rococo.page;

import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.SelenideElement;
import guru.qa.rococo.config.Config;
import guru.qa.rococo.page.component.HeaderComponent;
import io.qameta.allure.Step;
import lombok.Getter;

import static com.codeborne.selenide.Condition.exist;
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

    @Override
    @Step("Проверить, что главная страница загружена")
    public MainPage checkThatPageLoaded() {
        $(byText("Ваши любимые картины и художники всегда рядом")).shouldBe(visible);
        return this;
    }

    @Step("Нажать кнопку входа")
    public LoginPage clickLogin() {
        loginBtn.click();
        return new LoginPage();
    }

    @Step("Проверить, что аватар профиля видим")
    public void profileAvatarShouldBeVisible() {
        profileBtn.shouldBe(visible);
    }
    
    @Step("Проверить, что аватар профиля не существует")
    public void profileAvatarShouldNotExist() {
        profileBtn.shouldNot(exist, visible);
    }

    private void goToItem(String name) {
        navigationList.find(text(name)).click();
    }

    @Step("Перейти к музеям")
    public MuseumPage goToMuseums() {
        goToItem("Музеи");
        return new MuseumPage();
    }

    @Step("Перейти к художникам")
    public ArtistPage goToArtists() {
        goToItem("Художники");
        return new ArtistPage();
    }

    @Step("Перейти к картинам")
    public PaintingPage goToPaintings() {
        goToItem("Картины");
        return new PaintingPage();
    }
}
