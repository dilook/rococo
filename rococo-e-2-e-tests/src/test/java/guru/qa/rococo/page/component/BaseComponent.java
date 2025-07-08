package guru.qa.rococo.page.component;

import com.codeborne.selenide.SelenideElement;
import io.qameta.allure.Step;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;

@ParametersAreNonnullByDefault
public abstract class BaseComponent<T extends BaseComponent<?>> {

  protected final SelenideElement self;

  protected BaseComponent(SelenideElement self) {
    this.self = self;
  }

  private final SelenideElement alert = $("[role='alertdialog']");


  @Nonnull
  public SelenideElement getSelf() {
    return self;
  }


  @Step("Check that alert message appears: {expectedText}")
  @SuppressWarnings("unchecked")
  @Nonnull
  public T checkAlertMessage(String expectedText) {
    alert.should(visible).should(text(expectedText));
    return (T) this;
  }
}
