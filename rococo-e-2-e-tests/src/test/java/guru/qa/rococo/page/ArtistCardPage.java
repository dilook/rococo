package guru.qa.rococo.page;

import com.codeborne.selenide.SelenideElement;
import guru.qa.rococo.page.component.CreateArtistComponent;
import io.qameta.allure.Step;

import static com.codeborne.selenide.Condition.attribute;
import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Selenide.$;

public class ArtistCardPage {

    private final SelenideElement editBtn = $("[data-testid=edit-artist]");
    private final SelenideElement biography = $("#biography");
    private final SelenideElement artistName = $("header.text-center");
    private final SelenideElement avatarImg = $("img.avatar-image");

    @Step("Редактировать художника")
    public CreateArtistComponent editArtist() {
        editBtn.click();
        return new CreateArtistComponent();
    }

    @Step("Проверить биографию '{expectedText}'")
    public ArtistCardPage checkBiography(String expectedText) {
        biography.shouldHave(text(expectedText));
        return this;
    }

    @Step("Проверить имя художника '{expectedText}'")
    public ArtistCardPage checkArtistName(String expectedText) {
        artistName.shouldHave(text(expectedText));
        return this;
    }

    @Step("Проверить изображение аватара")
    public ArtistCardPage checkAvatarImg(String expectedBase64Image) {
        avatarImg.shouldHave(attribute("src", expectedBase64Image));
        return this;
    }
}