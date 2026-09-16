package de.franconia.tuebingen.adh.corps.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CreateCorpsRequest(

        @NotBlank(
                message = "Corpsname darf nicht leer sein"
        )
        @Size(
                max = 255,
                message = "Corpsname darf maximal 255 Zeichen lang sein"
        )
        String name

) {
}