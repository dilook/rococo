package guru.qa.rococo.test;

import com.codeborne.selenide.Selenide;
import guru.qa.rococo.jupiter.annotation.User;
import guru.qa.rococo.jupiter.annotation.meta.WebTest;
import guru.qa.rococo.model.rest.UserJson;
import guru.qa.rococo.page.MainPage;
import guru.qa.rococo.page.RegisterPage;
import guru.qa.rococo.utils.RandomDataUtils;
import org.junit.jupiter.api.Test;

@WebTest
public class RegistrationTest {

    @Test
    void shouldRegisterUserWithValidData() {
        String username = RandomDataUtils.randomUsername();
        Selenide.open(RegisterPage.URL, RegisterPage.class)
                .register(username, "12345", "12345")
                .checkSuccessMessage();

        Selenide.open(MainPage.URL, MainPage.class)
                .clickLogin()
                .successLogin(username, "12345")
                .profileAvatarShouldBeVisible();
    }

    @Test
    void shouldDisplayErrorWhenPasswordsDoNotMatch() {
        Selenide.open(RegisterPage.URL, RegisterPage.class)
                .register(RandomDataUtils.randomUsername(), "12345", "54321")
                .checkPasswordError("Passwords should be equal");
    }

    @Test
    @User
    void shouldDisplayErrorForExistingUsername(UserJson user) {
        Selenide.open(RegisterPage.URL, RegisterPage.class)
                .register(user.username(), "12345", "12345")
                .checkUsernameError("Username `%s` already exists".formatted(user.username()));
    }

    @Test
    void shouldDisplayErrorForPasswordLessThan3Characters() {
        Selenide.open(RegisterPage.URL, RegisterPage.class)
                .register(RandomDataUtils.randomUsername(), "1", "1")
                .checkPasswordError("Allowed password length should be from 3 to 12 characters")
                .checkPasswordSubmitError("Allowed password length should be from 3 to 12 characters");
    }

    @Test
    void shouldDisplayErrorForPasswordMoreThan12Characters() {
        Selenide.open(RegisterPage.URL, RegisterPage.class)
                .register(RandomDataUtils.randomUsername(), "1234567891011", "1234567891011")
                .checkPasswordError("Allowed password length should be from 3 to 12 characters")
                .checkPasswordSubmitError("Allowed password length should be from 3 to 12 characters");
    }

    @Test
    void shouldDisplayErrorForUsernameMoreTHan50Characters() {
        Selenide.open(RegisterPage.URL, RegisterPage.class)
                .register(RandomDataUtils.randomSentence(51), "123", "123")
                .checkUsernameError("Allowed username length should be from 3 to 50 characters");
    }
}
