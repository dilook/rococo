package guru.qa.rococo.condition;

import com.codeborne.selenide.CheckResult;
import com.codeborne.selenide.Driver;
import com.codeborne.selenide.WebElementCondition;
import org.jetbrains.annotations.NotNull;
import org.openqa.selenium.WebElement;

public class DomConditions {

    public static final WebElementCondition validationMessage = new WebElementCondition("validationMessage") {
        @NotNull
        @Override
        public CheckResult check(Driver driver, WebElement element) {
            String propertyValue = element.getDomProperty("validationMessage");
            return new CheckResult(
                    propertyValue != null && !propertyValue.isEmpty(),
                    String.format("validationMessage=\"%s\"", propertyValue)
            );
        }
    };

}
