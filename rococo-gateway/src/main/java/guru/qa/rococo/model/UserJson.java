package guru.qa.rococo.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.annotation.Nonnull;
import jakarta.validation.constraints.Size;

import java.util.UUID;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record UserJson(
        @JsonProperty("id")
        UUID id,
        @JsonProperty("username")
        String username,
        @JsonProperty("firstname")
        @Size(min = 3, max = 255, message = "First name can`t be shorter than 3 and longer than 255 characters")
        String firstname,
        @JsonProperty("lastname")
        @Size(max = 255, message = "Last name can`t be longer than 255 characters")
        String lastname,
        @JsonProperty("avatar")
        @Size(max = 1024 * 1024 * 15, message = "Photo can`t be bigger than 15MB")
        String avatar
) {


    public @Nonnull UserJson addUsername(@Nonnull String username) {
        return new UserJson(id, username, firstname, lastname, avatar);
    }


}
