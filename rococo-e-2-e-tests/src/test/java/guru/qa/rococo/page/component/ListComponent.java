package guru.qa.rococo.page.component;

import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.SelenideElement;
import io.qameta.allure.Step;
import org.openqa.selenium.WebElement;

import static com.codeborne.selenide.CollectionCondition.allMatch;
import static com.codeborne.selenide.CollectionCondition.size;
import static com.codeborne.selenide.CollectionCondition.sizeGreaterThanOrEqual;
import static com.codeborne.selenide.CollectionCondition.textsInAnyOrder;
import static com.codeborne.selenide.Condition.text;

public class ListComponent extends BaseComponent<ListComponent> {

    public ListComponent(SelenideElement self) {
        super(self);
    }

    private final ElementsCollection items = self.$$("li");


    @Step("Проверить элементы списка")
    public void checkItems(String... items) {
        this.items.shouldHave(textsInAnyOrder(items));
    }

    @Step("Кликнуть по элементу '{item}' в списке")
    public void clickItem(String item) {
        this.items.find(text(item)).click();
    }

    @Step("Проверить количество элементов в списке: {expectedSize}")
    public void checkItemsSize(int expectedSize) {
        this.items.shouldHave(size(expectedSize))
                .shouldBe(allMatch("visible", WebElement::isDisplayed));
    }

    @Step("Проверить, что количество элементов в списке больше или равно: {expectedSize}")
    public void checkItemsSizeMore(int expectedSize) {
        this.items.shouldHave(sizeGreaterThanOrEqual(expectedSize))
                .shouldBe(allMatch("visible", WebElement::isDisplayed));
    }

    @Step("Загрузить следующую страницу списка")
    public void loadNextPage() {
        this.items.last().scrollIntoView(true);
    }

}
