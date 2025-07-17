package guru.qa.rococo.test.web;

import com.codeborne.selenide.Selenide;
import guru.qa.rococo.jupiter.annotation.ApiLogin;
import guru.qa.rococo.jupiter.annotation.TestArtist;
import guru.qa.rococo.jupiter.annotation.TestArtists;
import guru.qa.rococo.jupiter.annotation.User;
import guru.qa.rococo.jupiter.annotation.meta.WebTest;
import guru.qa.rococo.model.rest.ArtistJson;
import guru.qa.rococo.model.rest.UserJson;
import guru.qa.rococo.page.ArtistCardPage;
import guru.qa.rococo.page.ArtistPage;
import guru.qa.rococo.page.MainPage;
import guru.qa.rococo.utils.RandomDataUtils;
import org.junit.jupiter.api.Test;

@WebTest
public class ArtistWebTest {

    @Test
    @User
    @ApiLogin
    @TestArtists(count = 4)
    void shouldLoadArtistPageForAuthorizedUser() {
        Selenide.open(MainPage.URL, MainPage.class)
                .goToArtists()
                .checkArtistsGreaterOrEqThan(4);
    }

    @Test
    @TestArtists(count = 4)
    void shouldLoadArtistPageForUnauthorizedUser() {
        Selenide.open(ArtistPage.URL, ArtistPage.class)
                .checkArtistsGreaterOrEqThan(4);
    }

    @Test
    @ApiLogin
    @User
    void shouldAddArtistByAuthorizedUser() {
        String artistName = RandomDataUtils.randomArtistName();

        Selenide.open(MainPage.URL, MainPage.class)
                .goToArtists()
                .addArtist(artistName,
                        RandomDataUtils.randomArtistBiography(),
                        "img/artist.jpg"
                ).checkArtist(artistName);
    }

    @Test
    void shouldNotBeAbleToAddArtistForUnauthorizedUser() {
        Selenide.open(ArtistPage.URL, ArtistPage.class)
                .checkAddButtonNotExist();
    }

    @Test
    @TestArtist
    void shouldFindArtistByName(ArtistJson artist) {
        Selenide.open(ArtistPage.URL, ArtistPage.class)
                .checkArtist(artist.name())
                .checkArtistsSize(1);
    }

    @Test
    @ApiLogin
    @User
    @TestArtist
    void shouldUpdateArtistByAuthorizedUser(ArtistJson artist) {
        String randomBiography = RandomDataUtils.randomArtistBiography();
        Selenide.open(ArtistPage.URL, ArtistPage.class)
                .clickTo(artist.name())
                .editArtist()
                .setBiography(randomBiography)
                .submitForm();
        new ArtistCardPage().checkBiography(randomBiography);
    }

    @Test
    @ApiLogin
    @User
    @TestArtists(count = 36)
    void shouldLoadArtistsByPage() {
        Selenide.open(ArtistPage.URL, ArtistPage.class)
                .loadNextPage()
                .checkArtistsSize(36);

        // Test that page loads properly for unauthorized user as well
        Selenide.open(MainPage.URL, MainPage.class)
                .getHeader()
                .clickAvatar()
                .logout();

        Selenide.open(ArtistPage.URL, ArtistPage.class)
                .loadNextPage()
                .checkArtistsSize(36);
    }

    @Test
    @ApiLogin
    @User
    @TestArtist
    void shouldDisplayArtistDetails(ArtistJson artist) {
        Selenide.open(ArtistPage.URL, ArtistPage.class)
                .clickTo(artist.name())
                .checkArtistName(artist.name())
                .checkBiography(artist.biography())
                .checkAvatarImg(artist.photo());
    }

    @Test
    @ApiLogin
    @User
    void shouldValidateArtistFormFields() {
        Selenide.open(MainPage.URL, MainPage.class)
                .goToArtists()
                .addArtist()
                .submitForm()
                .checkNameRequiredValidateMessage()
                .setName("test")
                .submitForm()
                .checkPhotoRequiredValidateMessage()
                .setPhoto("img/artist.jpg")
                .submitForm()
                .checkBioRequiredValidateMessage();
    }
}
