package guru.qa.rococo.service;

import guru.qa.rococo.data.ArtistEntity;
import guru.qa.rococo.data.MuseumEntity;
import guru.qa.rococo.data.PaintingEntity;
import guru.qa.rococo.data.repository.ArtistRepository;
import guru.qa.rococo.data.repository.MuseumRepository;
import guru.qa.rococo.data.repository.PaintingRepository;
import guru.qa.rococo.model.PaintingJson;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.util.UUID;

@Service
public class PaintingService {

    private final PaintingRepository paintingRepository;
    private final ArtistRepository artistRepository;
    private final MuseumRepository museumRepository;

    public PaintingService(PaintingRepository paintingRepository, ArtistRepository artistRepository, MuseumRepository museumRepository) {
        this.paintingRepository = paintingRepository;
        this.artistRepository = artistRepository;
        this.museumRepository = museumRepository;
    }

    public Page<PaintingJson> getAllPainting(Pageable pageable, String title) {
        Page<PaintingEntity> paintings = title == null
                ? paintingRepository.findAll(pageable)
                : paintingRepository.findByTitleContainingIgnoreCase(title, pageable);
        return paintings.map(PaintingJson::fromEntity);
    }

    public PaintingJson getPaintingById(UUID id) {
        return paintingRepository.findById(id)
                .map(PaintingJson::fromEntity)
                .orElseThrow(() -> new IllegalArgumentException("Painting not found with id: " + id));
    }

    public Page<PaintingJson> getPaintingsByArtistId(UUID artistId, Pageable pageable) {
        return paintingRepository.findByArtistId(artistId, pageable).map(PaintingJson::fromEntity);
    }

    public PaintingJson updatePainting(PaintingJson paintingJson) {
        PaintingEntity paintingEntity = paintingRepository.findById(paintingJson.id())
                .orElseThrow(() -> new IllegalArgumentException("Painting not found with id: " + paintingJson.id()));

        paintingEntity.setTitle(paintingJson.title());
        paintingEntity.setDescription(paintingJson.description());
        paintingEntity.setContent(paintingJson.content() != null ? paintingJson.content().getBytes(StandardCharsets.UTF_8) : null);

        if (paintingJson.artist() != null) {
            ArtistEntity artistEntity = artistRepository.findById(paintingJson.artist().id())
                    .orElseThrow(() -> new IllegalArgumentException("Artist not found with id: " + paintingJson.artist().id()));
            paintingEntity.setArtist(artistEntity);
        }

        if (paintingJson.museum() != null) {
            MuseumEntity museumEntity = museumRepository.findById(paintingJson.museum().id())
                    .orElseThrow(() -> new IllegalArgumentException("Museum not found with id: " + paintingJson.museum().id()));
            paintingEntity.setMuseum(museumEntity);
        }

        return PaintingJson.fromEntity(paintingRepository.save(paintingEntity));
    }

    public PaintingJson createPainting(PaintingJson paintingJson) {
        PaintingEntity paintingEntity = new PaintingEntity();
        paintingEntity.setTitle(paintingJson.title());
        paintingEntity.setDescription(paintingJson.description());
        paintingEntity.setContent(paintingJson.content() != null ? paintingJson.content().getBytes(StandardCharsets.UTF_8) : null);

        if (paintingJson.artist() != null) {
            ArtistEntity artistEntity = artistRepository.findById(paintingJson.artist().id())
                    .orElseThrow(() -> new IllegalArgumentException("Artist not found with id: " + paintingJson.artist().id()));
            paintingEntity.setArtist(artistEntity);
        } else {
            throw new IllegalArgumentException("Artist is required for a painting");
        }

        if (paintingJson.museum() != null && paintingJson.museum().id() != null) {
            MuseumEntity museumEntity = museumRepository.findById(paintingJson.museum().id())
                    .orElseThrow(() -> new IllegalArgumentException("Museum not found with id: " + paintingJson.museum().id()));
            paintingEntity.setMuseum(museumEntity);
        }

        return PaintingJson.fromEntity(paintingRepository.save(paintingEntity));
    }
}
