package guru.qa.rococo.test;


import com.codeborne.selenide.Selenide;
import guru.qa.rococo.jupiter.annotation.meta.WebTest;
import guru.qa.rococo.page.MainPage;
import guru.qa.rococo.page.MuseumCardPage;
import guru.qa.rococo.page.MuseumPage;
import guru.qa.rococo.utils.RandomDataUtils;
import org.junit.jupiter.api.Test;

@WebTest
public class MuseumTest {

    @Test
    void shouldLoadMuseumPageForUnauthorizedUser() {
        Selenide.open(MainPage.URL, MainPage.class)
                .clickLogin()
                .successLogin("duck", "12345");
        for (int i = 0; i < 4; i++) {
            createMuseum(RandomDataUtils.randomMuseumName());
        }

        Selenide.open(MainPage.URL, MainPage.class)
                .getHeader()
                .clickAvatar()
                .logout();
        Selenide.open(MuseumPage.URL, MuseumPage.class)
                .checkMuseumsSize(4);
    }

    private void createMuseum(String museumName) {
        Selenide.open(MuseumPage.URL, MuseumPage.class)
                .checkThatPageLoaded()
                .addMuseum(museumName,
                        "Австралия",
                        "Канберра",
                        "img/lyvr.png",
                        "Музей %s — один из крупнейших и самый популярный художественный музей мира.".formatted(museumName)
                )
                .checkAlertMessage("Добавлен музей: %s".formatted(museumName));
    }

    @Test
    void shouldLoadMuseumPageForAuthorizedUser() {
        Selenide.open(MainPage.URL, MainPage.class)
                .clickLogin()
                .successLogin("duck", "12345")
                .goToMuseums()
                .checkMuseumsSize(4);
    }

    @Test
    void shouldNotBeAbleToAddMuseumForUnauthorizedUser() {
        Selenide.open(MuseumPage.URL, MuseumPage.class)
                .checkAddButtonNotExist();
    }

    @Test
    void shouldFindMuseumByTitle() {
        Selenide.open(MainPage.URL, MainPage.class)
                .clickLogin()
                .successLogin("duck", "12345");

        String museumName = RandomDataUtils.randomMuseumName();
        createMuseum(museumName);

        Selenide.open(MuseumPage.URL, MuseumPage.class)
                .checkMuseumsSize(4) // wait ending of first page request
                .checkMuseum(museumName)
                .checkMuseumsSize(1);
    }

    @Test
    void shouldAddMuseumByAuthorizedUser() {
        String museumName = RandomDataUtils.randomMuseumName();

        Selenide.open(MainPage.URL, MainPage.class)
                .clickLogin()
                .successLogin("duck", "12345")
                .goToMuseums()
                .addMuseum(museumName,
                        "Австралия",
                        "Канберра",
                        "img/lyvr.png",
                        "Музей" + museumName + " — один из крупнейших и самый популярный художественный музей мира."
                ).checkMuseum(museumName);
    }

    @Test
    void shouldUpdateMuseumByAuthorizedUser() {
        String originalMuseumName = RandomDataUtils.randomMuseumName();

        Selenide.open(MainPage.URL, MainPage.class)
                .clickLogin()
                .successLogin("duck", "12345");

        createMuseum(originalMuseumName);

        String randomCityName = RandomDataUtils.randomCityName();
        Selenide.open(MuseumPage.URL, MuseumPage.class)
                .clickTo(originalMuseumName)
                .editMuseum()
                .setCity(randomCityName)
                .submitForm();
        new MuseumCardPage().checkCountryCity(randomCityName);
    }

    @Test
    void shouldLoadMuseumByPage() {
        Selenide.open(MainPage.URL, MainPage.class)
                .clickLogin()
                .successLogin("duck", "12345");

        for (int i = 0; i < 8; i++) {
            createMuseum(RandomDataUtils.randomMuseumName());
        }

        Selenide.open(MuseumPage.URL, MuseumPage.class)
                .checkMuseumsSize(4)
                .loadNextPage()
                .checkMuseumsSize(8);

        // Test that page loads properly for unauthorized user as well
        Selenide.open(MainPage.URL, MainPage.class)
                .getHeader()
                .clickAvatar()
                .logout();

        Selenide.open(MuseumPage.URL, MuseumPage.class)
                .checkMuseumsSize(4)
                .loadNextPage()
                .checkMuseumsSize(8);
    }


}
