package guru.qa.rococo.page.component;

import com.codeborne.selenide.SelenideElement;

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

    public CreatePaintingComponent setTitle(String title) {
        this.title.setValue(title);
        return this;
    }

    public CreatePaintingComponent setDescription(String description) {
        this.description.setValue(description);
        return this;
    }

    public CreatePaintingComponent setContent(String content) {
        this.content.uploadFromClasspath(content);
        return this;
    }

    public CreatePaintingComponent setArtist(String artistName) {
        while (!this.artist.getOptions().find(text(artistName)).exists() && this.artist.getOptions().size() > 1) {
            this.artist.getOptions().last().scrollIntoView(true);
        }
        this.artist.selectOption(artistName);
        return this;
    }

    public CreatePaintingComponent setArtistFirstValue() {
        this.artist.selectOption(0);
        return this;
    }

    public CreatePaintingComponent setMuseum(String museumName) {
        while (!this.museum.getOptions().find(text(museumName)).exists() && this.museum.getOptions().size() > 1) {
            this.museum.getOptions().last().scrollIntoView(true);
        }
        this.museum.selectOption(museumName);
        return this;
    }

    public CreatePaintingComponent setMuseumFirstValue() {
        this.museum.selectOption(0);
        return this;
    }

    public CreatePaintingComponent submitForm() {
        addBtn.click();
        return this;
    }

    public void closeForm() {
        closeBtn.click();
    }

    public void fillForm(String title, String description, String content) {
        setTitle(title);
        setDescription(description);
        setContent(content);
        setArtistFirstValue();
        setMuseumFirstValue();
        submitForm();
    }

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

    public CreatePaintingComponent checkTitleRequiredValidateMessage() {
        checkValidateMessage(title);
        return this;
    }

    public CreatePaintingComponent checkDescriptionRequiredValidateMessage() {
        checkValidateMessage(description);
        return this;
    }

    public CreatePaintingComponent checkContentRequiredValidateMessage() {
        checkValidateMessage(content);
        return this;
    }

    public CreatePaintingComponent checkArtistRequiredValidateMessage() {
        checkValidateMessage(artist);
        return this;
    }

    private void checkValidateMessage(SelenideElement element) {
        element.shouldHave(validationMessage);
    }
}
