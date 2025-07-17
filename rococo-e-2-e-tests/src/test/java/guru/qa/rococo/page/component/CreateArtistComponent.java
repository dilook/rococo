package guru.qa.rococo.page.component;

import com.codeborne.selenide.SelenideElement;

import static com.codeborne.selenide.Selenide.$;
import static guru.qa.rococo.condition.DomConditions.validationMessage;

public class CreateArtistComponent extends BaseComponent<CreateArtistComponent> {
    public CreateArtistComponent() {
        super($("[data-testid='modal-component']"));
    }

    private final SelenideElement name = self.$("input[name='name']");
    private final SelenideElement biography = self.$("textarea[name='biography']");
    private final SelenideElement photo = self.$("input[name='photo']");
    private final SelenideElement addBtn = self.$("button[type='submit']");
    private final SelenideElement closeBtn = self.$("button[type='button']");

    public CreateArtistComponent setName(String name) {
        this.name.setValue(name);
        return this;
    }

    public CreateArtistComponent setBiography(String biography) {
        this.biography.setValue(biography);
        return this;
    }

    public CreateArtistComponent setPhoto(String photo) {
        this.photo.uploadFromClasspath(photo);
        return this;
    }

    public CreateArtistComponent submitForm() {
        addBtn.click();
        return this;
    }

    public void closeForm() {
        closeBtn.click();
    }

    public void fillForm(String name, String biography, String photo) {
        setName(name);
        setBiography(biography);
        setPhoto(photo);
        submitForm();
    }

    public CreateArtistComponent checkNameRequiredValidateMessage() {
        checkValidateMessage(name);
        return this;
    }

    public CreateArtistComponent checkBioRequiredValidateMessage() {
        checkValidateMessage(biography);
        return this;
    }

    public CreateArtistComponent checkPhotoRequiredValidateMessage() {
        checkValidateMessage(photo);
        return this;
    }

    private void checkValidateMessage(SelenideElement element) {
        element.shouldHave(validationMessage);
    }
}