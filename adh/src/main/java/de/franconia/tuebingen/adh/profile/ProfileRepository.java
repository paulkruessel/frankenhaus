package de.franconia.tuebingen.adh.profile;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

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

    @Query(
            value = """
                    SELECT p
                    FROM PersonProfile p
                    JOIN FETCH p.user u
                    WHERE u.enabled = true
                      AND (
                          :q = ''
                          OR LOWER(COALESCE(p.firstName, ''))
                                LIKE CONCAT('%', :q, '%')
                          OR LOWER(p.lastName)
                                LIKE CONCAT('%', :q, '%')
                          OR LOWER(u.email)
                                LIKE CONCAT('%', :q, '%')
                          OR LOWER(COALESCE(p.academicDegree, ''))
                                LIKE CONCAT('%', :q, '%')
                          OR LOWER(COALESCE(p.fieldOfStudy, ''))
                                LIKE CONCAT('%', :q, '%')
                          OR LOWER(COALESCE(p.jobTitle, ''))
                                LIKE CONCAT('%', :q, '%')
                          OR LOWER(COALESCE(p.company, ''))
                                LIKE CONCAT('%', :q, '%')
                          OR LOWER(COALESCE(p.position, ''))
                                LIKE CONCAT('%', :q, '%')
                          OR LOWER(COALESCE(p.birthPlace, ''))
                                LIKE CONCAT('%', :q, '%')
                          OR EXISTS (
                              SELECT 1
                              FROM CorpsMembership cm
                              JOIN cm.corps c
                              WHERE cm.user = u
                                AND (
                                    LOWER(c.name)
                                        LIKE CONCAT('%', :q, '%')
                                    OR LOWER(COALESCE(cm.nameInCorps, ''))
                                        LIKE CONCAT('%', :q, '%')
                                    OR LOWER(COALESCE(cm.corpsListNumber, ''))
                                        LIKE CONCAT('%', :q, '%')
                                )
                          )
                      )
                    """,
            countQuery = """
                    SELECT COUNT(p)
                    FROM PersonProfile p
                    JOIN p.user u
                    WHERE u.enabled = true
                      AND (
                          :q = ''
                          OR LOWER(COALESCE(p.firstName, ''))
                                LIKE CONCAT('%', :q, '%')
                          OR LOWER(p.lastName)
                                LIKE CONCAT('%', :q, '%')
                          OR LOWER(u.email)
                                LIKE CONCAT('%', :q, '%')
                          OR LOWER(COALESCE(p.academicDegree, ''))
                                LIKE CONCAT('%', :q, '%')
                          OR LOWER(COALESCE(p.fieldOfStudy, ''))
                                LIKE CONCAT('%', :q, '%')
                          OR LOWER(COALESCE(p.jobTitle, ''))
                                LIKE CONCAT('%', :q, '%')
                          OR LOWER(COALESCE(p.company, ''))
                                LIKE CONCAT('%', :q, '%')
                          OR LOWER(COALESCE(p.position, ''))
                                LIKE CONCAT('%', :q, '%')
                          OR LOWER(COALESCE(p.birthPlace, ''))
                                LIKE CONCAT('%', :q, '%')
                          OR EXISTS (
                              SELECT 1
                              FROM CorpsMembership cm
                              JOIN cm.corps c
                              WHERE cm.user = u
                                AND (
                                    LOWER(c.name)
                                        LIKE CONCAT('%', :q, '%')
                                    OR LOWER(COALESCE(cm.nameInCorps, ''))
                                        LIKE CONCAT('%', :q, '%')
                                    OR LOWER(COALESCE(cm.corpsListNumber, ''))
                                        LIKE CONCAT('%', :q, '%')
                                )
                          )
                      )
                    """
    )
    Page<PersonProfile> searchMembers(
            @Param("q")
            String q,

            Pageable pageable
    );
}