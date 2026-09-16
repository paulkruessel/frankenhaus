package de.franconia.tuebingen.adh.profile;

import de.franconia.tuebingen.adh.profile.dto.AddressResponse;
import de.franconia.tuebingen.adh.profile.dto.CreateAddressRequest;
import de.franconia.tuebingen.adh.profile.dto.UpdateAddressRequest;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;

import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/addresses")
public class AddressController {

    private final AddressService addressService;

    public AddressController(
            AddressService addressService
    ) {
        this.addressService = addressService;
    }

    @GetMapping
    public List<AddressResponse> getOwnAddresses(
            @AuthenticationPrincipal Jwt jwt
    ) {

        return addressService.getOwnAddresses(
                userId(jwt)
        );
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public AddressResponse createAddress(
            @AuthenticationPrincipal Jwt jwt,
            @Valid
            @RequestBody
            CreateAddressRequest request
    ) {

        return addressService.createAddress(
                userId(jwt),
                request
        );
    }

    @PutMapping("/{id}")
    public AddressResponse updateAddress(
            @AuthenticationPrincipal Jwt jwt,

            @PathVariable
            UUID id,

            @Valid
            @RequestBody
            UpdateAddressRequest request
    ) {

        return addressService.updateAddress(
                userId(jwt),
                id,
                request
        );
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteAddress(
            @AuthenticationPrincipal Jwt jwt,
            @PathVariable UUID id
    ) {

        addressService.deleteAddress(
                userId(jwt),
                id
        );
    }

    private UUID userId(Jwt jwt) {
        return UUID.fromString(
                jwt.getSubject()
        );
    }
}