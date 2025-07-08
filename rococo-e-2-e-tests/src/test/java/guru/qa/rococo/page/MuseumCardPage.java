package guru.qa.rococo.page;

import com.codeborne.selenide.SelenideElement;
import guru.qa.rococo.page.component.CreateMuseumComponent;

import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Selenide.$;

public class MuseumCardPage {

    private final SelenideElement editBtn = $("[data-testid=edit-museum]");
    private final SelenideElement description = $("#description");
    private final SelenideElement countryCity = $("div.text-center");



    public CreateMuseumComponent editMuseum() {
        editBtn.click();
        return new CreateMuseumComponent();
    }

    public MuseumCardPage checkDescription(String expectedText) {
        description.shouldHave(text(expectedText));
        return this;
    }

    public MuseumCardPage checkCountryCity(String expectedText) {
        countryCity.shouldHave(text(expectedText));
        return this;
    }
}
