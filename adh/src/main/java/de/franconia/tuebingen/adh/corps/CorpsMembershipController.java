package de.franconia.tuebingen.adh.corps;

import de.franconia.tuebingen.adh.corps.dto.CorpsMembershipResponse;
import de.franconia.tuebingen.adh.corps.dto.CreateCorpsMembershipRequest;
import de.franconia.tuebingen.adh.corps.dto.UpdateCorpsMembershipRequest;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;

import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/corps-memberships")
public class CorpsMembershipController {

    private final CorpsMembershipService
            membershipService;

    public CorpsMembershipController(
            CorpsMembershipService membershipService
    ) {
        this.membershipService =
                membershipService;
    }

    @GetMapping
    public List<CorpsMembershipResponse>
    getOwnMemberships(
            @AuthenticationPrincipal
            Jwt jwt
    ) {

        return membershipService
                .getOwnMemberships(
                        userId(jwt)
                );
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CorpsMembershipResponse createMembership(
            @AuthenticationPrincipal
            Jwt jwt,

            @Valid
            @RequestBody
            CreateCorpsMembershipRequest request
    ) {

        return membershipService
                .createMembership(
                        userId(jwt),
                        request
                );
    }

    @PutMapping("/{id}")
    public CorpsMembershipResponse updateMembership(
            @AuthenticationPrincipal
            Jwt jwt,

            @PathVariable
            UUID id,

            @Valid
            @RequestBody
            UpdateCorpsMembershipRequest request
    ) {

        return membershipService
                .updateMembership(
                        userId(jwt),
                        id,
                        request
                );
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteMembership(
            @AuthenticationPrincipal
            Jwt jwt,

            @PathVariable
            UUID id
    ) {

        membershipService
                .deleteMembership(
                        userId(jwt),
                        id
                );
    }

    private UUID userId(Jwt jwt) {

        return UUID.fromString(
                jwt.getSubject()
        );
    }
}