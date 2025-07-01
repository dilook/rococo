package guru.qa.rococo.page;

import com.codeborne.selenide.SelenideElement;
import guru.qa.rococo.config.Config;
import guru.qa.rococo.page.component.SearchComponent;

import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Selenide.$;

public class MuseumPage extends BasePage<MuseumPage> {
    public static final String URL =  Config.getInstance().frontUrl() + "museum";

    private final SelenideElement header = $("h2");
    private final SearchComponent search = new SearchComponent();

    @Override
    public MuseumPage checkThatPageLoaded() {
        header.shouldHave(text("Музеи"));
        return this;
    }

    public MuseumPage checkMuseum(String title) {
        search.search(title);
        $("h1").shouldHave(text(title));
        return this;
    }
}
