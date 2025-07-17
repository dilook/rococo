package guru.qa.rococo.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Size;

import java.util.UUID;

public record PaintingJson(
        @JsonProperty("id")
        UUID id,
        @JsonProperty("title")
        @Size(max = 255, message = "Title can`t be longer than 255 characters")
        String title,
        @JsonProperty("description")
        @Size(max = 1000, message = "Description can`t be longer than 1000 characters")
        String description,
        @JsonProperty("content")
        @Size(max = 1024 * 1024 * 15, message = "Photo can`t be bigger than 15MB")
        String content,
        @JsonProperty("museum")
        MuseumJson museum,
        @JsonProperty("artist")
        ArtistJson artist
) {
}
