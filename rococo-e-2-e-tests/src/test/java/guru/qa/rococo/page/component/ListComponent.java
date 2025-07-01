package guru.qa.rococo.page.component;

import com.codeborne.selenide.SelenideElement;

public class ListComponent extends BaseComponent<ListComponent> {

    public ListComponent(SelenideElement self) {
        super(self);
    }

    private final SelenideElement items = self.$("li");

}
