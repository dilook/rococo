package guru.qa.rococo.page.component;

import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.SelenideElement;
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


    public void checkItems(String... items) {
        this.items.shouldHave(textsInAnyOrder(items));
    }

    public void clickItem(String item) {
        this.items.find(text(item)).click();
    }


    public void checkItemsSize(int expectedSize) {
        this.items.shouldHave(size(expectedSize))
                .shouldBe(allMatch("", WebElement::isDisplayed));
    }

    public void checkItemsSizeMore(int expectedSize) {
        this.items.shouldHave(sizeGreaterThanOrEqual(expectedSize))
                .shouldBe(allMatch("", WebElement::isDisplayed));
    }

    public void loadNextPage() {
        this.items.last().scrollIntoView(true);
    }

}
