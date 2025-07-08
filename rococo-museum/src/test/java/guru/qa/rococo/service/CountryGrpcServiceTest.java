package guru.qa.rococo.service;

import guru.qa.rococo.grpc.GetAllCountriesListRequest;
import guru.qa.rococo.grpc.GetAllCountriesRequest;
import guru.qa.rococo.grpc.GetAllCountriesResponse;
import guru.qa.rococo.grpc.Country;
import io.grpc.Status;
import io.grpc.StatusRuntimeException;
import io.grpc.stub.StreamObserver;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import guru.qa.rococo.data.CountryEntity;
import guru.qa.rococo.data.repository.CountryRepository;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import org.mockito.ArgumentCaptor;

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
        ArgumentCaptor<GetAllCountriesResponse> responseCaptor = ArgumentCaptor.forClass(GetAllCountriesResponse.class);
        verify(responseObserver).onNext(responseCaptor.capture());
        verify(responseObserver).onCompleted();
        verify(responseObserver, never()).onError(any());

        GetAllCountriesResponse response = responseCaptor.getValue();
        assertEquals(0, response.getTotalPages());
        assertEquals(0, response.getTotalElements());
        assertEquals(0, response.getCurrentPage());
        assertEquals(10, response.getPageSize());
        assertTrue(response.getFirst());
        assertTrue(response.getLast());
        assertEquals(0, response.getCountriesCount());
    }

    @Test
    void getAllCountries_ShouldReturnCountriesWithPagination_WhenCountriesExist() {
        // Given
        GetAllCountriesRequest request = GetAllCountriesRequest.newBuilder()
                .setPage(0)
                .setSize(10)
                .build();

        CountryEntity country1 = new CountryEntity();
        country1.setId(UUID.randomUUID());
        country1.setName("Russia");

        CountryEntity country2 = new CountryEntity();
        country2.setId(UUID.randomUUID());
        country2.setName("USA");

        List<CountryEntity> countries = List.of(country1, country2);
        Page<CountryEntity> countriesPage = new PageImpl<>(countries, PageRequest.of(0, 10), 2);
        when(countryRepository.findAll(any(PageRequest.class))).thenReturn(countriesPage);

        // When
        countryGrpcService.getAllCountries(request, responseObserver);

        // Then
        ArgumentCaptor<GetAllCountriesResponse> responseCaptor = ArgumentCaptor.forClass(GetAllCountriesResponse.class);
        verify(responseObserver).onNext(responseCaptor.capture());
        verify(responseObserver).onCompleted();
        verify(responseObserver, never()).onError(any());

        GetAllCountriesResponse response = responseCaptor.getValue();
        assertEquals(1, response.getTotalPages());
        assertEquals(2, response.getTotalElements());
        assertEquals(0, response.getCurrentPage());
        assertEquals(10, response.getPageSize());
        assertTrue(response.getFirst());
        assertTrue(response.getLast());
        assertEquals(2, response.getCountriesCount());

        // Validate countries content
        assertEquals("Russia", response.getCountries(0).getName());
        assertEquals(country1.getId().toString(), response.getCountries(0).getId());
        assertEquals("USA", response.getCountries(1).getName());
        assertEquals(country2.getId().toString(), response.getCountries(1).getId());
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
        ArgumentCaptor<Country> countryCaptor = ArgumentCaptor.forClass(Country.class);
        verify(countryStreamObserver, times(2)).onNext(countryCaptor.capture());
        verify(countryStreamObserver).onCompleted();
        verify(countryStreamObserver, never()).onError(any());

        List<Country> capturedCountries = countryCaptor.getAllValues();
        assertEquals(2, capturedCountries.size());

        // Validate first country
        assertEquals("Russia", capturedCountries.get(0).getName());
        assertEquals(country1.getId().toString(), capturedCountries.get(0).getId());

        // Validate second country
        assertEquals("USA", capturedCountries.get(1).getName());
        assertEquals(country2.getId().toString(), capturedCountries.get(1).getId());
    }

    @Test
    void getAllCountries_ShouldHandleRepositoryException() {
        // Given
        GetAllCountriesRequest request = GetAllCountriesRequest.newBuilder()
                .setPage(0)
                .setSize(10)
                .build();

        when(countryRepository.findAll(any(PageRequest.class)))
                .thenThrow(new RuntimeException("Database connection failed"));

        // When
        countryGrpcService.getAllCountries(request, responseObserver);

        // Then
        ArgumentCaptor<StatusRuntimeException> exceptionCaptor = ArgumentCaptor.forClass(StatusRuntimeException.class);
        verify(responseObserver).onError(exceptionCaptor.capture());
        verify(responseObserver, never()).onNext(any());
        verify(responseObserver, never()).onCompleted();

        StatusRuntimeException exception = exceptionCaptor.getValue();
        assertEquals(Status.INTERNAL.getCode(), exception.getStatus().getCode());
        assertTrue(exception.getMessage().contains("Error retrieving countries: Database connection failed"));
    }

    @Test
    void getAllCountriesList_ShouldHandleRepositoryException() {
        // Given
        GetAllCountriesListRequest request = GetAllCountriesListRequest.newBuilder().build();

        when(countryRepository.findAll()).thenThrow(new RuntimeException("Database connection failed"));

        // When
        countryGrpcService.getAllCountriesList(request, countryStreamObserver);

        // Then
        ArgumentCaptor<StatusRuntimeException> exceptionCaptor = ArgumentCaptor.forClass(StatusRuntimeException.class);
        verify(countryStreamObserver).onError(exceptionCaptor.capture());
        verify(countryStreamObserver, never()).onNext(any());
        verify(countryStreamObserver, never()).onCompleted();

        StatusRuntimeException exception = exceptionCaptor.getValue();
        assertEquals(Status.INTERNAL.getCode(), exception.getStatus().getCode());
        assertTrue(exception.getMessage().contains("Error retrieving countries list: Database connection failed"));
    }

    @Test
    void getAllCountriesList_ShouldReturnEmptyStream_WhenNoCountriesExist() {
        // Given
        GetAllCountriesListRequest request = GetAllCountriesListRequest.newBuilder().build();

        when(countryRepository.findAll()).thenReturn(List.of());

        // When
        countryGrpcService.getAllCountriesList(request, countryStreamObserver);

        // Then
        verify(countryStreamObserver, never()).onNext(any());
        verify(countryStreamObserver).onCompleted();
        verify(countryStreamObserver, never()).onError(any());
    }

    @Test
    void getAllCountries_ShouldApplyAscendingSorting() {
        // Given
        GetAllCountriesRequest request = GetAllCountriesRequest.newBuilder()
                .setPage(0)
                .setSize(10)
                .addSort("name")
                .build();

        CountryEntity country1 = new CountryEntity();
        country1.setId(UUID.randomUUID());
        country1.setName("Australia");

        CountryEntity country2 = new CountryEntity();
        country2.setId(UUID.randomUUID());
        country2.setName("Brazil");

        List<CountryEntity> countries = List.of(country1, country2);
        Page<CountryEntity> countriesPage = new PageImpl<>(countries, PageRequest.of(0, 10), 2);
        when(countryRepository.findAll(any(PageRequest.class))).thenReturn(countriesPage);

        // When
        countryGrpcService.getAllCountries(request, responseObserver);

        // Then
        ArgumentCaptor<Pageable> pageableCaptor = ArgumentCaptor.forClass(Pageable.class);
        verify(countryRepository).findAll(pageableCaptor.capture());

        Pageable capturedPageable = pageableCaptor.getValue();
        Sort sort = capturedPageable.getSort();
        assertFalse(sort.isUnsorted());
        assertEquals(Sort.Direction.ASC, sort.getOrderFor("name").getDirection());

        verify(responseObserver).onNext(any(GetAllCountriesResponse.class));
        verify(responseObserver).onCompleted();
        verify(responseObserver, never()).onError(any());
    }

    @Test
    void getAllCountries_ShouldApplyDescendingSorting() {
        // Given
        GetAllCountriesRequest request = GetAllCountriesRequest.newBuilder()
                .setPage(0)
                .setSize(10)
                .addSort("-name")
                .build();

        CountryEntity country1 = new CountryEntity();
        country1.setId(UUID.randomUUID());
        country1.setName("Brazil");

        CountryEntity country2 = new CountryEntity();
        country2.setId(UUID.randomUUID());
        country2.setName("Australia");

        List<CountryEntity> countries = List.of(country1, country2);
        Page<CountryEntity> countriesPage = new PageImpl<>(countries, PageRequest.of(0, 10), 2);
        when(countryRepository.findAll(any(PageRequest.class))).thenReturn(countriesPage);

        // When
        countryGrpcService.getAllCountries(request, responseObserver);

        // Then
        ArgumentCaptor<Pageable> pageableCaptor = ArgumentCaptor.forClass(Pageable.class);
        verify(countryRepository).findAll(pageableCaptor.capture());

        Pageable capturedPageable = pageableCaptor.getValue();
        Sort sort = capturedPageable.getSort();
        assertFalse(sort.isUnsorted());
        assertEquals(Sort.Direction.DESC, sort.getOrderFor("name").getDirection());

        verify(responseObserver).onNext(any(GetAllCountriesResponse.class));
        verify(responseObserver).onCompleted();
        verify(responseObserver, never()).onError(any());
    }

    @Test
    void getAllCountries_ShouldApplyMultipleSortFields() {
        // Given
        GetAllCountriesRequest request = GetAllCountriesRequest.newBuilder()
                .setPage(0)
                .setSize(10)
                .addSort("name")
                .addSort("-id")
                .build();

        CountryEntity country1 = new CountryEntity();
        country1.setId(UUID.randomUUID());
        country1.setName("Australia");

        List<CountryEntity> countries = List.of(country1);
        Page<CountryEntity> countriesPage = new PageImpl<>(countries, PageRequest.of(0, 10), 1);
        when(countryRepository.findAll(any(PageRequest.class))).thenReturn(countriesPage);

        // When
        countryGrpcService.getAllCountries(request, responseObserver);

        // Then
        ArgumentCaptor<Pageable> pageableCaptor = ArgumentCaptor.forClass(Pageable.class);
        verify(countryRepository).findAll(pageableCaptor.capture());

        Pageable capturedPageable = pageableCaptor.getValue();
        Sort sort = capturedPageable.getSort();
        assertFalse(sort.isUnsorted());
        assertEquals(Sort.Direction.ASC, sort.getOrderFor("name").getDirection());
        assertEquals(Sort.Direction.DESC, sort.getOrderFor("id").getDirection());

        verify(responseObserver).onNext(any(GetAllCountriesResponse.class));
        verify(responseObserver).onCompleted();
        verify(responseObserver, never()).onError(any());
    }

    @Test
    void getAllCountries_ShouldUseUnsortedWhenNoSortProvided() {
        // Given
        GetAllCountriesRequest request = GetAllCountriesRequest.newBuilder()
                .setPage(0)
                .setSize(10)
                .build();

        CountryEntity country1 = new CountryEntity();
        country1.setId(UUID.randomUUID());
        country1.setName("Australia");

        List<CountryEntity> countries = List.of(country1);
        Page<CountryEntity> countriesPage = new PageImpl<>(countries, PageRequest.of(0, 10), 1);
        when(countryRepository.findAll(any(PageRequest.class))).thenReturn(countriesPage);

        // When
        countryGrpcService.getAllCountries(request, responseObserver);

        // Then
        ArgumentCaptor<Pageable> pageableCaptor = ArgumentCaptor.forClass(Pageable.class);
        verify(countryRepository).findAll(pageableCaptor.capture());

        Pageable capturedPageable = pageableCaptor.getValue();
        Sort sort = capturedPageable.getSort();
        assertTrue(sort.isUnsorted());

        verify(responseObserver).onNext(any(GetAllCountriesResponse.class));
        verify(responseObserver).onCompleted();
        verify(responseObserver, never()).onError(any());
    }

    @Test
    void getAllCountries_ShouldHandleDifferentPageSizes() {
        // Given
        GetAllCountriesRequest request = GetAllCountriesRequest.newBuilder()
                .setPage(1)
                .setSize(5)
                .build();

        CountryEntity country1 = new CountryEntity();
        country1.setId(UUID.randomUUID());
        country1.setName("France");

        List<CountryEntity> countries = List.of(country1);
        Page<CountryEntity> countriesPage = new PageImpl<>(countries, PageRequest.of(1, 5), 6);
        when(countryRepository.findAll(any(PageRequest.class))).thenReturn(countriesPage);

        // When
        countryGrpcService.getAllCountries(request, responseObserver);

        // Then
        ArgumentCaptor<GetAllCountriesResponse> responseCaptor = ArgumentCaptor.forClass(GetAllCountriesResponse.class);
        verify(responseObserver).onNext(responseCaptor.capture());
        verify(responseObserver).onCompleted();
        verify(responseObserver, never()).onError(any());

        GetAllCountriesResponse response = responseCaptor.getValue();
        assertEquals(2, response.getTotalPages()); // 6 total elements / 5 page size = 2 pages
        assertEquals(6, response.getTotalElements());
        assertEquals(1, response.getCurrentPage());
        assertEquals(5, response.getPageSize());
        assertFalse(response.getFirst());
        assertTrue(response.getLast());
        assertEquals(1, response.getCountriesCount());
    }
}
