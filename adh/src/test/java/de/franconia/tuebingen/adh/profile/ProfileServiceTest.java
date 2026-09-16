package de.franconia.tuebingen.adh.profile;

import de.franconia.tuebingen.adh.auth.EmailAlreadyExistsException;
import de.franconia.tuebingen.adh.profile.dto.UpdateProfileRequest;
import de.franconia.tuebingen.adh.user.User;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProfileServiceTest {

    @Mock
    ProfileRepository profileRepository;
    @Mock
    de.franconia.tuebingen.adh.user.UserRepository userRepository;

    @Test
    void updateNormalizesEmailAndOptionalFields() {
        UUID userId = UUID.randomUUID();
        User user = new User();
        user.setId(userId);
        user.setEmail("old@example.com");
        PersonProfile profile = new PersonProfile();
        profile.setId(UUID.randomUUID());
        profile.setUser(user);
        when(profileRepository.findByUserId(userId)).thenReturn(Optional.of(profile));
        when(userRepository.findByEmail("new@example.com")).thenReturn(Optional.empty());

        var response = new ProfileService(profileRepository, userRepository).updateOwnProfile(
                userId, request(" new@example.com ", " Ada ", " Lovelace "));

        assertEquals("new@example.com", user.getEmail());
        assertEquals("Ada", response.firstName());
        assertEquals("Lovelace", response.lastName());
        org.junit.jupiter.api.Assertions.assertNull(response.title());
    }

    @Test
    void updateRejectsEmailOwnedByAnotherUser() {
        UUID userId = UUID.randomUUID();
        User user = new User();
        user.setId(userId);
        user.setEmail("old@example.com");
        PersonProfile profile = new PersonProfile();
        profile.setUser(user);
        User other = new User();
        other.setId(UUID.randomUUID());
        when(profileRepository.findByUserId(userId)).thenReturn(Optional.of(profile));
        when(userRepository.findByEmail("other@example.com")).thenReturn(Optional.of(other));

        assertThrows(EmailAlreadyExistsException.class, () -> new ProfileService(
                profileRepository, userRepository)
                .updateOwnProfile(userId, request("other@example.com", "Ada", "Lovelace")));
    }

    private UpdateProfileRequest request(String email, String firstName, String lastName) {
        return new UpdateProfileRequest(email, null, firstName, lastName, null, null, null,
                null, null, null, null, null, null, null, null, null, null, null, null,
                null, null);
    }
}