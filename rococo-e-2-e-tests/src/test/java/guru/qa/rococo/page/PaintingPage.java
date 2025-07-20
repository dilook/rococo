package guru.qa.rococo.page;

import com.codeborne.selenide.SelenideElement;
import guru.qa.rococo.config.Config;
import guru.qa.rococo.page.component.CreatePaintingComponent;
import guru.qa.rococo.page.component.ListComponent;
import guru.qa.rococo.page.component.SearchComponent;
import io.qameta.allure.Step;
import lombok.Getter;

import static com.codeborne.selenide.Condition.exist;
import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;

public class PaintingPage extends BasePage<PaintingPage> {
    public static final String URL = Config.getInstance().frontUrl() + "painting";

    private final SelenideElement header = $("h2");
    private final SearchComponent search = new SearchComponent();
    private final SelenideElement addBtn = $("#add-button");

    @Getter
    private final CreatePaintingComponent modalForm = new CreatePaintingComponent();
    private final ListComponent listItems = new ListComponent($("#list-painting"));

    @Override
    @Step("Проверить, что страница картин загружена")
    public PaintingPage checkThatPageLoaded() {
        header.shouldHave(text("Картины"));
        return this;
    }

    @Step("Проверить картину '{title}'")
    public PaintingPage checkPainting(String title) {
        search.search(title);
        listItems.checkItems(title);
        return this;
    }

    @Step("Кликнуть по картине '{title}'")
    public PaintingCardPage clickTo(String title) {
        search.search(title);
        listItems.clickItem(title);
        return new PaintingCardPage();
    }

    @Step("Проверить количество картин: {expectedSize}")
    public PaintingPage checkPaintingsSize(int expectedSize) {
        listItems.checkItemsSize(expectedSize);
        return this;
    }

    @Step("Загрузить следующую страницу с картинами")
    public PaintingPage loadNextPage() {
        listItems.loadNextPage();
        return this;
    }

    @Step("Добавить картину")
    public CreatePaintingComponent addPainting() {
        addBtn.shouldBe(visible).click();
        return modalForm;
    }

    @Step("Добавить картину '{title}' с описанием '{description}'")
    public PaintingPage addPainting(String title, String description, String imgClasspath) {
        addBtn.shouldBe(visible).click();
        modalForm.fillForm(title, description, imgClasspath);
        return this;
    }

    @Step("Добавить картину '{title}' художника '{artistName}' в музей '{museumName}'")
    public PaintingPage addPainting(String title, String description, String imgClasspath, String artistName, String museumName) {
        addBtn.shouldBe(visible).click();
        modalForm.fillForm(title, description, imgClasspath, artistName, museumName);
        return this;
    }

    @Step("Проверить, что кнопка добавления картины не существует")
    public PaintingPage checkAddButtonNotExist() {
        addBtn.shouldNot(exist);
        return this;
    }
}
