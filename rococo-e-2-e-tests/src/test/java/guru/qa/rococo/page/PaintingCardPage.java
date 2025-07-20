package guru.qa.rococo.page;

import com.codeborne.selenide.SelenideElement;
import guru.qa.rococo.page.component.CreatePaintingComponent;
import io.qameta.allure.Step;

import static com.codeborne.selenide.Condition.attribute;
import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Selenide.$;

public class PaintingCardPage {

    private final SelenideElement editBtn = $("[data-testid=edit-painting]");
    private final SelenideElement description = $("#description");
    private final SelenideElement paintingTitle = $("header.text-center");
    private final SelenideElement paintingImg = $("[data-testid=painting-image]");
    private final SelenideElement artistName = $("#artistName");

    @Step("Редактировать картину")
    public CreatePaintingComponent editPainting() {
        editBtn.click();
        return new CreatePaintingComponent();
    }

    @Step("Проверить описание '{expectedText}'")
    public PaintingCardPage checkDescription(String expectedText) {
        description.shouldHave(text(expectedText));
        return this;
    }

    @Step("Проверить название картины '{expectedText}'")
    public PaintingCardPage checkPaintingTitle(String expectedText) {
        paintingTitle.shouldHave(text(expectedText));
        return this;
    }

    @Step("Проверить изображение картины")
    public PaintingCardPage checkPaintingImg(String expectedBase64Image) {
        paintingImg.shouldHave(attribute("src", expectedBase64Image));
        return this;
    }

    @Step("Проверить имя художника '{expectedText}'")
    public PaintingCardPage checkArtistName(String expectedText) {
        artistName.shouldHave(text(expectedText));
        return this;
    }

}