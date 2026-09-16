package de.franconia.tuebingen.adh.corps;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

public interface CorpsMembershipRepository
        extends JpaRepository<CorpsMembership, UUID> {

    List<CorpsMembership> findByUserId(UUID userId);

    Optional<CorpsMembership> findByIdAndUserId(
            UUID id,
            UUID userId
    );

    boolean existsByUserIdAndCorpsId(
            UUID userId,
            UUID corpsId
    );
}