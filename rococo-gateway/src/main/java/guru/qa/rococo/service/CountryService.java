package guru.qa.rococo.service;

import guru.qa.rococo.data.repository.CountryRepository;
import guru.qa.rococo.model.CountryJson;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CountryService {

    private final CountryRepository countryRepository;

    public CountryService(CountryRepository countryRepository) {
        this.countryRepository = countryRepository;
    }

    public Page<CountryJson> getAllCountry(Pageable pageable) {
        return countryRepository.findAll(pageable).map(CountryJson::fromEntity);
    }

    public List<CountryJson> getAllCountry() {
        return countryRepository.findAll().stream().map(CountryJson::fromEntity).toList();
    }
}