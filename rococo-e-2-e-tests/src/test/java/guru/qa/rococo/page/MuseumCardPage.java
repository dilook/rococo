package guru.qa.rococo.page;

import com.codeborne.selenide.SelenideElement;
import guru.qa.rococo.page.component.CreateMuseumComponent;
import io.qameta.allure.Step;

import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Selenide.$;

public class MuseumCardPage {

    private final SelenideElement editBtn = $("[data-testid=edit-museum]");
    private final SelenideElement description = $("#description");
    private final SelenideElement countryCity = $("div.text-center");



    @Step("Редактировать музей")
    public CreateMuseumComponent editMuseum() {
        editBtn.click();
        return new CreateMuseumComponent();
    }

    @Step("Проверить описание '{expectedText}'")
    public MuseumCardPage checkDescription(String expectedText) {
        description.shouldHave(text(expectedText));
        return this;
    }

    @Step("Проверить страну и город '{expectedText}'")
    public MuseumCardPage checkCountryCity(String expectedText) {
        countryCity.shouldHave(text(expectedText));
        return this;
    }
}
