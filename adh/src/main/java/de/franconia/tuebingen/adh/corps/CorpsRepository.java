package de.franconia.tuebingen.adh.corps;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

public interface CorpsRepository extends JpaRepository<Corps, UUID> {
    Optional<Corps> findByName(String name);
}
