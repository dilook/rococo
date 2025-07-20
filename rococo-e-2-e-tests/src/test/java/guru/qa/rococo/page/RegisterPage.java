package guru.qa.rococo.page;

import com.codeborne.selenide.SelenideElement;
import guru.qa.rococo.config.Config;
import io.qameta.allure.Step;

import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;

public class RegisterPage extends BasePage<RegisterPage>{
    public static final String URL = Config.getInstance().authUrl() + "register";

    private final SelenideElement username = $("input[name='username']");
    private final SelenideElement password = $("input[name='password']");
    private final SelenideElement passwordSubmit = $("input[name='passwordSubmit']");
    private final SelenideElement submitBtn = $("button[type='submit']");
    private final SelenideElement usernameError = $(".form__error.error__username");
    private final SelenideElement passwordError = $(".form__error.error__password");
    private final SelenideElement passwordSubmitError = $(".form__error.error__passwordSubmit");
    private final SelenideElement successMessage = $("p.form__subheader");
    private final SelenideElement loginBtn = $(".form__submit");

    @Step("Зарегистрировать пользователя с именем '{username}'")
    public RegisterPage register(String username, String password, String passwordConfirm) {
        this.username.setValue(username);
        this.password.setValue(password);
        this.passwordSubmit.setValue(passwordConfirm);
        submitBtn.click();
        return this;
    }

    @Step("Войти в систему")
    public MainPage login() {
        loginBtn.click();
        return new MainPage();
    }

    @Step("Проверить ошибку имени пользователя: '{errorText}'")
    public RegisterPage checkUsernameError(String errorText) {
        usernameError.shouldHave(text(errorText));
        return this;
    }

    @Step("Проверить ошибку пароля: '{errorText}'")
    public RegisterPage checkPasswordError(String errorText) {
        passwordError.shouldHave(text(errorText));
        return this;
    }

    @Step("Проверить ошибку подтверждения пароля: '{errorText}'")
    public RegisterPage checkPasswordSubmitError(String errorText) {
        passwordSubmitError.shouldHave(text(errorText));
        return this;
    }

    @Step("Проверить сообщение об успешной регистрации")
    public RegisterPage checkSuccessMessage() {
        successMessage.shouldHave(text("Добро пожаловать в Rococo"));
        return this;
    }

    @Override
    @Step("Проверить, что страница регистрации загружена")
    public RegisterPage checkThatPageLoaded() {
        submitBtn.shouldBe(visible).shouldHave(text("Зарегистрироваться"));
        return this;
    }
}
