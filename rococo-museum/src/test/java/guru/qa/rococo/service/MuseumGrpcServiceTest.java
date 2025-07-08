package guru.qa.rococo.service;

import guru.qa.rococo.data.CountryEntity;
import guru.qa.rococo.grpc.CreateMuseumRequest;
import guru.qa.rococo.grpc.GetAllMuseumsRequest;
import guru.qa.rococo.grpc.GetAllMuseumsResponse;
import guru.qa.rococo.grpc.GetMuseumByIdRequest;
import guru.qa.rococo.grpc.UpdateMuseumRequest;
import guru.qa.rococo.grpc.Museum;
import guru.qa.rococo.grpc.Country;
import guru.qa.rococo.grpc.Geo;
import io.grpc.Status;
import io.grpc.StatusRuntimeException;
import io.grpc.stub.StreamObserver;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import guru.qa.rococo.data.MuseumEntity;
import guru.qa.rococo.data.repository.CountryRepository;
import guru.qa.rococo.data.repository.MuseumRepository;

import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MuseumGrpcServiceTest {

    @Mock
    private MuseumRepository museumRepository;

    @Mock
    private CountryRepository countryRepository;

    @Mock
    private StreamObserver<GetAllMuseumsResponse> responseObserver;

    @Mock
    private StreamObserver<Museum> museumResponseObserver;

    @InjectMocks
    private MuseumGrpcService museumGrpcService;

    @Test
    void getAllMuseums_ShouldReturnEmptyResponse_WhenNoMuseumsExist() {
        // Arrange
        GetAllMuseumsRequest request = GetAllMuseumsRequest.newBuilder()
                .setPage(0)
                .setSize(4)
                .build();

        Page<MuseumEntity> emptyPage = new PageImpl<>(Collections.emptyList(), PageRequest.of(0, 4), 0);
        when(museumRepository.findAll(any(PageRequest.class))).thenReturn(emptyPage);

        // Act
        museumGrpcService.getAllMuseums(request, responseObserver);

        // Assert
        ArgumentCaptor<GetAllMuseumsResponse> responseCaptor = ArgumentCaptor.forClass(GetAllMuseumsResponse.class);
        verify(responseObserver).onNext(responseCaptor.capture());
        verify(responseObserver).onCompleted();
        verify(responseObserver, never()).onError(any());

        GetAllMuseumsResponse response = responseCaptor.getValue();
        assertEquals(0, response.getTotalElements());
        assertEquals(0, response.getMuseumsCount());
        assertTrue(response.getFirst());
        assertTrue(response.getLast());
    }

    @Test
    void getAllMuseums_ShouldHandleException() {
        // Arrange
        GetAllMuseumsRequest request = GetAllMuseumsRequest.newBuilder()
                .setPage(0)
                .setSize(4)
                .build();

        when(museumRepository.findAll(any(PageRequest.class))).thenThrow(new RuntimeException("Database error"));

        // Act
        museumGrpcService.getAllMuseums(request, responseObserver);

        // Assert
        ArgumentCaptor<StatusRuntimeException> errorCaptor = ArgumentCaptor.forClass(StatusRuntimeException.class);
        verify(responseObserver, never()).onNext(any());
        verify(responseObserver, never()).onCompleted();
        verify(responseObserver).onError(errorCaptor.capture());

        StatusRuntimeException exception = errorCaptor.getValue();
        assertEquals(Status.INTERNAL.getCode(), exception.getStatus().getCode());
        assertTrue(exception.getMessage().contains("Database error"));
    }

    @Test
    void getAllMuseums_ShouldReturnMuseumsWithPagination_WhenMuseumsExist() {
        // Given
        GetAllMuseumsRequest request = GetAllMuseumsRequest.newBuilder()
                .setPage(0)
                .setSize(2)
                .build();

        CountryEntity country = createCountryEntity("Russia");
        MuseumEntity museum1 = createMuseumEntity("Hermitage", "Famous museum", "photo1", "St. Petersburg", country);
        MuseumEntity museum2 = createMuseumEntity("Tretyakov Gallery", "Art gallery", "photo2", "Moscow", country);

        List<MuseumEntity> museums = List.of(museum1, museum2);
        Page<MuseumEntity> museumsPage = new PageImpl<>(museums, PageRequest.of(0, 2), 2);
        when(museumRepository.findAll(any(PageRequest.class))).thenReturn(museumsPage);

        // When
        museumGrpcService.getAllMuseums(request, responseObserver);

        // Then
        ArgumentCaptor<GetAllMuseumsResponse> responseCaptor = ArgumentCaptor.forClass(GetAllMuseumsResponse.class);
        verify(responseObserver).onNext(responseCaptor.capture());
        verify(responseObserver).onCompleted();
        verify(responseObserver, never()).onError(any());

        GetAllMuseumsResponse response = responseCaptor.getValue();
        assertEquals(1, response.getTotalPages());
        assertEquals(2, response.getTotalElements());
        assertEquals(0, response.getCurrentPage());
        assertEquals(2, response.getPageSize());
        assertTrue(response.getFirst());
        assertTrue(response.getLast());
        assertEquals(2, response.getMuseumsCount());

        // Validate museums content
        assertEquals("Hermitage", response.getMuseums(0).getTitle());
        assertEquals("Famous museum", response.getMuseums(0).getDescription());
        assertEquals("photo1", response.getMuseums(0).getPhoto());
        assertEquals("St. Petersburg", response.getMuseums(0).getGeo().getCity());
        assertEquals("Russia", response.getMuseums(0).getGeo().getCountry().getName());

        assertEquals("Tretyakov Gallery", response.getMuseums(1).getTitle());
        assertEquals("Art gallery", response.getMuseums(1).getDescription());
        assertEquals("photo2", response.getMuseums(1).getPhoto());
        assertEquals("Moscow", response.getMuseums(1).getGeo().getCity());
        assertEquals("Russia", response.getMuseums(1).getGeo().getCountry().getName());
    }

    @Test
    void getAllMuseums_ShouldFilterByTitle_WhenTitleProvided() {
        // Given
        GetAllMuseumsRequest request = GetAllMuseumsRequest.newBuilder()
                .setPage(0)
                .setSize(4)
                .setTitle("Hermitage")
                .build();

        CountryEntity country = createCountryEntity("Russia");
        MuseumEntity museum = createMuseumEntity("Hermitage", "Famous museum", "photo1", "St. Petersburg", country);

        List<MuseumEntity> museums = List.of(museum);
        Page<MuseumEntity> museumsPage = new PageImpl<>(museums, PageRequest.of(0, 4), 1);
        when(museumRepository.findByTitleContainingIgnoreCase(eq("Hermitage"), any(Pageable.class))).thenReturn(museumsPage);

        // When
        museumGrpcService.getAllMuseums(request, responseObserver);

        // Then
        ArgumentCaptor<GetAllMuseumsResponse> responseCaptor = ArgumentCaptor.forClass(GetAllMuseumsResponse.class);
        verify(responseObserver).onNext(responseCaptor.capture());
        verify(responseObserver).onCompleted();
        verify(responseObserver, never()).onError(any());

        GetAllMuseumsResponse response = responseCaptor.getValue();
        assertEquals(1, response.getMuseumsCount());
        assertEquals("Hermitage", response.getMuseums(0).getTitle());
        verify(museumRepository).findByTitleContainingIgnoreCase("Hermitage", PageRequest.of(0, 4));
        verify(museumRepository, never()).findAll(any(PageRequest.class));
    }

    @Test
    void getMuseumById_ShouldReturnMuseum_WhenMuseumExists() {
        // Given
        UUID museumId = UUID.randomUUID();
        GetMuseumByIdRequest request = GetMuseumByIdRequest.newBuilder()
                .setId(museumId.toString())
                .build();

        CountryEntity country = createCountryEntity("Russia");
        MuseumEntity museum = createMuseumEntity("Hermitage", "Famous museum", "photo1", "St. Petersburg", country);
        museum.setId(museumId);

        when(museumRepository.findById(museumId)).thenReturn(Optional.of(museum));

        // When
        museumGrpcService.getMuseumById(request, museumResponseObserver);

        // Then
        ArgumentCaptor<Museum> responseCaptor = ArgumentCaptor.forClass(Museum.class);
        verify(museumResponseObserver).onNext(responseCaptor.capture());
        verify(museumResponseObserver).onCompleted();
        verify(museumResponseObserver, never()).onError(any());

        Museum response = responseCaptor.getValue();
        assertEquals(museumId.toString(), response.getId());
        assertEquals("Hermitage", response.getTitle());
        assertEquals("Famous museum", response.getDescription());
        assertEquals("photo1", response.getPhoto());
        assertEquals("St. Petersburg", response.getGeo().getCity());
        assertEquals("Russia", response.getGeo().getCountry().getName());
    }

    @Test
    void getMuseumById_ShouldReturnNotFound_WhenMuseumDoesNotExist() {
        // Given
        UUID museumId = UUID.randomUUID();
        GetMuseumByIdRequest request = GetMuseumByIdRequest.newBuilder()
                .setId(museumId.toString())
                .build();

        when(museumRepository.findById(museumId)).thenReturn(Optional.empty());

        // When
        museumGrpcService.getMuseumById(request, museumResponseObserver);

        // Then
        ArgumentCaptor<StatusRuntimeException> errorCaptor = ArgumentCaptor.forClass(StatusRuntimeException.class);
        verify(museumResponseObserver, never()).onNext(any());
        verify(museumResponseObserver, never()).onCompleted();
        verify(museumResponseObserver).onError(errorCaptor.capture());

        StatusRuntimeException exception = errorCaptor.getValue();
        assertEquals(Status.NOT_FOUND.getCode(), exception.getStatus().getCode());
        assertTrue(exception.getMessage().contains("Museum not found with id"));
    }

    @Test
    void getMuseumById_ShouldReturnInvalidArgument_WhenIdFormatIsInvalid() {
        // Given
        GetMuseumByIdRequest request = GetMuseumByIdRequest.newBuilder()
                .setId("invalid-uuid")
                .build();

        // When
        museumGrpcService.getMuseumById(request, museumResponseObserver);

        // Then
        ArgumentCaptor<StatusRuntimeException> errorCaptor = ArgumentCaptor.forClass(StatusRuntimeException.class);
        verify(museumResponseObserver, never()).onNext(any());
        verify(museumResponseObserver, never()).onCompleted();
        verify(museumResponseObserver).onError(errorCaptor.capture());

        StatusRuntimeException exception = errorCaptor.getValue();
        assertEquals(Status.INVALID_ARGUMENT.getCode(), exception.getStatus().getCode());
        assertTrue(exception.getMessage().contains("Invalid museum ID format"));
    }

    @Test
    void createMuseum_ShouldCreateMuseum_WhenValidRequest() {
        // Given
        CountryEntity country = createCountryEntity("Russia");
        UUID countryId = country.getId();

        CreateMuseumRequest request = CreateMuseumRequest.newBuilder()
                .setTitle("New Museum")
                .setDescription("New museum description")
                .setPhoto("photo_data")
                .setGeo(Geo.newBuilder()
                        .setCity("Moscow")
                        .setCountry(Country.newBuilder()
                                .setId(countryId.toString())
                                .setName("Russia")
                                .build())
                        .build())
                .build();

        when(museumRepository.findByTitle("New Museum")).thenReturn(Optional.empty());
        when(countryRepository.findById(countryId)).thenReturn(Optional.of(country));

        MuseumEntity savedMuseum = createMuseumEntity("New Museum", "New museum description", "photo_data", "Moscow", country);
        when(museumRepository.save(any(MuseumEntity.class))).thenReturn(savedMuseum);

        // When
        museumGrpcService.createMuseum(request, museumResponseObserver);

        // Then
        ArgumentCaptor<Museum> responseCaptor = ArgumentCaptor.forClass(Museum.class);
        verify(museumResponseObserver).onNext(responseCaptor.capture());
        verify(museumResponseObserver).onCompleted();
        verify(museumResponseObserver, never()).onError(any());

        Museum response = responseCaptor.getValue();
        assertEquals("New Museum", response.getTitle());
        assertEquals("New museum description", response.getDescription());
        assertEquals("photo_data", response.getPhoto());
        assertEquals("Moscow", response.getGeo().getCity());
        assertEquals("Russia", response.getGeo().getCountry().getName());

        verify(museumRepository).save(any(MuseumEntity.class));
    }

    @Test
    void createMuseum_ShouldReturnAlreadyExists_WhenMuseumWithTitleExists() {
        // Given
        CreateMuseumRequest request = CreateMuseumRequest.newBuilder()
                .setTitle("Existing Museum")
                .setDescription("Description")
                .setPhoto("photo")
                .setGeo(Geo.newBuilder()
                        .setCity("Moscow")
                        .setCountry(Country.newBuilder()
                                .setId(UUID.randomUUID().toString())
                                .setName("Russia")
                                .build())
                        .build())
                .build();

        MuseumEntity existingMuseum = new MuseumEntity();
        existingMuseum.setTitle("Existing Museum");
        when(museumRepository.findByTitle("Existing Museum")).thenReturn(Optional.of(existingMuseum));

        // When
        museumGrpcService.createMuseum(request, museumResponseObserver);

        // Then
        ArgumentCaptor<StatusRuntimeException> errorCaptor = ArgumentCaptor.forClass(StatusRuntimeException.class);
        verify(museumResponseObserver, never()).onNext(any());
        verify(museumResponseObserver, never()).onCompleted();
        verify(museumResponseObserver).onError(errorCaptor.capture());

        StatusRuntimeException exception = errorCaptor.getValue();
        assertEquals(Status.ALREADY_EXISTS.getCode(), exception.getStatus().getCode());
        assertTrue(exception.getMessage().contains("Museum with title 'Existing Museum' already exists"));
    }

    @Test
    void createMuseum_ShouldReturnInvalidArgument_WhenCountryIdFormatIsInvalid() {
        // Given
        CreateMuseumRequest request = CreateMuseumRequest.newBuilder()
                .setTitle("New Museum")
                .setDescription("Description")
                .setPhoto("photo")
                .setGeo(Geo.newBuilder()
                        .setCity("Moscow")
                        .setCountry(Country.newBuilder()
                                .setId("invalid-uuid")
                                .setName("Russia")
                                .build())
                        .build())
                .build();

        when(museumRepository.findByTitle("New Museum")).thenReturn(Optional.empty());

        // When
        museumGrpcService.createMuseum(request, museumResponseObserver);

        // Then
        ArgumentCaptor<StatusRuntimeException> errorCaptor = ArgumentCaptor.forClass(StatusRuntimeException.class);
        verify(museumResponseObserver, never()).onNext(any());
        verify(museumResponseObserver, never()).onCompleted();
        verify(museumResponseObserver).onError(errorCaptor.capture());

        StatusRuntimeException exception = errorCaptor.getValue();
        assertEquals(Status.INVALID_ARGUMENT.getCode(), exception.getStatus().getCode());
        assertTrue(exception.getMessage().contains("Invalid country ID format"));
    }

    @Test
    void updateMuseum_ShouldUpdateMuseum_WhenValidRequest() {
        // Given
        UUID museumId = UUID.randomUUID();
        CountryEntity country = createCountryEntity("Russia");
        UUID countryId = country.getId();

        UpdateMuseumRequest request = UpdateMuseumRequest.newBuilder()
                .setId(museumId.toString())
                .setTitle("Updated Museum")
                .setDescription("Updated description")
                .setPhoto("updated_photo")
                .setGeo(Geo.newBuilder()
                        .setCity("Updated City")
                        .setCountry(Country.newBuilder()
                                .setId(countryId.toString())
                                .setName("Russia")
                                .build())
                        .build())
                .build();

        MuseumEntity existingMuseum = createMuseumEntity("Old Museum", "Old description", "old_photo", "Old City", country);
        existingMuseum.setId(museumId);

        when(museumRepository.findById(museumId)).thenReturn(Optional.of(existingMuseum));
        when(countryRepository.findById(countryId)).thenReturn(Optional.of(country));

        MuseumEntity updatedMuseum = createMuseumEntity("Updated Museum", "Updated description", "updated_photo", "Updated City", country);
        updatedMuseum.setId(museumId);
        when(museumRepository.save(any(MuseumEntity.class))).thenReturn(updatedMuseum);

        // When
        museumGrpcService.updateMuseum(request, museumResponseObserver);

        // Then
        ArgumentCaptor<Museum> responseCaptor = ArgumentCaptor.forClass(Museum.class);
        verify(museumResponseObserver).onNext(responseCaptor.capture());
        verify(museumResponseObserver).onCompleted();
        verify(museumResponseObserver, never()).onError(any());

        Museum response = responseCaptor.getValue();
        assertEquals("Updated Museum", response.getTitle());
        assertEquals("Updated description", response.getDescription());
        assertEquals("updated_photo", response.getPhoto());
        assertEquals("Updated City", response.getGeo().getCity());
        assertEquals("Russia", response.getGeo().getCountry().getName());

        verify(museumRepository).save(any(MuseumEntity.class));
    }

    @Test
    void updateMuseum_ShouldReturnNotFound_WhenMuseumDoesNotExist() {
        // Given
        UUID museumId = UUID.randomUUID();
        UpdateMuseumRequest request = UpdateMuseumRequest.newBuilder()
                .setId(museumId.toString())
                .setTitle("Updated Museum")
                .setDescription("Updated description")
                .setPhoto("updated_photo")
                .setGeo(Geo.newBuilder()
                        .setCity("Updated City")
                        .setCountry(Country.newBuilder()
                                .setId(UUID.randomUUID().toString())
                                .setName("Russia")
                                .build())
                        .build())
                .build();

        when(museumRepository.findById(museumId)).thenReturn(Optional.empty());

        // When
        museumGrpcService.updateMuseum(request, museumResponseObserver);

        // Then
        ArgumentCaptor<StatusRuntimeException> errorCaptor = ArgumentCaptor.forClass(StatusRuntimeException.class);
        verify(museumResponseObserver, never()).onNext(any());
        verify(museumResponseObserver, never()).onCompleted();
        verify(museumResponseObserver).onError(errorCaptor.capture());

        StatusRuntimeException exception = errorCaptor.getValue();
        assertEquals(Status.NOT_FOUND.getCode(), exception.getStatus().getCode());
        assertTrue(exception.getMessage().contains("Museum not found with id"));
    }

    @Test
    void updateMuseum_ShouldReturnInvalidArgument_WhenMuseumIdFormatIsInvalid() {
        // Given
        UpdateMuseumRequest request = UpdateMuseumRequest.newBuilder()
                .setId("invalid-uuid")
                .setTitle("Updated Museum")
                .setDescription("Updated description")
                .setPhoto("updated_photo")
                .setGeo(Geo.newBuilder()
                        .setCity("Updated City")
                        .setCountry(Country.newBuilder()
                                .setId(UUID.randomUUID().toString())
                                .setName("Russia")
                                .build())
                        .build())
                .build();

        // When
        museumGrpcService.updateMuseum(request, museumResponseObserver);

        // Then
        ArgumentCaptor<StatusRuntimeException> errorCaptor = ArgumentCaptor.forClass(StatusRuntimeException.class);
        verify(museumResponseObserver, never()).onNext(any());
        verify(museumResponseObserver, never()).onCompleted();
        verify(museumResponseObserver).onError(errorCaptor.capture());

        StatusRuntimeException exception = errorCaptor.getValue();
        assertEquals(Status.INVALID_ARGUMENT.getCode(), exception.getStatus().getCode());
        assertTrue(exception.getMessage().contains("Invalid ID format"));
    }

    // Helper methods
    private CountryEntity createCountryEntity(String name) {
        CountryEntity country = new CountryEntity();
        country.setId(UUID.randomUUID());
        country.setName(name);
        return country;
    }

    private MuseumEntity createMuseumEntity(String title, String description, String photo, String city, CountryEntity country) {
        MuseumEntity museum = new MuseumEntity();
        museum.setId(UUID.randomUUID());
        museum.setTitle(title);
        museum.setDescription(description);
        museum.setPhoto(photo.getBytes(StandardCharsets.UTF_8));
        museum.setCity(city);
        museum.setCountry(country);
        return museum;
    }
}
