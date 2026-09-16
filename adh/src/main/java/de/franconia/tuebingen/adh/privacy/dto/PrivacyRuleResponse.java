package de.franconia.tuebingen.adh.privacy.dto;

import de.franconia.tuebingen.adh.corps.MembershipStatus;
import de.franconia.tuebingen.adh.privacy.PrivacyField;
import de.franconia.tuebingen.adh.privacy.PrivacyResourceType;

import java.util.List;
import java.util.UUID;

public record PrivacyRuleResponse(

        PrivacyResourceType resourceType,

        UUID resourceId,

        PrivacyField field,

        List<MembershipStatus> allowedStatuses,

        List<UUID> allowedUserIds

) {
}