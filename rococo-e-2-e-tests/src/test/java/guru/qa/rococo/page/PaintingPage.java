package guru.qa.rococo.page;

import com.codeborne.selenide.SelenideElement;
import guru.qa.rococo.config.Config;
import guru.qa.rococo.page.component.CreatePaintingComponent;
import guru.qa.rococo.page.component.ListComponent;
import guru.qa.rococo.page.component.SearchComponent;
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
    public PaintingPage checkThatPageLoaded() {
        header.shouldHave(text("Картины"));
        return this;
    }

    public PaintingPage checkPainting(String title) {
        search.search(title);
        listItems.checkItems(title);
        return this;
    }

    public PaintingCardPage clickTo(String title) {
        search.search(title);
        listItems.clickItem(title);
        return new PaintingCardPage();
    }

    public PaintingPage checkPaintingsSize(int expectedSize) {
        listItems.checkItemsSize(expectedSize);
        return this;
    }

    public PaintingPage loadNextPage() {
        listItems.loadNextPage();
        return this;
    }

    public CreatePaintingComponent addPainting() {
        addBtn.shouldBe(visible).click();
        return modalForm;
    }

    public PaintingPage addPainting(String title, String description, String imgClasspath) {
        addBtn.shouldBe(visible).click();
        modalForm.fillForm(title, description, imgClasspath);
        return this;
    }

    public PaintingPage addPainting(String title, String description, String imgClasspath, String artistName, String museumName) {
        addBtn.shouldBe(visible).click();
        modalForm.fillForm(title, description, imgClasspath, artistName, museumName);
        return this;
    }

    public PaintingPage checkAddButtonNotExist() {
        addBtn.shouldNot(exist);
        return this;
    }
}
