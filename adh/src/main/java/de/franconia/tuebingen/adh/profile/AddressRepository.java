package de.franconia.tuebingen.adh.profile;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

public interface AddressRepository extends JpaRepository<Address, UUID> {

    List<Address> findByUserId(UUID userId);
    Optional<Address> findByIdAndUserId(
        UUID id,
        UUID userId
    );
}