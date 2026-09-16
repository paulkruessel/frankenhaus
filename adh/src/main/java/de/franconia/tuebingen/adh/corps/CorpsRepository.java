package de.franconia.tuebingen.adh.corps;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.EntityGraph;

public interface CorpsRepository
        extends JpaRepository<Corps, UUID> {

    Optional<Corps> findByName(String name);

    boolean existsByName(String name);

    @EntityGraph(attributePaths = {
            "corps"
    })
    List<CorpsMembership> findByUserId(
            UUID userId);
}