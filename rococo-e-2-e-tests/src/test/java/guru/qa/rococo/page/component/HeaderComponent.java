package guru.qa.rococo.page.component;

import com.codeborne.selenide.SelenideElement;
import guru.qa.rococo.page.LoginPage;
import io.qameta.allure.Step;

import static com.codeborne.selenide.Selenide.$;

public class HeaderComponent extends BaseComponent<HeaderComponent>{
    public HeaderComponent() {
        super($("header#shell-header"));
    }

    private final SelenideElement loginBtn = self.$("button");
    private final SelenideElement avatarBtn = self.$("[data-testid='avatar']");


    @Step("Нажать кнопку входа в заголовке")
    public LoginPage clickLogin() {
        loginBtn.click();
        return new LoginPage();
    }

    @Step("Нажать на аватар в заголовке")
    public ProfileComponent clickAvatar() {
        avatarBtn.click();
        return new ProfileComponent();
    }



}
