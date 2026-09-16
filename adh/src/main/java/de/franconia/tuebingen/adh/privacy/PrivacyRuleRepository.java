package de.franconia.tuebingen.adh.privacy;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface PrivacyRuleRepository
        extends JpaRepository<PrivacyRule, UUID> {

    @Query("""
            SELECT r
            FROM PrivacyRule r
            WHERE r.owner.id = :ownerId
            """)
    List<PrivacyRule> findByOwnerId(
            @Param("ownerId")
            UUID ownerId
    );

    @Query("""
            SELECT r
            FROM PrivacyRule r
            WHERE r.owner.id IN :ownerIds
            """)
    List<PrivacyRule> findByOwnerIds(
            @Param("ownerIds")
            Collection<UUID> ownerIds
    );

    @Query("""
            SELECT r
            FROM PrivacyRule r
            WHERE r.owner.id = :ownerId
              AND r.resourceType = :resourceType
              AND r.resourceId = :resourceId
              AND r.field = :field
            """)
    Optional<PrivacyRule> findRule(
            @Param("ownerId")
            UUID ownerId,

            @Param("resourceType")
            PrivacyResourceType resourceType,

            @Param("resourceId")
            UUID resourceId,

            @Param("field")
            PrivacyField field
    );

    @Modifying
    @Query("""
            DELETE FROM PrivacyRule r
            WHERE r.owner.id = :ownerId
              AND r.resourceType = :resourceType
              AND r.resourceId = :resourceId
            """)
    int deleteForResource(
            @Param("ownerId")
            UUID ownerId,

            @Param("resourceType")
            PrivacyResourceType resourceType,

            @Param("resourceId")
            UUID resourceId
    );
}