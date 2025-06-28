package guru.qa.rococo.service;

import guru.qa.rococo.model.CountryJson;
import guru.qa.rococo.service.api.CountryGrpcClient;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CountryService {

    private final CountryGrpcClient countryGrpcClient;

    public CountryService(CountryGrpcClient countryGrpcClient) {
        this.countryGrpcClient = countryGrpcClient;
    }

    public Page<CountryJson> getAllCountry(Pageable pageable) {
        return countryGrpcClient.getAllCountries(pageable);
    }

    public List<CountryJson> getAllCountry() {
        return countryGrpcClient.getAllCountriesList();
    }
}
