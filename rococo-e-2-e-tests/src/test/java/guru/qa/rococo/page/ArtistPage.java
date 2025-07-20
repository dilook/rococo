package guru.qa.rococo.page;

import com.codeborne.selenide.SelenideElement;
import guru.qa.rococo.config.Config;
import guru.qa.rococo.page.component.CreateArtistComponent;
import guru.qa.rococo.page.component.ListComponent;
import guru.qa.rococo.page.component.SearchComponent;
import io.qameta.allure.Step;
import lombok.Getter;

import static com.codeborne.selenide.Condition.exist;
import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;

public class ArtistPage extends BasePage<ArtistPage> {
    public static final String URL = Config.getInstance().frontUrl() + "artist";

    private final SelenideElement header = $("h2");
    private final SearchComponent search = new SearchComponent();
    private final SelenideElement addBtn = $("#add-button");

    @Getter
    private final CreateArtistComponent modalForm = new CreateArtistComponent();
    private final ListComponent listItems = new ListComponent($("#list-artist"));

    @Override
    @Step("Проверить, что страница художников загружена")
    public ArtistPage checkThatPageLoaded() {
        header.shouldHave(text("Художники"));
        return this;
    }

    @Step("Добавить художника")
    public CreateArtistComponent addArtist() {
        addBtn.shouldBe(visible).click();
        return modalForm;
    }

    @Step("Проверить художника '{name}'")
    public ArtistPage checkArtist(String name) {
        search.search(name);
        listItems.checkItems(name);
        return this;
    }

    @Step("Кликнуть по художнику '{name}'")
    public ArtistCardPage clickTo(String name) {
        search.search(name);
        listItems.clickItem(name);
        return new ArtistCardPage();
    }

    @Step("Проверить количество художников: {expectedSize}")
    public ArtistPage checkArtistsSize(int expectedSize) {
        listItems.checkItemsSize(expectedSize);
        return this;
    }

    @Step("Проверить, что количество художников больше или равно: {expectedSize}")
    public ArtistPage checkArtistsGreaterOrEqThan(int expectedSize) {
        listItems.checkItemsSizeMore(expectedSize);
        return this;
    }

    @Step("Загрузить следующую страницу с художниками")
    public ArtistPage loadNextPage() {
        listItems.loadNextPage();
        return this;
    }

    @Step("Добавить художника '{artistName}' с биографией '{biography}'")
    public ArtistPage addArtist(String artistName, String biography, String imgClasspath) {
        addBtn.shouldBe(visible).click();
        modalForm.fillForm(artistName, biography, imgClasspath);
        return this;
    }

    @Step("Проверить, что кнопка добавления художника не существует")
    public ArtistPage checkAddButtonNotExist() {
        addBtn.shouldNot(exist);
        return this;
    }
}
