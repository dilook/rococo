package guru.qa.rococo.page;

import com.codeborne.selenide.SelenideElement;
import guru.qa.rococo.config.Config;
import guru.qa.rococo.page.component.CreateMuseumComponent;
import guru.qa.rococo.page.component.ListComponent;
import guru.qa.rococo.page.component.SearchComponent;

import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Selenide.$;

public class MuseumPage extends BasePage<MuseumPage> {
    public static final String URL =  Config.getInstance().frontUrl() + "museum";

    private final SelenideElement header = $("h2");
    private final SearchComponent search = new SearchComponent();
    private final SelenideElement addBtn = $("button.btn");

    private final CreateMuseumComponent createMuseum = new CreateMuseumComponent($("[data-testid='modal-component']"));
    private final ListComponent listItems = new ListComponent($("#list-museum"));


    @Override
    public MuseumPage checkThatPageLoaded() {
        header.shouldHave(text("Музеи"));
        return this;
    }

    public MuseumPage checkMuseum(String title) {
        search.search(title);
        listItems.checkItems(title);
        return this;
    }

    public MuseumPage checkMuseumsSize(int expectedSize) {
        listItems.checkItemsSize(expectedSize);
        return this;
    }

    public MuseumPage addMuseum(String museumTitle, String country, String city, String imgClasspath, String description) {
        addBtn.click();
        createMuseum.fillForm(museumTitle, country, city, imgClasspath, description);
        return this;
    }
}
