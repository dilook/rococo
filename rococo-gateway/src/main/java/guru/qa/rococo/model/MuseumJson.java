package guru.qa.rococo.model;

import guru.qa.rococo.data.MuseumEntity;
import jakarta.annotation.Nonnull;

import java.nio.charset.StandardCharsets;
import java.util.UUID;

public record MuseumJson(
        UUID id,
        String title,
        String description,
        String photo,
        Geo geo
) {
    public static @Nonnull MuseumJson fromEntity(@Nonnull MuseumEntity museum) {
        return new MuseumJson(
                museum.getId(),
                museum.getTitle(),
                museum.getDescription(),
                museum.getPhoto() != null && museum.getPhoto().length > 0 ? new String(museum.getPhoto(), StandardCharsets.UTF_8) : null,
                new Geo(
                        museum.getCity(),
                        CountryJson.fromEntity(museum.getCountry())
                )
        );
    }

    public record Geo(String city, CountryJson country) {
    }
}
