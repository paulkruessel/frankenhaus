package de.franconia.tuebingen.adh.profile;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ProfileRepository
        extends JpaRepository<PersonProfile, UUID> {

    Optional<PersonProfile> findByUserId(UUID userId);

    @Query("""
            SELECT p
            FROM PersonProfile p
            JOIN FETCH p.user u
            WHERE u.id = :userId
              AND u.enabled = true
            """)
    Optional<PersonProfile> findEnabledMemberByUserId(
            @Param("userId")
            UUID userId
    );

    @Query("""
            SELECT p
            FROM PersonProfile p
            JOIN FETCH p.user u
            WHERE u.enabled = true
            """)
    List<PersonProfile> findAllEnabledMembers();
}