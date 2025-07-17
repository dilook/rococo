package guru.qa.rococo.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Size;

import java.util.UUID;


public record CountryJson(
        @JsonProperty("id")
        UUID id,
        @JsonProperty("name")
        @Size(max = 255, message = "Country name can`t be longer than 255 characters")
        String name
) {
}
