package guru.qa.rococo.page;

import com.codeborne.selenide.SelenideElement;
import guru.qa.rococo.config.Config;
import guru.qa.rococo.page.component.CreateMuseumComponent;
import guru.qa.rococo.page.component.ListComponent;
import guru.qa.rococo.page.component.SearchComponent;
import io.qameta.allure.Step;
import lombok.Getter;

import static com.codeborne.selenide.Condition.exist;
import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;

public class MuseumPage extends BasePage<MuseumPage> {
    public static final String URL =  Config.getInstance().frontUrl() + "museum";

    private final SelenideElement header = $("h2");
    private final SearchComponent search = new SearchComponent();
    private final SelenideElement addBtn = $("#add-button");

    @Getter
    private final CreateMuseumComponent modalForm = new CreateMuseumComponent();
    private final ListComponent listItems = new ListComponent($("#list-museum"));


    @Override
    @Step("Проверить, что страница музеев загружена")
    public MuseumPage checkThatPageLoaded() {
        header.shouldHave(text("Музеи"));
        return this;
    }

    @Step("Проверить музей '{title}'")
    public MuseumPage checkMuseum(String title) {
        search.search(title);
        listItems.checkItems(title);
        return this;
    }

    @Step("Кликнуть по музею '{title}'")
    public MuseumCardPage clickTo(String title) {
        search.search(title);
        listItems.clickItem(title);
        return new MuseumCardPage();
    }

    @Step("Проверить количество музеев: {expectedSize}")
    public MuseumPage checkMuseumsSize(int expectedSize) {
        listItems.checkItemsSize(expectedSize);
        return this;
    }

    @Step("Загрузить следующую страницу с музеями")
    public MuseumPage loadNextPage() {
        listItems.loadNextPage();
        return this;
    }

    @Step("Добавить музей '{museumTitle}' в стране '{country}', городе '{city}'")
    public MuseumPage addMuseum(String museumTitle, String country, String city, String imgClasspath, String description) {
        addBtn.shouldBe(visible).click();
        modalForm.fillForm(museumTitle, country, city, imgClasspath, description);
        return this;
    }

    @Step("Проверить, что кнопка добавления музея не существует")
    public MuseumPage checkAddButtonNotExist() {
        header.shouldNot(exist);
        return this;
    }
}
