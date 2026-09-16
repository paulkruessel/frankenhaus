package de.franconia.tuebingen.adh.privacy;

import de.franconia.tuebingen.adh.corps.MembershipStatus;
import de.franconia.tuebingen.adh.user.User;

import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PrivacyAccessContextTest {

    @Test
    void fieldWithoutRuleIsPublic() {

        UUID viewerId = UUID.randomUUID();
        UUID ownerId = UUID.randomUUID();
        UUID profileId = UUID.randomUUID();

        PrivacyAccessContext context =
                new PrivacyAccessContext(
                        viewerId,
                        ownerId,
                        Set.of(),
                        List.of()
                );

        assertTrue(
                context.canView(
                        PrivacyResourceType.PROFILE,
                        profileId,
                        PrivacyField.MOBILE_PHONE
                )
        );
    }

    @Test
    void explicitUserGrantAllowsOnlySelectedUser() {

        UUID allowedViewerId = UUID.randomUUID();
        UUID otherViewerId = UUID.randomUUID();
        UUID ownerId = UUID.randomUUID();
        UUID profileId = UUID.randomUUID();

        PrivacyRule rule =
                rule(
                        ownerId,
                        profileId,
                        PrivacyField.MOBILE_PHONE
                );

        rule.getAllowedUsers().add(
                user(allowedViewerId)
        );

        PrivacyAccessContext allowedContext =
                new PrivacyAccessContext(
                        allowedViewerId,
                        ownerId,
                        Set.of(),
                        List.of(rule)
                );

        PrivacyAccessContext deniedContext =
                new PrivacyAccessContext(
                        otherViewerId,
                        ownerId,
                        Set.of(),
                        List.of(rule)
                );

        assertTrue(
                allowedContext.canView(
                        PrivacyResourceType.PROFILE,
                        profileId,
                        PrivacyField.MOBILE_PHONE
                )
        );

        assertFalse(
                deniedContext.canView(
                        PrivacyResourceType.PROFILE,
                        profileId,
                        PrivacyField.MOBILE_PHONE
                )
        );
    }

    @Test
    void membershipStatusGrantAllowsMatchingViewer() {

        UUID viewerId = UUID.randomUUID();
        UUID ownerId = UUID.randomUUID();
        UUID profileId = UUID.randomUUID();

        PrivacyRule rule =
                rule(
                        ownerId,
                        profileId,
                        PrivacyField.BIRTH_DATE
                );

        rule.getAllowedStatuses().add(
                MembershipStatus.AH
        );

        PrivacyAccessContext context =
                new PrivacyAccessContext(
                        viewerId,
                        ownerId,
                        Set.of(
                                MembershipStatus.AH
                        ),
                        List.of(rule)
                );

        assertTrue(
                context.canView(
                        PrivacyResourceType.PROFILE,
                        profileId,
                        PrivacyField.BIRTH_DATE
                )
        );
    }

    @Test
    void ownerAlwaysSeesOwnField() {

        UUID ownerId = UUID.randomUUID();
        UUID profileId = UUID.randomUUID();

        PrivacyRule rule =
                rule(
                        ownerId,
                        profileId,
                        PrivacyField.BIRTH_DATE
                );

        PrivacyAccessContext context =
                new PrivacyAccessContext(
                        ownerId,
                        ownerId,
                        Set.of(),
                        List.of(rule)
                );

        assertTrue(
                context.canView(
                        PrivacyResourceType.PROFILE,
                        profileId,
                        PrivacyField.BIRTH_DATE
                )
        );
    }

    private PrivacyRule rule(
            UUID ownerId,
            UUID resourceId,
            PrivacyField field
    ) {

        PrivacyRule rule =
                new PrivacyRule();

        rule.setOwner(
                user(ownerId)
        );

        rule.setResourceType(
                PrivacyResourceType.PROFILE
        );

        rule.setResourceId(
                resourceId
        );

        rule.setField(field);

        return rule;
    }

    private User user(
            UUID id
    ) {

        User user =
                new User();

        user.setId(id);
        user.setEmail(
                id + "@test.example"
        );
        user.setPasswordHash("test");

        return user;
    }
}