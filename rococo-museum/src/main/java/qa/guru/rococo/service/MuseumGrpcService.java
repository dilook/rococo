package qa.guru.rococo.service;

import guru.qa.rococo.grpc.*;
import io.grpc.Status;
import io.grpc.stub.StreamObserver;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.grpc.server.service.GrpcService;
import qa.guru.rococo.data.CountryEntity;
import qa.guru.rococo.data.MuseumEntity;
import qa.guru.rococo.data.repository.CountryRepository;
import qa.guru.rococo.data.repository.MuseumRepository;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.UUID;

@GrpcService
public class MuseumGrpcService extends RococoMuseumServiceGrpc.RococoMuseumServiceImplBase {

    private final MuseumRepository museumRepository;
    private final CountryRepository countryRepository;

    public MuseumGrpcService(MuseumRepository museumRepository, CountryRepository countryRepository) {
        this.museumRepository = museumRepository;
        this.countryRepository = countryRepository;
    }

    @Override
    public void getAllMuseums(GetAllMuseumsRequest request, StreamObserver<GetAllMuseumsResponse> responseObserver) {
        try {
            // Create a sort from request
            Sort sort = Sort.unsorted();
            if (!request.getSortList().isEmpty()) {
                List<Sort.Order> orders = request.getSortList().stream()
                        .map(sortStr -> {
                            if (sortStr.startsWith("-")) {
                                return Sort.Order.desc(sortStr.substring(1));
                            } else {
                                return Sort.Order.asc(sortStr);
                            }
                        })
                        .toList();
                sort = Sort.by(orders);
            }

            Pageable pageable = PageRequest.of(request.getPage(), request.getSize(), sort);
            
            Page<MuseumEntity> museums = request.hasTitle() && !request.getTitle().isEmpty()
                    ? museumRepository.findByTitleContainingIgnoreCase(request.getTitle(), pageable)
                    : museumRepository.findAll(pageable);

            GetAllMuseumsResponse.Builder responseBuilder = GetAllMuseumsResponse.newBuilder()
                    .setTotalPages(museums.getTotalPages())
                    .setTotalElements(museums.getTotalElements())
                    .setCurrentPage(museums.getNumber())
                    .setPageSize(museums.getSize())
                    .setFirst(museums.isFirst())
                    .setLast(museums.isLast());

            museums.getContent().forEach(museum -> 
                responseBuilder.addMuseums(convertToGrpcMuseum(museum))
            );

            responseObserver.onNext(responseBuilder.build());
            responseObserver.onCompleted();
        } catch (Exception e) {
            responseObserver.onError(Status.INTERNAL
                    .withDescription("Error retrieving museums: " + e.getMessage())
                    .asRuntimeException());
        }
    }

    @Override
    public void getMuseumById(GetMuseumByIdRequest request, StreamObserver<Museum> responseObserver) {
        try {
            UUID id = UUID.fromString(request.getId());
            MuseumEntity museum = museumRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Museum not found with id: " + id));

            responseObserver.onNext(convertToGrpcMuseum(museum));
            responseObserver.onCompleted();
        } catch (IllegalArgumentException e) {
            responseObserver.onError(Status.INVALID_ARGUMENT
                    .withDescription("Invalid museum ID format")
                    .asRuntimeException());
        } catch (RuntimeException e) {
            responseObserver.onError(Status.NOT_FOUND
                    .withDescription(e.getMessage())
                    .asRuntimeException());
        } catch (Exception e) {
            responseObserver.onError(Status.INTERNAL
                    .withDescription("Error retrieving museum: " + e.getMessage())
                    .asRuntimeException());
        }
    }

