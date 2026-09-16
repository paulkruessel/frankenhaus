package de.franconia.tuebingen.adh.corps;

import de.franconia.tuebingen.adh.corps.dto.CorpsResponse;
import de.franconia.tuebingen.adh.corps.dto.CreateCorpsRequest;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/corps")
public class CorpsController {

    private final CorpsService corpsService;

    public CorpsController(
            CorpsService corpsService
    ) {
        this.corpsService = corpsService;
    }

    @GetMapping
    public List<CorpsResponse> getAllCorps() {

        return corpsService.getAllCorps();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CorpsResponse createCorps(
            @Valid
            @RequestBody
            CreateCorpsRequest request
    ) {

        return corpsService.createCorps(
                request
        );
    }
}