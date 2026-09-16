package de.franconia.tuebingen.adh.corps.dto;

import java.util.UUID;

public record CorpsResponse(
        UUID id,
        String name
) {
}