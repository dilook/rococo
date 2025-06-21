package guru.qa.rococo.controller;

import guru.qa.rococo.model.PaintingJson;
import guru.qa.rococo.service.PaintingService;
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
@RequestMapping("/api/painting")
public class PaintingController {

    private final PaintingService paintingService;

    public PaintingController(PaintingService paintingService) {
        this.paintingService = paintingService;
    }

    @GetMapping
    Page<PaintingJson> getAllPainting(@PageableDefault Pageable pageable,
                                      @RequestParam(required = false) String title) {
        return paintingService.getAllPainting(pageable, title);
    }

    @GetMapping("/{id}")
    PaintingJson getPaintingById(@PathVariable UUID id) {
        return paintingService.getPaintingById(id);
    }

    @GetMapping("/author/{id}")
    Page<PaintingJson> getPaintingsByArtistId(@PathVariable UUID id, @PageableDefault Pageable pageable) {
        return paintingService.getPaintingsByArtistId(id, pageable);
    }

    @PatchMapping
    PaintingJson updatePainting(@RequestBody @Valid PaintingJson paintingJson) {
        return paintingService.updatePainting(paintingJson);
    }

    @PostMapping
    PaintingJson createPainting(@RequestBody @Valid PaintingJson paintingJson) {
        return paintingService.createPainting(paintingJson);
    }
}
