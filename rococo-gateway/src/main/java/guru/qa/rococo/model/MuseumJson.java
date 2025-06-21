package guru.qa.rococo.model;

import guru.qa.rococo.data.MuseumEntity;
import jakarta.annotation.Nonnull;

import java.util.UUID;

public record MuseumJson(
        UUID id,
        String title,
        String description,
        byte[] photo,
        Geo geo
) {
    public static @Nonnull MuseumJson fromEntity(@Nonnull MuseumEntity museum) {
        return new MuseumJson(
                museum.getId(),
                museum.getTitle(),
                museum.getDescription(),
                museum.getPhoto(),
                new Geo(
                        museum.getCity(),
                        CountryJson.fromEntity(museum.getCountry())
                )
        );
    }

    record Geo(String city, CountryJson country) {
    }
}
