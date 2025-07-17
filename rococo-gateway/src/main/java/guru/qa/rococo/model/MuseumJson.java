package guru.qa.rococo.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Size;

import java.util.UUID;

public record MuseumJson(
        @JsonProperty("id")
        UUID id,
        @JsonProperty("title")
        @Size(max = 50, message = "Title name can`t be longer than 50 characters")
        String title,
        @JsonProperty("description")
        @Size(max = 1000, message = "Description can`t be longer than 1000 characters")
        String description,
        @JsonProperty("photo")
        @Size(max = 1024 * 1024 * 15, message = "Photo can`t be bigger than 15MB")
        String photo,
        @JsonProperty("geo")
        Geo geo
) {
    public record Geo(
            @JsonProperty("city")
            @Size(max = 255, message = "City name can`t be longer than 255 characters")
            String city,
            @JsonProperty("country")
            CountryJson country
    ) {}
}
