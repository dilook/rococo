package guru.qa.rococo.test.web;

import com.codeborne.selenide.Selenide;
import guru.qa.rococo.jupiter.annotation.ApiLogin;
import guru.qa.rococo.jupiter.annotation.User;
import guru.qa.rococo.jupiter.annotation.meta.WebTest;
import guru.qa.rococo.model.rest.UserJson;
import guru.qa.rococo.page.MainPage;
import org.junit.jupiter.api.Test;

@WebTest
public class LoginWebTest {

    @Test
    @User
    void shouldDisplayedProfileAfterSuccessLogin(UserJson user) {
        Selenide.open(MainPage.URL, MainPage.class)
                .checkThatPageLoaded()
                .clickLogin()
                .successLogin(user.username(), user.testData().password())
                .profileAvatarShouldBeVisible();
    }

    @Test
    void shouldDisplayedErrorOnBadCredentialLogin() {
        Selenide.open(MainPage.URL, MainPage.class)
                .clickLogin()
                .login("duck", "1")
                .checkErrorsMessage("Неверные учетные данные пользователя");
    }

    @Test
    @User
    @ApiLogin
    void shouldBeAbleToLogout() {
        Selenide.open(MainPage.URL, MainPage.class)
                .getHeader()
                .clickAvatar()
                .logout();
        new MainPage()
                .checkThatPageLoaded()
                .profileAvatarShouldNotExist();
    }
}
