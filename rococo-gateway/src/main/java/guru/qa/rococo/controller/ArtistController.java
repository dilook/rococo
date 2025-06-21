package guru.qa.rococo.controller;

import guru.qa.rococo.model.ArtistJson;
import guru.qa.rococo.service.ArtistService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/api/artist")
public class ArtistController {

    private final ArtistService artistService;

    public ArtistController(ArtistService artistService) {
        this.artistService = artistService;
    }

    @GetMapping
    Page<ArtistJson> getAllArtist(@PageableDefault Pageable pageable) {
        return artistService.getAllArtist(pageable);
    }

    @GetMapping("/{id}")
    ArtistJson getArtistById(@PathVariable UUID id) {
        return artistService.getArtistById(id);
    }

    @PatchMapping
    ArtistJson updateArtist(@RequestBody ArtistJson artistJson) {
        return artistService.updateArtist(artistJson);
    }

    @PostMapping
    ArtistJson createArtist(@RequestBody ArtistJson artistJson) {
        return artistService.createArtist(artistJson);
    }
}
