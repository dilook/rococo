package guru.qa.rococo.service;

import guru.qa.rococo.grpc.*;
import io.grpc.Status;
import io.grpc.stub.StreamObserver;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.grpc.server.service.GrpcService;
import guru.qa.rococo.data.CountryEntity;
import guru.qa.rococo.data.repository.CountryRepository;

import java.util.List;

@GrpcService
public class CountryGrpcService extends RococoCountryServiceGrpc.RococoCountryServiceImplBase {

    private final CountryRepository countryRepository;

    public CountryGrpcService(CountryRepository countryRepository) {
        this.countryRepository = countryRepository;
    }

    @Override
    public void getAllCountries(GetAllCountriesRequest request, StreamObserver<GetAllCountriesResponse> responseObserver) {
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
            Page<CountryEntity> countries = countryRepository.findAll(pageable);

            GetAllCountriesResponse.Builder responseBuilder = GetAllCountriesResponse.newBuilder()
                    .setTotalPages(countries.getTotalPages())
                    .setTotalElements(countries.getTotalElements())
                    .setCurrentPage(countries.getNumber())
                    .setPageSize(countries.getSize())
                    .setFirst(countries.isFirst())
                    .setLast(countries.isLast());

            countries.getContent().forEach(country -> 
                responseBuilder.addCountries(convertToGrpcCountry(country))
            );

            responseObserver.onNext(responseBuilder.build());
            responseObserver.onCompleted();
        } catch (Exception e) {
            responseObserver.onError(Status.INTERNAL
                    .withDescription("Error retrieving countries: " + e.getMessage())
                    .asRuntimeException());
        }
    }

    @Override
    public void getAllCountriesList(GetAllCountriesListRequest request, StreamObserver<Country> responseObserver) {
        try {
            List<CountryEntity> countries = countryRepository.findAll();

            countries.forEach(country -> 
                responseObserver.onNext(convertToGrpcCountry(country))
            );

            responseObserver.onCompleted();
        } catch (Exception e) {
            responseObserver.onError(Status.INTERNAL
                    .withDescription("Error retrieving countries list: " + e.getMessage())
                    .asRuntimeException());
        }
    }

    private Country convertToGrpcCountry(CountryEntity entity) {
        return Country.newBuilder()
                .setId(entity.getId().toString())
                .setName(entity.getName())
                .build();
    }
}