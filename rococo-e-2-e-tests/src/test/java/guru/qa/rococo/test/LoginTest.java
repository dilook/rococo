package guru.qa.rococo.test;

import com.codeborne.selenide.Selenide;
import guru.qa.rococo.jupiter.annotation.meta.WebTest;
import guru.qa.rococo.page.MainPage;
import org.junit.jupiter.api.Test;

@WebTest
public class LoginTest {

    @Test
    void profileShouldBeDisplayedAfterSuccessLogin() {
        Selenide.open(MainPage.URL, MainPage.class)
                .checkThatPageLoaded()
                .clickLogin()
                .successLogin("duck", "12345")
                .profileAvatarShouldBeVisible();
    }

    @Test
    void errorShouldBeDisplayedOnBadCredentialLogin() {
        Selenide.open(MainPage.URL, MainPage.class)
                .clickLogin()
                .login("duck", "1")
                .checkErrorsMessage("Неверные учетные данные пользователя");
    }
}
