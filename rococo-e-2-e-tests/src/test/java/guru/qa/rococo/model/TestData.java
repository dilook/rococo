package guru.qa.rococo.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import guru.qa.rococo.model.rest.UserJson;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;

@ParametersAreNonnullByDefault
public record TestData(@JsonIgnore String password) {

    private @Nonnull String[] extractUsernames(List<UserJson> users) {
        return users.stream().map(UserJson::username).toArray(String[]::new);
    }
}
