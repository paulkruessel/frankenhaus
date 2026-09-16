package de.franconia.tuebingen.adh.privacy;

import de.franconia.tuebingen.adh.corps.MembershipStatus;
import de.franconia.tuebingen.adh.user.User;

import jakarta.persistence.*;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import org.hibernate.annotations.BatchSize;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.Instant;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(
        name = "privacy_rules",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uq_privacy_rule",
                        columnNames = {
                                "owner_user_id",
                                "resource_type",
                                "resource_id",
                                "field_key"
                        }
                )
        }
)
public class PrivacyRule {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
            name = "owner_user_id",
            nullable = false
    )
    private User owner;

    @Enumerated(EnumType.STRING)
    @Column(
            name = "resource_type",
            nullable = false,
            length = 50
    )
    private PrivacyResourceType resourceType;

    @Column(
            name = "resource_id",
            nullable = false
    )
    private UUID resourceId;

    @Enumerated(EnumType.STRING)
    @Column(
            name = "field_key",
            nullable = false,
            length = 100
    )
    private PrivacyField field;

    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(
            name = "privacy_rule_statuses",
            joinColumns = @JoinColumn(
                    name = "privacy_rule_id",
                    nullable = false
            )
    )
    @Enumerated(EnumType.STRING)
    @Column(
            name = "membership_status",
            nullable = false,
            length = 50
    )
    @BatchSize(size = 50)
    private Set<MembershipStatus> allowedStatuses =
            new HashSet<>();

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "privacy_rule_users",
            joinColumns = @JoinColumn(
                    name = "privacy_rule_id",
                    nullable = false
            ),
            inverseJoinColumns = @JoinColumn(
                    name = "allowed_user_id",
                    nullable = false
            )
    )
    @BatchSize(size = 50)
    private Set<User> allowedUsers =
            new HashSet<>();

    @CreationTimestamp
    @Column(
            name = "created_at",
            nullable = false,
            updatable = false
    )
    private Instant createdAt;

    @UpdateTimestamp
    @Column(
            name = "updated_at",
            nullable = false
    )
    private Instant updatedAt;
}