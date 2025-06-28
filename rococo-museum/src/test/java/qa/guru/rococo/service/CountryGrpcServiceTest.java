package qa.guru.rococo.service;

import guru.qa.rococo.grpc.GetAllCountriesListRequest;
import guru.qa.rococo.grpc.GetAllCountriesRequest;
import guru.qa.rococo.grpc.GetAllCountriesResponse;
import guru.qa.rococo.grpc.Country;
import io.grpc.stub.StreamObserver;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import qa.guru.rococo.data.CountryEntity;
import qa.guru.rococo.data.repository.CountryRepository;

import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CountryGrpcServiceTest {

    @Mock
    private CountryRepository countryRepository;

    @InjectMocks
    private CountryGrpcService countryGrpcService;

    @Mock
    private StreamObserver<GetAllCountriesResponse> responseObserver;

    @Mock
    private StreamObserver<Country> countryStreamObserver;

    @Test
    void getAllCountries_ShouldReturnEmptyResponse_WhenNoCountriesExist() {
        // Given
        GetAllCountriesRequest request = GetAllCountriesRequest.newBuilder()
                .setPage(0)
                .setSize(10)
                .build();

        Page<CountryEntity> emptyPage = new PageImpl<>(List.of(), PageRequest.of(0, 10), 0);
        when(countryRepository.findAll(any(PageRequest.class))).thenReturn(emptyPage);

        // When
        countryGrpcService.getAllCountries(request, responseObserver);

        // Then
        verify(responseObserver).onNext(any(GetAllCountriesResponse.class));
        verify(responseObserver).onCompleted();
        verify(responseObserver, never()).onError(any());
    }

    @Test
    void getAllCountriesList_ShouldReturnAllCountries() {
        // Given
        GetAllCountriesListRequest request = GetAllCountriesListRequest.newBuilder().build();

        CountryEntity country1 = new CountryEntity();
        country1.setId(UUID.randomUUID());
        country1.setName("Russia");

        CountryEntity country2 = new CountryEntity();
        country2.setId(UUID.randomUUID());
        country2.setName("USA");

        List<CountryEntity> countries = List.of(country1, country2);
        when(countryRepository.findAll()).thenReturn(countries);

        // When
        countryGrpcService.getAllCountriesList(request, countryStreamObserver);

        // Then
        verify(countryStreamObserver, times(2)).onNext(any(Country.class));
        verify(countryStreamObserver).onCompleted();
        verify(countryStreamObserver, never()).onError(any());
    }
}
