package guru.qa.rococo.model;

import guru.qa.rococo.data.PaintingEntity;
import jakarta.annotation.Nonnull;

import java.nio.charset.StandardCharsets;
import java.util.UUID;

public record PaintingJson(
        UUID id,
        String title,
        String description,
        String content,
        MuseumJson museum,
        ArtistJson artist
) {

public static @Nonnull PaintingJson fromEntity(@Nonnull PaintingEntity entity) {
    return new PaintingJson(
            entity.getId(),
            entity.getTitle(),
            entity.getDescription(),
            entity.getContent() != null && entity.getContent().length > 0 ? new String(entity.getContent(), StandardCharsets.UTF_8) : null,
            MuseumJson.fromEntity(entity.getMuseum()),
            ArtistJson.fromEntity(entity.getArtist())
    );
}
}
