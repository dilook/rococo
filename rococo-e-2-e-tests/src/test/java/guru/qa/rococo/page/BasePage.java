package guru.qa.rococo.page;

import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.SelenideElement;
import io.qameta.allure.Step;

import javax.annotation.Nonnull;

import static com.codeborne.selenide.CollectionCondition.textsInAnyOrder;
import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.$$;

public abstract class BasePage<T extends BasePage<?>> {

    private final ElementsCollection errors = $$(".form__error");
    private final SelenideElement alert = $("[role='alertdialog']");


    public abstract T checkThatPageLoaded();

    @Step("Проверить, что появилось сообщение об ошибке формы: {expectedText}")
    @SuppressWarnings("unchecked")
    @Nonnull
    public T checkErrorsMessage(String... expectedText) {
        errors.should(textsInAnyOrder(expectedText));
        return (T) this;
    }

    @Step("Проверить, что появилось предупреждающее сообщение: {expectedText}")
    @SuppressWarnings("unchecked")
    @Nonnull
    public T checkAlertMessage(String expectedText) {
        alert.should(visible).should(text(expectedText));
        return (T) this;
    }
}
