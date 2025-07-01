package guru.qa.rococo.page;

import com.codeborne.selenide.CollectionCondition;
import com.codeborne.selenide.ElementsCollection;
import io.qameta.allure.Step;

import javax.annotation.Nonnull;

import static com.codeborne.selenide.Selenide.$$;

public abstract class BasePage<T extends BasePage<?>> {

    private final ElementsCollection errors = $$(".form__error");


    public abstract T checkThatPageLoaded();

    @Step("Check that form error message appears: {expectedText}")
    @SuppressWarnings("unchecked")
    @Nonnull
    public T checkErrorsMessage(String... expectedText) {
        errors.should(CollectionCondition.textsInAnyOrder(expectedText));
        return (T) this;
    }
}
