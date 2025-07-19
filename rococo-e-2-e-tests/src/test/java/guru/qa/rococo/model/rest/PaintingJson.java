package guru.qa.rococo.model.rest;

import com.fasterxml.jackson.annotation.JsonProperty;

import javax.annotation.Nonnull;
import java.util.UUID;

public record PaintingJson(
        @JsonProperty("id")
        UUID id,
        @JsonProperty("title")
        String title,
        @JsonProperty("description")
        String description,
        @JsonProperty("content")
        String content,
        @JsonProperty("museum")
        MuseumJson museum,
        @JsonProperty("artist")
        ArtistJson artist
) {

    @Nonnull
    @Override
    public String toString() {
        return "PaintingJson[" +
                "id=" + id +
                ", title=" + title +
                ", description=" + description +
                ", content=" + (content != null ? content.substring(0, Math.min(100, content.length())) + "... [TRUNCATED - original length: " + content.length() + "]" : "null") +
                ", museum=" + museum +
                ", artist=" + artist +
                ']';
    }
}