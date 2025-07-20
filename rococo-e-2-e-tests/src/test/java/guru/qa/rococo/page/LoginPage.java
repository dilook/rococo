package guru.qa.rococo.page;

import com.codeborne.selenide.SelenideElement;
import guru.qa.rococo.config.Config;
import io.qameta.allure.Step;

import static com.codeborne.selenide.Selenide.$;

public class LoginPage extends BasePage<LoginPage> {
    public static final String URL = Config.getInstance().authUrl() + "login";

    private final SelenideElement username = $("input[name='username']");
    private final SelenideElement password = $("input[name='password']");
    private final SelenideElement submitBtn = $("button[type='submit']");

    @Step("Войти в систему с именем '{username}' и паролем '{password}'")
    public LoginPage login(String username, String password) {
        this.username.setValue(username);
        this.password.setValue(password);
        submitBtn.click();
        return this;
    }

    @Step("Успешно войти в систему с именем '{username}' и паролем '{password}'")
    public MainPage successLogin(String username, String password) {
        login(username, password);
        MainPage mainPage = new MainPage();
        mainPage.profileAvatarShouldBeVisible();
        return mainPage;
    }


    @Override
    @Step("Проверить, что страница входа загружена")
    public LoginPage checkThatPageLoaded() {
        return this;
    }
}
