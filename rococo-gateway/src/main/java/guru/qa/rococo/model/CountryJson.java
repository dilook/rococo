package guru.qa.rococo.model;

import guru.qa.rococo.data.CountryEntity;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record CountryJson(
        UUID id,
        String name
) {
    public static @NotNull CountryJson fromEntity(@NotNull CountryEntity country) {
        return new CountryJson(country.getId(), country.getName());
    }
}
