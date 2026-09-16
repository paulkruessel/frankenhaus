package de.franconia.tuebingen.adh.profile;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

public interface ProfileRepository extends JpaRepository<PersonProfile, UUID> {
    Optional<PersonProfile> findByUserId(UUID userId);
}
