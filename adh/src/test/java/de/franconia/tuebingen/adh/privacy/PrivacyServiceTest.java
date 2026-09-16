package de.franconia.tuebingen.adh.privacy;

import de.franconia.tuebingen.adh.corps.CorpsMembershipRepository;
import de.franconia.tuebingen.adh.corps.MembershipStatus;
import de.franconia.tuebingen.adh.privacy.dto.UpdatePrivacyRuleRequest;
import de.franconia.tuebingen.adh.profile.AddressRepository;
import de.franconia.tuebingen.adh.profile.ProfileRepository;
import de.franconia.tuebingen.adh.user.User;
import de.franconia.tuebingen.adh.user.UserRepository;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PrivacyServiceTest {

    @Mock
    PrivacyRuleRepository ruleRepository;
    @Mock
    UserRepository userRepository;
    @Mock
    ProfileRepository profileRepository;
    @Mock
    AddressRepository addressRepository;
    @Mock
    CorpsMembershipRepository membershipRepository;

    @Test
    void emptyAllowListsRemoveRuleAndMakeFieldPublic() {
        UUID ownerId = UUID.randomUUID();
        UUID profileId = UUID.randomUUID();
        when(profileRepository.findByUserId(ownerId)).thenReturn(profile(ownerId, profileId));
        PrivacyRule existing = new PrivacyRule();
        when(ruleRepository.findRule(ownerId, PrivacyResourceType.PROFILE, profileId, PrivacyField.EMAIL))
                .thenReturn(Optional.of(existing));

        var response = service().updateRule(ownerId, new UpdatePrivacyRuleRequest(
                PrivacyResourceType.PROFILE, profileId, PrivacyField.EMAIL, Set.of(), Set.of()));

        assertEquals(List.of(), response.allowedStatuses());
        verify(ruleRepository).delete(existing);
    }

    @Test
    void rejectsFieldFromWrongResource() {
        assertThrows(InvalidPrivacyRuleException.class, () -> service().updateRule(
                UUID.randomUUID(), new UpdatePrivacyRuleRequest(
                        PrivacyResourceType.ADDRESS, UUID.randomUUID(), PrivacyField.EMAIL, Set.of(), Set.of())));
    }

    @Test
    void rejectsDisabledAllowedUser() {
        UUID ownerId = UUID.randomUUID();
        UUID profileId = UUID.randomUUID();
        UUID allowedId = UUID.randomUUID();
        when(profileRepository.findByUserId(ownerId)).thenReturn(profile(ownerId, profileId));
        User disabled = new User();
        disabled.setId(allowedId);
        disabled.setEnabled(false);
        when(userRepository.findAllById(Set.of(allowedId))).thenReturn(List.of(disabled));

        assertThrows(InvalidPrivacyRuleException.class, () -> service().updateRule(ownerId,
                new UpdatePrivacyRuleRequest(PrivacyResourceType.PROFILE, profileId,
                        PrivacyField.EMAIL, Set.of(MembershipStatus.AH), Set.of(allowedId))));
    }

    @Test
    void createsAccessContextsWithViewerStatuses() {
        UUID viewerId = UUID.randomUUID();
        UUID ownerId = UUID.randomUUID();
        when(membershipRepository.findMembershipStatusesByUserId(viewerId)).thenReturn(List.of(MembershipStatus.AH));
        when(ruleRepository.findByOwnerIds(Set.of(ownerId))).thenReturn(List.of());

        PrivacyAccessContext context = service().createAccessContext(viewerId, ownerId);
        org.junit.jupiter.api.Assertions.assertTrue(context.canView(
                PrivacyResourceType.PROFILE, UUID.randomUUID(), PrivacyField.EMAIL));

        PrivacyRule rule = new PrivacyRule();
        rule.setOwner(user(ownerId));
        rule.setResourceType(PrivacyResourceType.PROFILE);
        rule.setResourceId(UUID.randomUUID());
        rule.setField(PrivacyField.EMAIL);
        rule.getAllowedStatuses().add(MembershipStatus.AH);
        PrivacyAccessContext granted = new PrivacyAccessContext(
                viewerId, ownerId, Set.of(MembershipStatus.AH), List.of(rule));
        org.junit.jupiter.api.Assertions.assertTrue(granted.canView(
                PrivacyResourceType.PROFILE, rule.getResourceId(), PrivacyField.EMAIL));
    }

    private PrivacyService service() {
        return new PrivacyService(ruleRepository, userRepository, profileRepository,
                addressRepository, membershipRepository);
    }

    private Optional<de.franconia.tuebingen.adh.profile.PersonProfile> profile(UUID userId, UUID profileId) {
        User user = new User();
        user.setId(userId);
        de.franconia.tuebingen.adh.profile.PersonProfile profile = new de.franconia.tuebingen.adh.profile.PersonProfile();
        profile.setId(profileId);
        profile.setUser(user);
        return Optional.of(profile);
    }

    private User user(UUID id) {
        User user = new User();
        user.setId(id);
        return user;
    }
}