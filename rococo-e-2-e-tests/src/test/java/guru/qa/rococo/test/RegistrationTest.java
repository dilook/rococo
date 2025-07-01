package guru.qa.rococo.test;

import com.codeborne.selenide.Selenide;
import guru.qa.rococo.jupiter.annotation.meta.WebTest;
import guru.qa.rococo.page.RegisterPage;
import guru.qa.rococo.utils.RandomDataUtils;
import org.junit.jupiter.api.Test;

@WebTest
public class RegistrationTest {

    @Test
    void userShouldBeRegisteredWithValidData() {
        Selenide.open(RegisterPage.URL, RegisterPage.class)
                .register(RandomDataUtils.randomUsername(), "12345", "12345")
                .checkSuccessMessage();
    }

    @Test
    void errorShouldBeDisplayedWhenPasswordsDoNotMatch() {
        Selenide.open(RegisterPage.URL, RegisterPage.class)
                .register(RandomDataUtils.randomUsername(), "12345", "54321")
                .checkPasswordError("Passwords should be equal");
    }

    @Test
    void errorShouldBeDisplayedForExistingUsername() {
        String randomUsername = RandomDataUtils.randomUsername();
        Selenide.open(RegisterPage.URL, RegisterPage.class)
                .register(randomUsername, "12345", "12345")
                .checkSuccessMessage();
        Selenide.open(RegisterPage.URL, RegisterPage.class)
                .register(randomUsername, "12345", "12345")
                .checkUsernameError("Username `" + randomUsername + "` already exists");
    }

    @Test
    void errorShouldBeDisplayedForShortPassword() {
        Selenide.open(RegisterPage.URL, RegisterPage.class)
                .register(RandomDataUtils.randomUsername(), "1", "1")
                .checkPasswordError("Allowed password length should be from 3 to 12 characters")
                .checkPasswordSubmitError("Allowed password length should be from 3 to 12 characters");
    }
}
