package guru.qa.rococo.page.component;

import com.codeborne.selenide.SelenideElement;

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


    public CreateMuseumComponent setTitle(String title) {
        this.title.setValue(title);
        return this;
    }

    public CreateMuseumComponent setCountry(String country) {
        while (!this.country.getOptions().find(text(country)).exists() && this.country.getOptions().size() < 194) {
            this.country.getOptions().last().scrollIntoView(true);
        }
        this.country.selectOption(country);
        return this;
    }

    public CreateMuseumComponent setCity(String city) {
        this.city.setValue(city);
        return this;
    }

    public CreateMuseumComponent setPhoto(String photo) {
        this.photo.uploadFromClasspath(photo);
        return this;
    }

    public CreateMuseumComponent setDescription(String description) {
        this.description.setValue(description);
        return this;
    }

    public CreateMuseumComponent submitForm() {
        addBtn.click();
        return this;
    }

    public void closeForm() {
        closeBtn.click();
    }

    public void fillForm(String title, String country, String city, String photo, String description) {
        setTitle(title);
        setCountry(country);
        setCity(city);
        setPhoto(photo);
        setDescription(description);
        submitForm();
    }


}
