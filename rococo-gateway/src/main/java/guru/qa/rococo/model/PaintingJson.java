package guru.qa.rococo.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import guru.qa.rococo.data.PaintingEntity;
import jakarta.annotation.Nonnull;
import jakarta.validation.constraints.Size;

import java.nio.charset.StandardCharsets;
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

    public static @Nonnull PaintingJson fromEntity(@Nonnull PaintingEntity entity) {
        return new PaintingJson(
                entity.getId(),
                entity.getTitle(),
                entity.getDescription(),
                entity.getContent() != null && entity.getContent().length > 0 ? new String(entity.getContent(), StandardCharsets.UTF_8) : null,
                entity.getMuseum() != null ? MuseumJson.fromEntity(entity.getMuseum()) : null,
                ArtistJson.fromEntity(entity.getArtist())
        );
    }
}
