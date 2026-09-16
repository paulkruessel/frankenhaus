package de.franconia.tuebingen.adh.corps;

import de.franconia.tuebingen.adh.corps.dto.CreateCorpsMembershipRequest;
import de.franconia.tuebingen.adh.privacy.PrivacyService;
import de.franconia.tuebingen.adh.user.User;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CorpsMembershipServiceTest {

    @Mock
    CorpsMembershipRepository membershipRepository;
    @Mock
    CorpsRepository corpsRepository;
    @Mock
    de.franconia.tuebingen.adh.user.UserRepository userRepository;
    @Mock
    PrivacyService privacyService;

    @Test
    void createRejectsReceptionBeforeAdmission() {
        UUID userId = UUID.randomUUID();
        User user = new User();
        user.setId(userId);
        Corps corps = new Corps();
        corps.setId(UUID.randomUUID());
        corps.setName("Franconia");
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(corpsRepository.findByName("Franconia")).thenReturn(Optional.of(corps));
        when(membershipRepository.existsByUserIdAndCorpsId(userId, corps.getId())).thenReturn(false);

        assertThrows(InvalidMembershipDataException.class, () -> new CorpsMembershipService(
                membershipRepository, corpsRepository, userRepository, privacyService)
                .createMembership(userId, new CreateCorpsMembershipRequest(
                        " Franconia ", null, null, null, null, null,
                        LocalDate.of(2020, 2, 1), LocalDate.of(2020, 1, 1), null, null, null)));
    }

    @Test
    void createRejectsMissingCorps() {
        UUID userId = UUID.randomUUID();
        when(userRepository.findById(userId)).thenReturn(Optional.of(new User()));
        when(corpsRepository.findByName("Unknown")).thenReturn(Optional.empty());

        assertThrows(CorpsNotFoundException.class, () -> new CorpsMembershipService(
                membershipRepository, corpsRepository, userRepository, privacyService)
                .createMembership(userId, new CreateCorpsMembershipRequest(
                        "Unknown", null, null, null, null, null, null, null, null, null, null)));
    }
}