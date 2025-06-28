package guru.qa.rococo.service;

import guru.qa.rococo.grpc.Country;
import guru.qa.rococo.grpc.GetAllCountriesListRequest;
import guru.qa.rococo.grpc.GetAllCountriesRequest;
import guru.qa.rococo.grpc.GetAllCountriesResponse;
import guru.qa.rococo.grpc.RococoCountryServiceGrpc;
import guru.qa.rococo.model.CountryJson;
import io.grpc.StatusRuntimeException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;

@Service
public class CountryGrpcClient {

    private final RococoCountryServiceGrpc.RococoCountryServiceBlockingStub countryServiceStub;

    public CountryGrpcClient(RococoCountryServiceGrpc.RococoCountryServiceBlockingStub countryServiceStub) {
        this.countryServiceStub = countryServiceStub;
    }

    public Page<CountryJson> getAllCountries(Pageable pageable) {
        try {
            GetAllCountriesRequest.Builder requestBuilder = GetAllCountriesRequest.newBuilder()
                    .setPage(pageable.getPageNumber())
                    .setSize(pageable.getPageSize());

            // Add sorting
            if (pageable.getSort().isSorted()) {
                pageable.getSort().forEach(order -> {
                    String sortStr = order.isDescending() ? "-" + order.getProperty() : order.getProperty();
                    requestBuilder.addSort(sortStr);
                });
            }

            GetAllCountriesResponse response = countryServiceStub.getAllCountries(requestBuilder.build());

            List<CountryJson> countries = response.getCountriesList().stream()
                    .map(this::convertFromGrpcCountry)
                    .toList();

            return new PageImpl<>(
                    countries,
                    pageable,
                    response.getTotalElements()
            );
        } catch (StatusRuntimeException e) {
            throw new RuntimeException("Error retrieving countries: " + e.getStatus().getDescription(), e);
        }
    }

    public List<CountryJson> getAllCountriesList() {
        try {
            GetAllCountriesListRequest request = GetAllCountriesListRequest.newBuilder().build();
            Iterator<Country> countryIterator = countryServiceStub.getAllCountriesList(request);

            List<CountryJson> countries = new ArrayList<>();
            countryIterator.forEachRemaining(country -> 
                countries.add(convertFromGrpcCountry(country))
            );

            return countries;
        } catch (StatusRuntimeException e) {
            throw new RuntimeException("Error retrieving countries list: " + e.getStatus().getDescription(), e);
        }
    }

    private CountryJson convertFromGrpcCountry(Country country) {
        return new CountryJson(
                UUID.fromString(country.getId()),
                country.getName()
        );
    }
}
