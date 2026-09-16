package de.franconia.tuebingen.adh.member;

import de.franconia.tuebingen.adh.common.dto.PageResponse;
import de.franconia.tuebingen.adh.member.dto.MemberDetailResponse;
import de.franconia.tuebingen.adh.member.dto.MemberSummaryResponse;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;

import org.springframework.validation.annotation.Validated;

import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@Validated
@RestController
@RequestMapping("/api/members")
public class MemberController {

    private final MemberService memberService;

    public MemberController(
            MemberService memberService
    ) {
        this.memberService = memberService;
    }

    @GetMapping
    public PageResponse<MemberSummaryResponse> searchMembers(

            @AuthenticationPrincipal
            Jwt jwt,

            @RequestParam(
                    defaultValue = ""
            )
            @Size(
                    max = 255,
                    message = "Suchbegriff darf maximal 255 Zeichen lang sein"
            )
            String q,

            @RequestParam(
                    defaultValue = "0"
            )
            @Min(
                    value = 0,
                    message = "Seite darf nicht negativ sein"
            )
            int page,

            @RequestParam(
                    defaultValue = "20"
            )
            @Min(
                    value = 1,
                    message = "Seitengröße muss mindestens 1 sein"
            )
            @Max(
                    value = 100,
                    message = "Seitengröße darf maximal 100 sein"
            )
            int size
    ) {

        return memberService.searchMembers(
                userId(jwt),
                q,
                page,
                size
        );
    }

    @GetMapping("/{id}")
    public MemberDetailResponse getMember(

            @AuthenticationPrincipal
            Jwt jwt,

            @PathVariable
            UUID id
    ) {

        return memberService.getMember(
                userId(jwt),
                id
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