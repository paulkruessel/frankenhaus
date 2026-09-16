package de.franconia.tuebingen.adh.corps;

import de.franconia.tuebingen.adh.user.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(
        name = "corps_memberships",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"user_id", "corps_id"})
        }
)
public class CorpsMembership {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "corps_id", nullable = false)
    private Corps corps;

    @Column(name = "corps_name", length = 255)
    private String nameInCorps;

    @Column(name = "corps_list_number", length = 100)
    private String corpsListNumber;

    @Column(name = "band_number", length = 100)
    private String bandNumber;

    @Column(length = 255)
    private String brackets;

    @Enumerated(EnumType.STRING)
    @Column(name = "membership_status", length = 50)
    private MembershipStatus membershipStatus;

    @Column(name = "admission_date")
    private LocalDate admissionDate;

    @Column(name = "reception_date")
    private LocalDate receptionDate;

    @Column(name = "philistration_date")
    private LocalDate philistrationDate;

    @Column(name = "reception_photo", columnDefinition = "TEXT")
    private String receptionPhoto;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "leibbursch_id")
    private CorpsMembership leibbursch;
}