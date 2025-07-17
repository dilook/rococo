package guru.qa.rococo.controller;

import guru.qa.rococo.model.MuseumJson;
import guru.qa.rococo.service.api.MuseumGrpcClient;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/api/museum")
public class MuseumController {

    private final MuseumGrpcClient museumGrpcClient;

    public MuseumController(MuseumGrpcClient museumGrpcClient) {
        this.museumGrpcClient = museumGrpcClient;
    }

    @GetMapping
    Page<MuseumJson> getAllMuseum(@PageableDefault Pageable pageable,
                                  @RequestParam(required = false) String title) {
        return museumGrpcClient.getAllMuseums(pageable, title);
    }

    @GetMapping("/{id}")
    MuseumJson getMuseumById(@PathVariable UUID id) {
        return museumGrpcClient.getMuseumById(id);
    }

    @PatchMapping
    MuseumJson updateMuseum(@RequestBody @Valid MuseumJson museumJson) {
        return museumGrpcClient.updateMuseum(museumJson);
    }

    @PostMapping
    MuseumJson createMuseum(@RequestBody @Valid MuseumJson museumJson) {
        return museumGrpcClient.createMuseum(museumJson);
    }
}
