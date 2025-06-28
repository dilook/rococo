package qa.guru.rococo.service;

import guru.qa.rococo.grpc.GetAllMuseumsRequest;
import guru.qa.rococo.grpc.GetAllMuseumsResponse;
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
import qa.guru.rococo.data.MuseumEntity;
import qa.guru.rococo.data.repository.CountryRepository;
import qa.guru.rococo.data.repository.MuseumRepository;

import java.util.Collections;

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
        verify(responseObserver, never()).onNext(any());
        verify(responseObserver, never()).onCompleted();
        verify(responseObserver).onError(any());
    }
}
