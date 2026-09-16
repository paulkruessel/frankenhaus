package de.franconia.tuebingen.adh.privacy.dto;

import de.franconia.tuebingen.adh.corps.MembershipStatus;
import de.franconia.tuebingen.adh.privacy.PrivacyField;
import de.franconia.tuebingen.adh.privacy.PrivacyResourceType;

import jakarta.validation.constraints.NotNull;

import java.util.Set;
import java.util.UUID;

public record UpdatePrivacyRuleRequest(

        @NotNull
        PrivacyResourceType resourceType,

        @NotNull
        UUID resourceId,

        @NotNull
        PrivacyField field,

        @NotNull
        Set<@NotNull MembershipStatus> allowedStatuses,

        @NotNull
        Set<@NotNull UUID> allowedUserIds

) {
}