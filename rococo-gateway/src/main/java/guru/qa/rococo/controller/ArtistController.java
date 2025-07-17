package guru.qa.rococo.controller;

import guru.qa.rococo.model.ArtistJson;
import guru.qa.rococo.service.api.ArtistGrpcClient;
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
@RequestMapping("/api/artist")
public class ArtistController {

    private final ArtistGrpcClient artistGrpcClient;

    public ArtistController(ArtistGrpcClient artistGrpcClient) {
        this.artistGrpcClient = artistGrpcClient;
    }

    @GetMapping
    Page<ArtistJson> getAllArtist(@PageableDefault Pageable pageable,
                                  @RequestParam(required = false) String name) {
        return artistGrpcClient.getAllArtist(pageable, name);
    }

    @GetMapping("/{id}")
    ArtistJson getArtistById(@PathVariable UUID id) {
        return artistGrpcClient.getArtistById(id);
    }

    @PatchMapping
    ArtistJson updateArtist(@RequestBody @Valid ArtistJson artistJson) {
        return artistGrpcClient.updateArtist(artistJson);
    }

    @PostMapping
    ArtistJson createArtist(@RequestBody @Valid ArtistJson artistJson) {
        return artistGrpcClient.createArtist(artistJson);
    }
}
