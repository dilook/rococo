package guru.qa.rococo.page.component;

import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.SelenideElement;

import static com.codeborne.selenide.CollectionCondition.size;
import static com.codeborne.selenide.CollectionCondition.textsInAnyOrder;

public class ListComponent extends BaseComponent<ListComponent> {

    public ListComponent(SelenideElement self) {
        super(self);
    }

    private final ElementsCollection items = self.$$("li");


    public void checkItems(String... items) {
        this.items.shouldHave(textsInAnyOrder(items));
    }

    public void checkItemsSize(int expectedSize) {
        this.items.shouldHave(size(expectedSize));
    }

}
