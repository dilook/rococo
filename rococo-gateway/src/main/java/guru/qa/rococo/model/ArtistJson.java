package guru.qa.rococo.model;

import guru.qa.rococo.data.ArtistEntity;
import jakarta.validation.constraints.NotNull;

import java.nio.charset.StandardCharsets;
import java.util.UUID;

public record ArtistJson(
        UUID id,
        String name,
        String biography,
        String photo
) {
    public static @NotNull ArtistJson fromEntity(@NotNull ArtistEntity artist) {
        return new ArtistJson(
                artist.getId(),
                artist.getName(),
                artist.getBiography(),
                artist.getPhoto() != null && artist.getPhoto().length > 0 ? new String(artist.getPhoto(), StandardCharsets.UTF_8) : null
        );
    }
}
