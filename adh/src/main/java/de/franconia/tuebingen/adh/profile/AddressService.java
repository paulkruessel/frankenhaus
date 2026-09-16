package de.franconia.tuebingen.adh.profile;

import de.franconia.tuebingen.adh.privacy.PrivacyResourceType;
import de.franconia.tuebingen.adh.privacy.PrivacyService;

import de.franconia.tuebingen.adh.profile.dto.AddressResponse;
import de.franconia.tuebingen.adh.profile.dto.CreateAddressRequest;
import de.franconia.tuebingen.adh.profile.dto.UpdateAddressRequest;

import de.franconia.tuebingen.adh.user.User;
import de.franconia.tuebingen.adh.user.UserRepository;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class AddressService {

    private final AddressRepository addressRepository;
    private final UserRepository userRepository;
    private final PrivacyService privacyService;

    public AddressService(
            AddressRepository addressRepository,
            UserRepository userRepository,
            PrivacyService privacyService
    ) {
        this.addressRepository = addressRepository;
        this.userRepository = userRepository;
        this.privacyService = privacyService;
    }

    @Transactional(readOnly = true)
    public List<AddressResponse> getOwnAddresses(
            UUID userId
    ) {

        return addressRepository
                .findByUserId(userId)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional
    public AddressResponse createAddress(
            UUID userId,
            CreateAddressRequest request
    ) {

        User user = userRepository
                .findById(userId)
                .orElseThrow();

        Address address = new Address();

        address.setUser(user);

        apply(
                address,
                request.type(),
                request.street(),
                request.houseNumber(),
                request.postalCode(),
                request.city(),
                request.country()
        );

        Address saved =
                addressRepository.save(address);

        return toResponse(saved);
    }

    @Transactional
    public AddressResponse updateAddress(
            UUID userId,
            UUID addressId,
            UpdateAddressRequest request
    ) {

        Address address = addressRepository
                .findByIdAndUserId(
                        addressId,
                        userId
                )
                .orElseThrow(
                        AddressNotFoundException::new
                );

        apply(
                address,
                request.type(),
                request.street(),
                request.houseNumber(),
                request.postalCode(),
                request.city(),
                request.country()
        );

        return toResponse(address);
    }

    @Transactional
    public void deleteAddress(
            UUID userId,
            UUID addressId
    ) {

        Address address = addressRepository
                .findByIdAndUserId(
                        addressId,
                        userId
                )
                .orElseThrow(
                        AddressNotFoundException::new
                );

        privacyService.deleteRulesForResource(
                userId,
                PrivacyResourceType.ADDRESS,
                addressId
        );

        addressRepository.delete(address);
    }

    private void apply(
            Address address,
            AddressType type,
            String street,
            String houseNumber,
            String postalCode,
            String city,
            String country
    ) {

        address.setType(type);

        address.setStreet(
                street.trim()
        );

        address.setHouseNumber(
                houseNumber.trim()
        );

        address.setPostalCode(
                postalCode.trim()
        );

        address.setCity(
                city.trim()
        );

        address.setCountry(
                country.trim()
        );
    }

    private AddressResponse toResponse(
            Address address
    ) {

        return new AddressResponse(
                address.getId(),
                address.getType(),
                address.getStreet(),
                address.getHouseNumber(),
                address.getPostalCode(),
                address.getCity(),
                address.getCountry()
        );
    }
}