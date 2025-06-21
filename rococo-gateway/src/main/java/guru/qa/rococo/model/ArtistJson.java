package guru.qa.rococo.model;

import guru.qa.rococo.data.ArtistEntity;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record ArtistJson(
        UUID id,
        String name,
        String biography,
        byte[] photo
) {
    public static @NotNull ArtistJson fromEntity(@NotNull ArtistEntity artist) {
        return new ArtistJson(artist.getId(), artist.getName(), artist.getBiography(), artist.getPhoto());
    }
}
