package guru.qa.rococo.test;


import com.codeborne.selenide.Selenide;
import guru.qa.rococo.jupiter.annotation.meta.WebTest;
import guru.qa.rococo.page.MainPage;
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
            String museumName = RandomDataUtils.randomMuseumName();
            createMuseum(museumName);
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
                .addMuseum(museumName,
                        "Австралия",
                        "Канберра",
                        "img/lyvr.png",
                        "Музей" + museumName + " — один из крупнейших и самый популярный художественный музей мира."
                );
    }


    @Test
    void shouldLoadMuseumPageForAuthorizedUser() {

    }

    @Test
    void shouldFindMuseumByTitle() {

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

    }

    @Test
    void shouldLoadMuseumByPage() {
    }


}
