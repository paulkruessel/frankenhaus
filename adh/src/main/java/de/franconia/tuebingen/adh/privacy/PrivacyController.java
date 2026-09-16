package de.franconia.tuebingen.adh.privacy;

import de.franconia.tuebingen.adh.privacy.dto.PrivacyRuleResponse;
import de.franconia.tuebingen.adh.privacy.dto.UpdatePrivacyRuleRequest;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;

import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/privacy")
public class PrivacyController {

    private final PrivacyService privacyService;

    public PrivacyController(
            PrivacyService privacyService
    ) {
        this.privacyService = privacyService;
    }

    @GetMapping
    public List<PrivacyRuleResponse> getOwnRules(
            @AuthenticationPrincipal
            Jwt jwt
    ) {

        return privacyService.getOwnRules(
                userId(jwt)
        );
    }

    @PutMapping
    public PrivacyRuleResponse updateRule(
            @AuthenticationPrincipal
            Jwt jwt,

            @Valid
            @RequestBody
            UpdatePrivacyRuleRequest request
    ) {

        return privacyService.updateRule(
                userId(jwt),
                request
        );
    }

    @DeleteMapping(
            "/{resourceType}/{resourceId}/{field}"
    )
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteRule(
            @AuthenticationPrincipal
            Jwt jwt,

            @PathVariable
            PrivacyResourceType resourceType,

            @PathVariable
            UUID resourceId,

            @PathVariable
            PrivacyField field
    ) {

        privacyService.deleteRule(
                userId(jwt),
                resourceType,
                resourceId,
                field
        );
    }

    private UUID userId(
            Jwt jwt
    ) {

        return UUID.fromString(
                jwt.getSubject()
        );
    }
}