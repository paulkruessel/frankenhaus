package de.franconia.tuebingen.adh.profile;

import de.franconia.tuebingen.adh.user.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "addresses")
public class Address {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(length = 50)
    private String type;

    @Column(length = 255)
    private String street;

    @Column(name = "house_number", length = 50)
    private String houseNumber;

    @Column(name = "postal_code", length = 50)
    private String postalCode;

    @Column(length = 255)
    private String city;

    @Column(length = 255)
    private String country;
}