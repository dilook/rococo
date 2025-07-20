package guru.qa.rococo.page.component;

import com.codeborne.selenide.SelenideElement;
import io.qameta.allure.Step;

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

    @Step("Установить имя художника: '{name}'")
    public CreateArtistComponent setName(String name) {
        this.name.setValue(name);
        return this;
    }

    @Step("Установить биографию художника: '{biography}'")
    public CreateArtistComponent setBiography(String biography) {
        this.biography.setValue(biography);
        return this;
    }

    @Step("Загрузить фото художника")
    public CreateArtistComponent setPhoto(String photo) {
        this.photo.uploadFromClasspath(photo);
        return this;
    }

    @Step("Отправить форму создания художника")
    public CreateArtistComponent submitForm() {
        addBtn.click();
        return this;
    }

    @Step("Закрыть форму создания художника")
    public void closeForm() {
        closeBtn.click();
    }

    @Step("Заполнить и отправить форму создания художника")
    public void fillForm(String name, String biography, String photo) {
        setName(name);
        setBiography(biography);
        setPhoto(photo);
        submitForm();
    }

    @Step("Проверить сообщение валидации для поля имени")
    public CreateArtistComponent checkNameRequiredValidateMessage() {
        checkValidateMessage(name);
        return this;
    }

    @Step("Проверить сообщение валидации для поля биографии")
    public CreateArtistComponent checkBioRequiredValidateMessage() {
        checkValidateMessage(biography);
        return this;
    }

    @Step("Проверить сообщение валидации для поля фото")
    public CreateArtistComponent checkPhotoRequiredValidateMessage() {
        checkValidateMessage(photo);
        return this;
    }

    private void checkValidateMessage(SelenideElement element) {
        element.shouldHave(validationMessage);
    }
}