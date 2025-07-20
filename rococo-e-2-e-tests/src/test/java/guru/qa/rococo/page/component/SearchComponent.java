package guru.qa.rococo.page.component;

import com.codeborne.selenide.SelenideElement;
import io.qameta.allure.Step;

import static com.codeborne.selenide.Selenide.$;

public class SearchComponent extends BaseComponent<SearchComponent> {

    public SearchComponent(SelenideElement self) {
        super(self);
    }

    public SearchComponent() {
        super($("[type='search']").parent());
    }

    private final SelenideElement input = self.$("input[type='search']");
    private final SelenideElement searchBtn = self.$("button");


    @Step("Выполнить поиск по тексту '{text}'")
    public SearchComponent search(String text) {
        input.val(text).pressEnter();
        return this;
    }
}
