package guru.qa.rococo.model.rest;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import guru.qa.rococo.model.TestData;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.UUID;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record UserJson(
        @JsonProperty("id")
        UUID id,
        @JsonProperty("username")
        String username,
        @JsonProperty("firstname")
        String firstname,
        @JsonProperty("lastname")
        String lastname,
        @JsonProperty("avatar")
        String avatar,
        @JsonIgnore
        TestData testData
) {

    public UserJson(@Nonnull String username, @Nullable TestData testData) {
        this(null, username, null, null, null, testData);
    }

    public @Nonnull UserJson addUsername(@Nonnull String username) {
        return new UserJson(id, username, firstname, lastname, avatar, null);
    }

    public @Nonnull UserJson addTestData(@Nonnull TestData testData) {
        return new UserJson(
                id, username, firstname, lastname, avatar, testData
        );
    }
}
