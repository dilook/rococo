package guru.qa.rococo.service;

import guru.qa.rococo.data.repository.PaintingRepository;
import guru.qa.rococo.model.PaintingJson;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class PaintingService {

    private final PaintingRepository paintingRepository;

    public PaintingService(PaintingRepository paintingRepository) {
        this.paintingRepository = paintingRepository;
    }

    public Page<PaintingJson> getAllPainting(Pageable pageable) {
        return null;
    }
}