    @Override
    public void createMuseum(CreateMuseumRequest request, StreamObserver<Museum> responseObserver) {
        try {
            if (request.getGeo() == null) {
                responseObserver.onError(Status.INVALID_ARGUMENT
                        .withDescription("Geo information is required for a museum")
                        .asRuntimeException());
                return;
            }

            if (request.getGeo().getCountry() == null) {
                responseObserver.onError(Status.INVALID_ARGUMENT
                        .withDescription("Country is required for a museum")
                        .asRuntimeException());
                return;
            }

            UUID countryId = UUID.fromString(request.getGeo().getCountry().getId());
            CountryEntity countryEntity = countryRepository.findById(countryId)
                    .orElseThrow(() -> new RuntimeException("Country not found with id: " + countryId));

            MuseumEntity museumEntity = new MuseumEntity();
            museumEntity.setTitle(request.getTitle());
            museumEntity.setDescription(request.getDescription());
            museumEntity.setPhoto(request.getPhoto() != null && !request.getPhoto().isEmpty() 
                    ? request.getPhoto().getBytes(StandardCharsets.UTF_8) : null);
            museumEntity.setCity(request.getGeo().getCity());
            museumEntity.setCountry(countryEntity);

            MuseumEntity savedMuseum = museumRepository.save(museumEntity);
            responseObserver.onNext(convertToGrpcMuseum(savedMuseum));
            responseObserver.onCompleted();
        } catch (IllegalArgumentException e) {
            responseObserver.onError(Status.INVALID_ARGUMENT
                    .withDescription("Invalid country ID format")
                    .asRuntimeException());
        } catch (RuntimeException e) {
            responseObserver.onError(Status.NOT_FOUND
                    .withDescription(e.getMessage())
                    .asRuntimeException());
        } catch (Exception e) {
            responseObserver.onError(Status.INTERNAL
                    .withDescription("Error creating museum: " + e.getMessage())
                    .asRuntimeException());
        }
    }

    @Override
    public void updateMuseum(UpdateMuseumRequest request, StreamObserver<Museum> responseObserver) {
        try {
            UUID id = UUID.fromString(request.getId());
            MuseumEntity museumEntity = museumRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Museum not found with id: " + id));

            museumEntity.setTitle(request.getTitle());
            museumEntity.setDescription(request.getDescription());
            museumEntity.setPhoto(request.getPhoto() != null && !request.getPhoto().isEmpty() 
                    ? request.getPhoto().getBytes(StandardCharsets.UTF_8) : null);

            if (request.getGeo() != null) {
                museumEntity.setCity(request.getGeo().getCity());

                if (request.getGeo().getCountry() != null) {
                    UUID countryId = UUID.fromString(request.getGeo().getCountry().getId());
                    CountryEntity countryEntity = countryRepository.findById(countryId)
                            .orElseThrow(() -> new RuntimeException("Country not found with id: " + countryId));
                    museumEntity.setCountry(countryEntity);
                }
            }

            MuseumEntity savedMuseum = museumRepository.save(museumEntity);
            responseObserver.onNext(convertToGrpcMuseum(savedMuseum));
            responseObserver.onCompleted();
        } catch (IllegalArgumentException e) {
            responseObserver.onError(Status.INVALID_ARGUMENT
                    .withDescription("Invalid ID format")
                    .asRuntimeException());
        } catch (RuntimeException e) {
            responseObserver.onError(Status.NOT_FOUND
                    .withDescription(e.getMessage())
                    .asRuntimeException());
        } catch (Exception e) {
            responseObserver.onError(Status.INTERNAL
                    .withDescription("Error updating museum: " + e.getMessage())
                    .asRuntimeException());
        }
    }

    private Museum convertToGrpcMuseum(MuseumEntity entity) {
        Museum.Builder builder = Museum.newBuilder()
                .setId(entity.getId().toString())
                .setTitle(entity.getTitle())
                .setDescription(entity.getDescription() != null ? entity.getDescription() : "");

        if (entity.getPhoto() != null && entity.getPhoto().length > 0) {
            builder.setPhoto(new String(entity.getPhoto(), StandardCharsets.UTF_8));
        }

        if (entity.getCity() != null || entity.getCountry() != null) {
            Geo.Builder geoBuilder = Geo.newBuilder();
            if (entity.getCity() != null) {
                geoBuilder.setCity(entity.getCity());
            }
            if (entity.getCountry() != null) {
                Country country = Country.newBuilder()
                        .setId(entity.getCountry().getId().toString())
                        .setName(entity.getCountry().getName())
                        .build();
                geoBuilder.setCountry(country);
            }
            builder.setGeo(geoBuilder.build());
        }

        return builder.build();
    }
}