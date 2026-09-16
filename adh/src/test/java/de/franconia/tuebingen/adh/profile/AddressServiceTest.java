package de.franconia.tuebingen.adh.profile;

import de.franconia.tuebingen.adh.privacy.PrivacyService;
import de.franconia.tuebingen.adh.profile.dto.CreateAddressRequest;
import de.franconia.tuebingen.adh.profile.dto.UpdateAddressRequest;
import de.franconia.tuebingen.adh.user.User;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AddressServiceTest {

    @Mock
    AddressRepository addressRepository;
    @Mock
    de.franconia.tuebingen.adh.user.UserRepository userRepository;
    @Mock
    PrivacyService privacyService;

    @Test
    void createTrimsFieldsAndReturnsAddress() {
        UUID userId = UUID.randomUUID();
        User user = new User();
        user.setId(userId);
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(addressRepository.save(any(Address.class))).thenAnswer(invocation -> {
            Address address = invocation.getArgument(0);
            address.setId(UUID.randomUUID());
            return address;
        });

        var response = new AddressService(addressRepository, userRepository, privacyService)
                .createAddress(userId, new CreateAddressRequest(
                        AddressType.PRIVATE, " Main Street ", " 1 ", " 72070 ", " Tuebingen ", " DE "));

        assertEquals("Main Street", response.street());
        assertEquals("1", response.houseNumber());
        assertEquals("DE", response.country());
    }

    @Test
    void updateRejectsForeignAddress() {
        UUID userId = UUID.randomUUID();
        UUID addressId = UUID.randomUUID();
        when(addressRepository.findByIdAndUserId(addressId, userId)).thenReturn(Optional.empty());

        assertThrows(AddressNotFoundException.class, () -> new AddressService(
                addressRepository, userRepository, privacyService)
                .updateAddress(userId, addressId, new UpdateAddressRequest(
                        AddressType.BUSINESS, "Street", "1", "72070", "City", "DE")));
    }

    @Test
    void deleteRemovesPrivacyRulesBeforeAddress() {
        UUID userId = UUID.randomUUID();
        UUID addressId = UUID.randomUUID();
        Address address = new Address();
        when(addressRepository.findByIdAndUserId(addressId, userId)).thenReturn(Optional.of(address));

        new AddressService(addressRepository, userRepository, privacyService).deleteAddress(userId, addressId);

        verify(privacyService).deleteRulesForResource(userId,
                de.franconia.tuebingen.adh.privacy.PrivacyResourceType.ADDRESS, addressId);
        verify(addressRepository).delete(address);
    }
}