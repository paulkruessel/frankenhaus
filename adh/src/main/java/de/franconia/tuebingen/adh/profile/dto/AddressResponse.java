package de.franconia.tuebingen.adh.profile.dto;

import de.franconia.tuebingen.adh.profile.AddressType;

import java.util.UUID;

public record AddressResponse(
        UUID id,
        AddressType type,
        String street,
        String houseNumber,
        String postalCode,
        String city,
        String country
) {
}