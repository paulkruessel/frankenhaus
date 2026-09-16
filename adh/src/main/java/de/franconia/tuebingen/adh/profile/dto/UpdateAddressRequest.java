package de.franconia.tuebingen.adh.profile.dto;

import de.franconia.tuebingen.adh.profile.AddressType;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record UpdateAddressRequest(

        @NotNull
        AddressType type,

        @NotBlank
        @Size(max = 255)
        String street,

        @NotBlank
        @Size(max = 50)
        String houseNumber,

        @NotBlank
        @Size(max = 50)
        String postalCode,

        @NotBlank
        @Size(max = 255)
        String city,

        @NotBlank
        @Size(max = 255)
        String country
) {
}