package de.franconia.tuebingen.adh.corps;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface CorpsMembershipRepository
        extends JpaRepository<CorpsMembership, UUID> {

    @EntityGraph(attributePaths = {
            "corps"
    })
    List<CorpsMembership> findByUserId(
            UUID userId
    );

    Optional<CorpsMembership> findByIdAndUserId(
            UUID id,
            UUID userId
    );

    boolean existsByUserIdAndCorpsId(
            UUID userId,
            UUID corpsId
    );

    @Query("""
            SELECT DISTINCT cm.membershipStatus
            FROM CorpsMembership cm
            WHERE cm.user.id = :userId
              AND cm.membershipStatus IS NOT NULL
            """)
    List<MembershipStatus> findMembershipStatusesByUserId(
            @Param("userId")
            UUID userId
    );

    @Query("""
            SELECT cm
            FROM CorpsMembership cm
            JOIN FETCH cm.corps
            WHERE cm.user.id IN :userIds
            """)
    List<CorpsMembership> findByUserIdsWithCorps(
            @Param("userIds")
            Collection<UUID> userIds
    );
}