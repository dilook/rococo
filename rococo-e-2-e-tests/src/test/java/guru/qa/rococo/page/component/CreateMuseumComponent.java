package guru.qa.rococo.page.component;

import com.codeborne.selenide.SelenideElement;
import io.qameta.allure.Step;

import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Selenide.$;

public class CreateMuseumComponent extends BaseComponent<CreateMuseumComponent> {
    public CreateMuseumComponent() {
        super($("[data-testid='modal-component']"));
    }

    private final SelenideElement title = self.$("input[name='title']");
    private final SelenideElement country = self.$("select[name='countryId']");
    private final SelenideElement city = self.$("input[name='city']");
    private final SelenideElement photo = self.$("input[name='photo']");
    private final SelenideElement description = self.$("textarea[name='description']");
    private final SelenideElement addBtn = self.$("button[type='submit']");
    private final SelenideElement closeBtn = self.$("button[type='button']");


    @Step("Установить название музея: '{title}'")
    public CreateMuseumComponent setTitle(String title) {
        this.title.setValue(title);
        return this;
    }

    @Step("Выбрать страну: '{country}'")
    public CreateMuseumComponent setCountry(String country) {
        while (!this.country.getOptions().find(text(country)).exists() && this.country.getOptions().size() < 194) {
            this.country.getOptions().last().scrollIntoView(true);
        }
        this.country.selectOption(country);
        return this;
    }

    @Step("Установить город: '{city}'")
    public CreateMuseumComponent setCity(String city) {
        this.city.setValue(city);
        return this;
    }

    @Step("Загрузить фото музея")
    public CreateMuseumComponent setPhoto(String photo) {
        this.photo.uploadFromClasspath(photo);
        return this;
    }

    @Step("Установить описание музея: '{description}'")
    public CreateMuseumComponent setDescription(String description) {
        this.description.setValue(description);
        return this;
    }

    @Step("Отправить форму создания музея")
    public CreateMuseumComponent submitForm() {
        addBtn.click();
        return this;
    }

    @Step("Закрыть форму создания музея")
    public void closeForm() {
        closeBtn.click();
    }

    @Step("Заполнить и отправить форму создания музея")
    public void fillForm(String title, String country, String city, String photo, String description) {
        setTitle(title);
        setCountry(country);
        setCity(city);
        setPhoto(photo);
        setDescription(description);
        submitForm();
    }


}
