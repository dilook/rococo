package guru.qa.rococo.page.component;

import com.codeborne.selenide.SelenideElement;

import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Selenide.$;

public class ProfileComponent extends BaseComponent<ProfileComponent> {
    protected ProfileComponent() {
        super($("[aria-label='Профиль']"));
    }

    private final SelenideElement logoutBtn = self.$$("button").find(text("Выйти"));

    public void logout() {
        logoutBtn.click();
    }
}
