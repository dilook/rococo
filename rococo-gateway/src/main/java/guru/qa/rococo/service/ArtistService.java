package guru.qa.rococo.service;

import guru.qa.rococo.data.ArtistEntity;
import guru.qa.rococo.data.repository.ArtistRepository;
import guru.qa.rococo.ex.NotFoundException;
import guru.qa.rococo.model.ArtistJson;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.util.UUID;

@Service
public class ArtistService {

    private final ArtistRepository artistRepository;

    public ArtistService(ArtistRepository artistRepository) {
        this.artistRepository = artistRepository;
    }

    public Page<ArtistJson> getAllArtist(Pageable pageable, String name) {
        Page<ArtistEntity> paintings = name == null
                ? artistRepository.findAll(pageable)
                : artistRepository.findByNameContainingIgnoreCase(name, pageable);
        return paintings.map(ArtistJson::fromEntity);
    }

    public ArtistJson getArtistById(UUID id) {
        return artistRepository.findById(id)
                .map(ArtistJson::fromEntity)
                .orElseThrow(() -> new NotFoundException("Artist not found with id: " + id));
    }

    public ArtistJson updateArtist(ArtistJson artistJson) {
        ArtistEntity artistEntity = artistRepository.findById(artistJson.id())
                .orElseThrow(() -> new NotFoundException("Artist not found with id: " + artistJson.id()));

        artistEntity.setName(artistJson.name());
        artistEntity.setBiography(artistJson.biography());
        artistEntity.setPhoto(artistJson.photo() != null ? artistJson.photo().getBytes(StandardCharsets.UTF_8): null);

        return ArtistJson.fromEntity(artistRepository.save(artistEntity));
    }

    public ArtistJson createArtist(ArtistJson artistJson) {
        ArtistEntity artistEntity = new ArtistEntity();
        artistEntity.setName(artistJson.name());
        artistEntity.setBiography(artistJson.biography());
        artistEntity.setPhoto(artistJson.photo() != null ? artistJson.photo().getBytes(StandardCharsets.UTF_8): null);

        return ArtistJson.fromEntity(artistRepository.save(artistEntity));
    }
}
