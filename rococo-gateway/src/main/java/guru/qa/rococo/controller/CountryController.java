package guru.qa.rococo.controller;

import guru.qa.rococo.model.CountryJson;
import guru.qa.rococo.service.api.CountryGrpcClient;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/country")
public class CountryController {

    private final CountryGrpcClient countryGrpcClient;

    public CountryController(CountryGrpcClient countryGrpcClient) {
        this.countryGrpcClient = countryGrpcClient;
    }

    @GetMapping
    Page<CountryJson> getAllCountry(@PageableDefault Pageable pageable) {
        return countryGrpcClient.getAllCountries(pageable);
    }

    @GetMapping("/all")
    List<CountryJson> getAllCountry() {
        return countryGrpcClient.getAllCountriesList();
    }
}