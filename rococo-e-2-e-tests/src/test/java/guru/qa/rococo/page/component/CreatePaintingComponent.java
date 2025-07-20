package guru.qa.rococo.page.component;

import com.codeborne.selenide.SelenideElement;
import io.qameta.allure.Step;

import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Selenide.$;
import static guru.qa.rococo.condition.DomConditions.validationMessage;

public class CreatePaintingComponent extends BaseComponent<CreatePaintingComponent> {
    public CreatePaintingComponent() {
        super($("[data-testid='modal-component']"));
    }

    private final SelenideElement title = self.$("input[name='title']");
    private final SelenideElement description = self.$("textarea[name='description']");
    private final SelenideElement content = self.$("input[name='content']");
    private final SelenideElement artist = self.$("select[name='authorId']");
    private final SelenideElement museum = self.$("select[name='museumId']");
    private final SelenideElement addBtn = self.$("button[type='submit']");
    private final SelenideElement closeBtn = self.$("button[type='button']");

    @Step("Установить название картины: '{title}'")
    public CreatePaintingComponent setTitle(String title) {
        this.title.setValue(title);
        return this;
    }

    @Step("Установить описание картины: '{description}'")
    public CreatePaintingComponent setDescription(String description) {
        this.description.setValue(description);
        return this;
    }

    @Step("Загрузить изображение картины")
    public CreatePaintingComponent setContent(String content) {
        this.content.uploadFromClasspath(content);
        return this;
    }

    @Step("Выбрать художника: '{artistName}'")
    public CreatePaintingComponent setArtist(String artistName) {
        while (!this.artist.getOptions().find(text(artistName)).exists() && this.artist.getOptions().size() > 1) {
            this.artist.getOptions().last().scrollIntoView(true);
        }
        this.artist.selectOption(artistName);
        return this;
    }

    @Step("Выбрать первого художника из списка")
    public CreatePaintingComponent setArtistFirstValue() {
        this.artist.selectOption(0);
        return this;
    }

    @Step("Выбрать музей: '{museumName}'")
    public CreatePaintingComponent setMuseum(String museumName) {
        while (!this.museum.getOptions().find(text(museumName)).exists() && this.museum.getOptions().size() > 1) {
            this.museum.getOptions().last().scrollIntoView(true);
        }
        this.museum.selectOption(museumName);
        return this;
    }

    @Step("Выбрать первый музей из списка")
    public CreatePaintingComponent setMuseumFirstValue() {
        this.museum.selectOption(0);
        return this;
    }

    @Step("Отправить форму создания картины")
    public CreatePaintingComponent submitForm() {
        addBtn.click();
        return this;
    }

    @Step("Закрыть форму создания картины")
    public void closeForm() {
        closeBtn.click();
    }

    @Step("Заполнить и отправить форму создания картины")
    public void fillForm(String title, String description, String content) {
        setTitle(title);
        setDescription(description);
        setContent(content);
        setArtistFirstValue();
        setMuseumFirstValue();
        submitForm();
    }

    @Step("Заполнить и отправить форму создания картины с выбором художника и музея")
    public void fillForm(String title, String description, String content, String artistName, String museumName) {
        setTitle(title);
        setDescription(description);
        setContent(content);
        if (artistName != null && !artistName.isEmpty()) {
            setArtist(artistName);
        }
        if (museumName != null && !museumName.isEmpty()) {
            setMuseum(museumName);
        }
        submitForm();
    }

    @Step("Проверить сообщение валидации для поля названия")
    public CreatePaintingComponent checkTitleRequiredValidateMessage() {
        checkValidateMessage(title);
        return this;
    }

    @Step("Проверить сообщение валидации для поля описания")
    public CreatePaintingComponent checkDescriptionRequiredValidateMessage() {
        checkValidateMessage(description);
        return this;
    }

    @Step("Проверить сообщение валидации для поля изображения")
    public CreatePaintingComponent checkContentRequiredValidateMessage() {
        checkValidateMessage(content);
        return this;
    }

    @Step("Проверить сообщение валидации для поля художника")
    public CreatePaintingComponent checkArtistRequiredValidateMessage() {
        checkValidateMessage(artist);
        return this;
    }

    private void checkValidateMessage(SelenideElement element) {
        element.shouldHave(validationMessage);
    }
}
