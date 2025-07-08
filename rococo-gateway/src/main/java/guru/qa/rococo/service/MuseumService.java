package guru.qa.rococo.service;

import guru.qa.rococo.model.MuseumJson;
import guru.qa.rococo.service.api.MuseumGrpcClient;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class MuseumService {

    private final MuseumGrpcClient museumGrpcClient;

    public MuseumService(MuseumGrpcClient museumGrpcClient) {
        this.museumGrpcClient = museumGrpcClient;
    }

    public Page<MuseumJson> getAllMuseum(Pageable pageable, String title) {
        return museumGrpcClient.getAllMuseums(pageable, title);
    }

    public MuseumJson getMuseumById(UUID id) {
        return museumGrpcClient.getMuseumById(id);
    }

    public MuseumJson updateMuseum(MuseumJson museumJson) {
        return museumGrpcClient.updateMuseum(museumJson);
    }

    public MuseumJson createMuseum(MuseumJson museumJson) {
        return museumGrpcClient.createMuseum(museumJson);
    }
}
