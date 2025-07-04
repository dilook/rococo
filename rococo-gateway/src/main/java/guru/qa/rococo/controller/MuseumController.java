package guru.qa.rococo.controller;

import guru.qa.rococo.model.MuseumJson;
import guru.qa.rococo.service.MuseumService;
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

    private final MuseumService museumService;

    public MuseumController(MuseumService museumService) {
        this.museumService = museumService;
    }

    @GetMapping
    Page<MuseumJson> getAllMuseum(@PageableDefault Pageable pageable,
                                  @RequestParam(required = false) String title) {
        return museumService.getAllMuseum(pageable, title);
    }

    @GetMapping("/{id}")
    MuseumJson getMuseumById(@PathVariable UUID id) {
        return museumService.getMuseumById(id);
    }

    @PatchMapping
    MuseumJson updateMuseum(@RequestBody @Valid MuseumJson museumJson) {
        return museumService.updateMuseum(museumJson);
    }

    @PostMapping
    MuseumJson createMuseum(@RequestBody @Valid MuseumJson museumJson) {
        return museumService.createMuseum(museumJson);
    }
}
