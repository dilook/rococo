package guru.qa.rococo.page.component;

import com.codeborne.selenide.SelenideElement;

import static com.codeborne.selenide.Condition.text;

public class CreateMuseumComponent extends BaseComponent<CreateMuseumComponent> {
    public CreateMuseumComponent(SelenideElement self) {
        super(self);
    }

    private final SelenideElement title = self.$("input[name='title']");
    private final SelenideElement country = self.$("select[name='countryId']");
    private final SelenideElement city = self.$("input[name='city']");
    private final SelenideElement photo = self.$("input[name='photo']");
    private final SelenideElement description = self.$("textarea[name='description']");
    private final SelenideElement addBtn = self.$("button[type='submit']");

    public void fillForm(String title, String country, String city, String photo, String description) {
        this.title.setValue(title);
        while (!this.country.getOptions().find(text(country)).exists() && this.country.getOptions().size() < 194) {
            this.country.getOptions().last().scrollIntoView(true);
        }
        this.country.selectOption(country);
        this.city.setValue(city);
        this.photo.uploadFromClasspath(photo);
        this.description.setValue(description);
        addBtn.click();
    }


}
