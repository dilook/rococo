package guru.qa.rococo.test.web;


import com.codeborne.selenide.Selenide;
import guru.qa.rococo.jupiter.annotation.ApiLogin;
import guru.qa.rococo.jupiter.annotation.Museum;
import guru.qa.rococo.jupiter.annotation.Museums;
import guru.qa.rococo.jupiter.annotation.User;
import guru.qa.rococo.jupiter.annotation.meta.WebTest;
import guru.qa.rococo.model.rest.MuseumJson;
import guru.qa.rococo.page.MainPage;
import guru.qa.rococo.page.MuseumCardPage;
import guru.qa.rococo.page.MuseumPage;
import guru.qa.rococo.utils.RandomDataUtils;
import org.junit.jupiter.api.Test;

@WebTest
public class MuseumTest {

    @Test
    @Museums(count = 4)
    void shouldLoadMuseumPageForUnauthorizedUser() {
        Selenide.open(MuseumPage.URL, MuseumPage.class)
                .checkMuseumsSize(4);
    }

    @Test
    @ApiLogin
    @User
    void shouldLoadMuseumPageForAuthorizedUser() {
        Selenide.open(MainPage.URL, MainPage.class)
                .goToMuseums()
                .checkMuseumsSize(4);
    }

    @Test
    void shouldNotBeAbleToAddMuseumForUnauthorizedUser() {
        Selenide.open(MuseumPage.URL, MuseumPage.class)
                .checkAddButtonNotExist();
    }

    @Test
    @ApiLogin
    @User
    @Museum
    void shouldFindMuseumByTitle(MuseumJson museum) {
        Selenide.open(MuseumPage.URL, MuseumPage.class)
                .checkMuseumsSize(4) // wait ending of first page request
                .checkMuseum(museum.title())
                .checkMuseumsSize(1);
    }

    @Test
    @ApiLogin
    @User
    void shouldAddMuseumByAuthorizedUser() {
        String museumName = RandomDataUtils.randomMuseumName();

        Selenide.open(MainPage.URL, MainPage.class)
                .goToMuseums()
                .addMuseum(museumName,
                        "Австралия",
                        "Канберра",
                        "img/lyvr.jpg",
                        "Музей" + museumName + " — один из крупнейших и самый популярный художественный музей мира."
                ).checkMuseum(museumName);
    }

    @Test
    @ApiLogin
    @User
    @Museum
    void shouldUpdateMuseumByAuthorizedUser(MuseumJson museum) {
        String randomCityName = RandomDataUtils.randomCityName();
        Selenide.open(MuseumPage.URL, MuseumPage.class)
                .clickTo(museum.title())
                .editMuseum()
                .setCity(randomCityName)
                .submitForm();
        new MuseumCardPage().checkCountryCity(randomCityName);
    }

    @Test
    @ApiLogin
    @User
    @Museums(count = 8)
    void shouldLoadMuseumByPage() {
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
