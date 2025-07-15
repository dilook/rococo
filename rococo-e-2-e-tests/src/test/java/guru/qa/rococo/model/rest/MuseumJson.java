package guru.qa.rococo.model.rest;

import com.fasterxml.jackson.annotation.JsonProperty;

import javax.annotation.Nonnull;
import java.util.UUID;

public record MuseumJson(
        @JsonProperty("id")
        UUID id,
        @JsonProperty("title")
        String title,
        @JsonProperty("description")
        String description,
        @JsonProperty("photo")
        String photo,
        @JsonProperty("geo")
        Geo geo
) {

    @Nonnull
    @Override
    public String toString() {
        return "MuseumJson[" +
                "id=" + id +
                ", title=" + title +
                ", description=" + description +
                ", photo=" + photo.substring(0, 100) +  "... [TRUNCATED - original length: " + photo.length() + "]" +
                ", geo=" + geo +
                ']';
    }

    public record Geo(
            @JsonProperty("city")
            String city,
            @JsonProperty("country")
            CountryJson country
    ) {}
}
