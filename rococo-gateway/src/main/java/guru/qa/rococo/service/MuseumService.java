package guru.qa.rococo.service;

import guru.qa.rococo.data.CountryEntity;
import guru.qa.rococo.data.MuseumEntity;
import guru.qa.rococo.data.repository.CountryRepository;
import guru.qa.rococo.data.repository.MuseumRepository;
import guru.qa.rococo.ex.NotFoundException;
import guru.qa.rococo.ex.RequiredParamException;
import guru.qa.rococo.model.MuseumJson;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.util.UUID;

@Service
public class MuseumService {

    private final MuseumRepository museumRepository;
    private final CountryRepository countryRepository;

    public MuseumService(MuseumRepository museumRepository, CountryRepository countryRepository) {
        this.museumRepository = museumRepository;
        this.countryRepository = countryRepository;
    }

    public Page<MuseumJson> getAllMuseum(Pageable pageable, String title) {
        Page<MuseumEntity> paintings = title == null
                ? museumRepository.findAll(pageable)
                : museumRepository.findByTitleContainingIgnoreCase(title, pageable);
        return paintings.map(MuseumJson::fromEntity);
    }

    public MuseumJson getMuseumById(UUID id) {
        return museumRepository.findById(id)
                .map(MuseumJson::fromEntity)
                .orElseThrow(() -> new NotFoundException("Museum not found with id: " + id));
    }

    public MuseumJson updateMuseum(MuseumJson museumJson) {
        MuseumEntity museumEntity = museumRepository.findById(museumJson.id())
                .orElseThrow(() -> new NotFoundException("Museum not found with id: " + museumJson.id()));

        museumEntity.setTitle(museumJson.title());
        museumEntity.setDescription(museumJson.description());
        museumEntity.setPhoto(museumJson.photo() != null ? museumJson.photo().getBytes(StandardCharsets.UTF_8): null);

        if (museumJson.geo() != null) {
            museumEntity.setCity(museumJson.geo().city());

            if (museumJson.geo().country() != null) {
                CountryEntity countryEntity = countryRepository.findById(museumJson.geo().country().id())
                        .orElseThrow(() -> new NotFoundException("Country not found with id: " + museumJson.geo().country().id()));
                museumEntity.setCountry(countryEntity);
            }
        }

        return MuseumJson.fromEntity(museumRepository.save(museumEntity));
    }

    public MuseumJson createMuseum(MuseumJson museumJson) {
        MuseumEntity museumEntity = new MuseumEntity();
        museumEntity.setTitle(museumJson.title());
        museumEntity.setDescription(museumJson.description());
        museumEntity.setPhoto(museumJson.photo() != null ? museumJson.photo().getBytes(StandardCharsets.UTF_8): null);

        if (museumJson.geo() != null) {
            museumEntity.setCity(museumJson.geo().city());

            if (museumJson.geo().country() != null) {
                CountryEntity countryEntity = countryRepository.findById(museumJson.geo().country().id())
                        .orElseThrow(() -> new NotFoundException("Country not found with id: " + museumJson.geo().country().id()));
                museumEntity.setCountry(countryEntity);
            } else {
                throw new RequiredParamException("Country is required for a museum");
            }
        } else {
            throw new RequiredParamException("Geo information is required for a museum");
        }

        return MuseumJson.fromEntity(museumRepository.save(museumEntity));
    }
}
