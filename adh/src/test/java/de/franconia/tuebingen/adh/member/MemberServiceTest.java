package de.franconia.tuebingen.adh.member;

import de.franconia.tuebingen.adh.corps.CorpsMembershipRepository;
import de.franconia.tuebingen.adh.privacy.PrivacyAccessContext;
import de.franconia.tuebingen.adh.privacy.PrivacyService;
import de.franconia.tuebingen.adh.profile.AddressRepository;
import de.franconia.tuebingen.adh.profile.PersonProfile;
import de.franconia.tuebingen.adh.profile.ProfileRepository;
import de.franconia.tuebingen.adh.user.User;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.mock;
import static org.mockito.ArgumentMatchers.any;

@ExtendWith(MockitoExtension.class)
class MemberServiceTest {

    @Mock
    ProfileRepository profileRepository;
    @Mock
    AddressRepository addressRepository;
    @Mock
    CorpsMembershipRepository membershipRepository;
    @Mock
    PrivacyService privacyService;

    @Test
    void searchSortsMembersAndPagesResults() {
        UUID viewer = UUID.randomUUID();
        PersonProfile zed = profile(UUID.randomUUID(), "Zed", "Alpha");
        PersonProfile ada = profile(UUID.randomUUID(), "Ada", "Beta");
        when(profileRepository.findAllEnabledMembers()).thenReturn(List.of(zed, ada));
        Map<UUID, PrivacyAccessContext> contexts = Map.of(
                zed.getUser().getId(), context(viewer, zed.getUser().getId()),
                ada.getUser().getId(), context(viewer, ada.getUser().getId()));
        when(privacyService.createAccessContexts(viewer, Set.of(zed.getUser().getId(), ada.getUser().getId())))
                .thenReturn(contexts);

        var page = new MemberService(profileRepository, addressRepository, membershipRepository, privacyService)
                .searchMembers(viewer, "", 0, 1);

        assertEquals(1, page.content().size());
        assertEquals("Alpha", page.content().getFirst().lastName());
        assertEquals(2, page.totalElements());
        assertEquals(2, page.totalPages());
    }

    @Test
    void getMemberRejectsInactiveOrUnknownUser() {
        UUID target = UUID.randomUUID();
        when(profileRepository.findEnabledMemberByUserId(target)).thenReturn(Optional.empty());

        assertThrows(MemberNotFoundException.class, () -> new MemberService(
                profileRepository, addressRepository, membershipRepository, privacyService)
                .getMember(UUID.randomUUID(), target));
    }

    private PersonProfile profile(UUID id, String firstName, String lastName) {
        User user = new User();
        user.setId(id);
        user.setEmail(firstName.toLowerCase() + "@example.com");
        PersonProfile profile = new PersonProfile();
        profile.setId(UUID.randomUUID());
        profile.setUser(user);
        profile.setFirstName(firstName);
        profile.setLastName(lastName);
        return profile;
    }

    private PrivacyAccessContext context(UUID viewer, UUID owner) {
        PrivacyAccessContext context = mock(PrivacyAccessContext.class);
        when(context.canView(any(), any(), any())).thenReturn(true);
        return context;
    }
}