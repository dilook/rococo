package guru.qa.rococo.test.web;

import com.codeborne.selenide.Selenide;
import guru.qa.rococo.jupiter.annotation.ApiLogin;
import guru.qa.rococo.jupiter.annotation.TestArtist;
import guru.qa.rococo.jupiter.annotation.TestMuseum;
import guru.qa.rococo.jupiter.annotation.TestPainting;
import guru.qa.rococo.jupiter.annotation.TestPaintings;
import guru.qa.rococo.jupiter.annotation.User;
import guru.qa.rococo.jupiter.annotation.meta.WebTest;
import guru.qa.rococo.model.rest.PaintingJson;
import guru.qa.rococo.page.MainPage;
import guru.qa.rococo.page.PaintingCardPage;
import guru.qa.rococo.page.PaintingPage;
import guru.qa.rococo.utils.RandomDataUtils;
import org.junit.jupiter.api.Test;

@WebTest
public class PaintingWebTest {

    @Test
    @User
    @ApiLogin
    void shouldLoadPaintingPageForAuthorizedUser() {
        Selenide.open(MainPage.URL, MainPage.class)
                .goToPaintings()
                .checkThatPageLoaded();
    }

    @Test
    @TestPaintings(count = 9)
    void shouldLoadPaintingPageForUnauthorizedUser() {
        Selenide.open(PaintingPage.URL, PaintingPage.class)
                .checkPaintingsSize(9);
    }

    @Test
    @ApiLogin
    @User
    @TestArtist
    @TestMuseum
    void shouldAddPaintingByAuthorizedUser() {
        String paintingTitle = RandomDataUtils.randomPaintingName();

        Selenide.open(MainPage.URL, MainPage.class)
                .goToPaintings()
                .addPainting(paintingTitle,
                        "Прекрасная картина, созданная талантливым художником.",
                        "img/painting.jpg"
                ).checkPainting(paintingTitle);
    }

    @Test
    void shouldNotBeAbleToAddPaintingForUnauthorizedUser() {
        Selenide.open(PaintingPage.URL, PaintingPage.class)
                .checkAddButtonNotExist();
    }

    @Test
    @TestPainting
    void shouldFindPaintingByTitle(PaintingJson painting) {
        Selenide.open(PaintingPage.URL, PaintingPage.class)
                .checkPaintingsSize(9) // wait ending of first page request
                .checkPainting(painting.title())
                .checkPaintingsSize(1);
    }

    @Test
    @ApiLogin
    @User
    @TestPainting
    void shouldUpdatePaintingByAuthorizedUser(PaintingJson painting) {
        String newDescription = RandomDataUtils.randomSentence(50);
        Selenide.open(PaintingPage.URL, PaintingPage.class)
                .clickTo(painting.title())
                .editPainting()
                .setDescription(newDescription)
                .submitForm();
        new PaintingCardPage().checkDescription(newDescription);
    }

    @Test
    @ApiLogin
    @User
    @TestPaintings(count = 18)
    void shouldLoadPaintingsByPage() {
        Selenide.open(PaintingPage.URL, PaintingPage.class)
                .checkPaintingsSize(9)
                .loadNextPage()
                .checkPaintingsSize(9);

        // Test that page loads properly for unauthorized user as well
        Selenide.open(MainPage.URL, MainPage.class)
                .getHeader()
                .clickAvatar()
                .logout();

        Selenide.open(PaintingPage.URL, PaintingPage.class)
                .checkPaintingsSize(9)
                .loadNextPage()
                .checkPaintingsSize(9);
    }

    @Test
    @TestPainting
    void shouldDisplayPaintingDetails(PaintingJson painting) {
        Selenide.open(PaintingPage.URL, PaintingPage.class)
                .clickTo(painting.title())
                .checkPaintingTitle(painting.title())
                .checkDescription(painting.description())
                .checkArtistName(painting.artist().name())
                .checkPaintingImg(painting.content());
    }

    @Test
    @ApiLogin
    @User
    void shouldValidatePaintingFormFields() {
        Selenide.open(MainPage.URL, MainPage.class)
                .goToPaintings()
                .addPainting()
                .submitForm()
                .checkTitleRequiredValidateMessage()
                .setTitle("test")
                .submitForm()
                .checkContentRequiredValidateMessage()
                .setContent("img/painting.jpg")
                .submitForm()
                .checkArtistRequiredValidateMessage()
                .setArtistFirstValue()
                .submitForm()
                .checkDescriptionRequiredValidateMessage();
    }

    @Test
    @TestPainting
    void shouldFilterPaintingsByTitle(PaintingJson painting) {
        Selenide.open(PaintingPage.URL, PaintingPage.class)
                .checkPainting(painting.title());
    }

}
