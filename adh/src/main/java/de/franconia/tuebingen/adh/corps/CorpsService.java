package de.franconia.tuebingen.adh.corps;

import de.franconia.tuebingen.adh.corps.dto.CorpsResponse;
import de.franconia.tuebingen.adh.corps.dto.CreateCorpsRequest;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class CorpsService {

    private final CorpsRepository corpsRepository;

    public CorpsService(
            CorpsRepository corpsRepository
    ) {
        this.corpsRepository = corpsRepository;
    }

    @Transactional(readOnly = true)
    public List<CorpsResponse> getAllCorps() {

        return corpsRepository
                .findAll()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional
    public CorpsResponse createCorps(
            CreateCorpsRequest request
    ) {

        String name = request.name().trim();

        if (corpsRepository.existsByName(name)) {
            throw new CorpsAlreadyExistsException();
        }

        Corps corps = new Corps();
        corps.setName(name);

        Corps saved =
                corpsRepository.save(corps);

        return toResponse(saved);
    }

    private CorpsResponse toResponse(
            Corps corps
    ) {

        return new CorpsResponse(
                corps.getId(),
                corps.getName()
        );
    }
}