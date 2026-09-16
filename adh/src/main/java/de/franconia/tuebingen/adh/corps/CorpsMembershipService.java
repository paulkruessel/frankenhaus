package de.franconia.tuebingen.adh.corps;

import de.franconia.tuebingen.adh.corps.dto.CorpsMembershipResponse;
import de.franconia.tuebingen.adh.corps.dto.CreateCorpsMembershipRequest;
import de.franconia.tuebingen.adh.corps.dto.UpdateCorpsMembershipRequest;

import de.franconia.tuebingen.adh.user.User;
import de.franconia.tuebingen.adh.user.UserRepository;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Service
public class CorpsMembershipService {

    private final CorpsMembershipRepository membershipRepository;

    private final CorpsRepository corpsRepository;

    private final UserRepository userRepository;

    public CorpsMembershipService(
            CorpsMembershipRepository membershipRepository,
            CorpsRepository corpsRepository,
            UserRepository userRepository) {
        this.membershipRepository = membershipRepository;

        this.corpsRepository = corpsRepository;

        this.userRepository = userRepository;
    }

    @Transactional(readOnly = true)
    public List<CorpsMembershipResponse> getOwnMemberships(
            UUID userId) {

        return membershipRepository
                .findByUserId(userId)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional
    public CorpsMembershipResponse createMembership(
            UUID userId,
            CreateCorpsMembershipRequest request) {

        User user = userRepository
                .findById(userId)
                .orElseThrow();

        String corpsName = request.corpsName().trim();

        Corps corps = corpsRepository
                .findByName(corpsName)
                .orElseThrow(
                        CorpsNotFoundException::new);

        if (membershipRepository
                .existsByUserIdAndCorpsId(
                        userId,
                        corps.getId())) {
            throw new CorpsMembershipAlreadyExistsException();
        }

        validateDates(
                request.admissionDate(),
                request.receptionDate(),
                request.philistrationDate());

        CorpsMembership membership = new CorpsMembership();

        membership.setUser(user);
        membership.setCorps(corps);

        apply(
                membership,
                request.nameInCorps(),
                request.corpsListNumber(),
                request.bandNumber(),
                request.brackets(),
                request.membershipStatus(),
                request.admissionDate(),
                request.receptionDate(),
                request.philistrationDate(),
                request.receptionPhoto());

        CorpsMembership saved = membershipRepository.save(
                membership);

        setLeibbursch(
                saved,
                request.leibburschMembershipId());

        return toResponse(saved);
    }

    @Transactional
    public CorpsMembershipResponse updateMembership(
            UUID userId,
            UUID membershipId,
            UpdateCorpsMembershipRequest request) {

        CorpsMembership membership = membershipRepository
                .findByIdAndUserId(
                        membershipId,
                        userId)
                .orElseThrow(
                        CorpsMembershipNotFoundException::new);

        validateDates(
                request.admissionDate(),
                request.receptionDate(),
                request.philistrationDate());

        apply(
                membership,
                request.nameInCorps(),
                request.corpsListNumber(),
                request.bandNumber(),
                request.brackets(),
                request.membershipStatus(),
                request.admissionDate(),
                request.receptionDate(),
                request.philistrationDate(),
                request.receptionPhoto());

        setLeibbursch(
                membership,
                request.leibburschMembershipId());

        return toResponse(membership);
    }

    @Transactional
    public void deleteMembership(
            UUID userId,
            UUID membershipId) {

        CorpsMembership membership = membershipRepository
                .findByIdAndUserId(
                        membershipId,
                        userId)
                .orElseThrow(
                        CorpsMembershipNotFoundException::new);

        membershipRepository.delete(membership);
    }

    private void setLeibbursch(
            CorpsMembership membership,
            UUID leibburschId) {

        if (leibburschId == null) {
            membership.setLeibbursch(null);
            return;
        }

        if (membership.getId()
                .equals(leibburschId)) {
            throw new InvalidLeibburschException(
                    "Eine Mitgliedschaft kann nicht ihr eigener Leibbursch sein");
        }

        CorpsMembership leibbursch = membershipRepository
                .findById(leibburschId)
                .orElseThrow(
                        () -> new InvalidLeibburschException(
                                "Leibbursch-Mitgliedschaft wurde nicht gefunden"));

        if (!leibbursch
                .getCorps()
                .getId()
                .equals(
                        membership
                                .getCorps()
                                .getId())) {
            throw new InvalidLeibburschException(
                    "Leibbursch muss Mitglied desselben Corps sein");
        }

        membership.setLeibbursch(leibbursch);
    }

    private void apply(
            CorpsMembership membership,
            String nameInCorps,
            String corpsListNumber,
            String bandNumber,
            String brackets,
            MembershipStatus membershipStatus,
            LocalDate admissionDate,
            LocalDate receptionDate,
            LocalDate philistrationDate,
            String receptionPhoto) {

        membership.setNameInCorps(
                normalize(nameInCorps));

        membership.setCorpsListNumber(
                normalize(corpsListNumber));

        membership.setBandNumber(
                normalize(bandNumber));

        membership.setBrackets(
                normalize(brackets));

        membership.setMembershipStatus(
                membershipStatus);

        membership.setAdmissionDate(
                admissionDate);

        membership.setReceptionDate(
                receptionDate);

        membership.setPhilistrationDate(
                philistrationDate);

        membership.setReceptionPhoto(
                normalize(receptionPhoto));
    }

    private void validateDates(
            LocalDate admissionDate,
            LocalDate receptionDate,
            LocalDate philistrationDate) {

        if (admissionDate != null
                && receptionDate != null
                && receptionDate.isBefore(admissionDate)) {
            throw new InvalidMembershipDataException(
                    "Reception darf nicht vor der Admission liegen");
        }

        if (receptionDate != null
                && philistrationDate != null
                && philistrationDate.isBefore(receptionDate)) {
            throw new InvalidMembershipDataException(
                    "Philistrierung darf nicht vor der Reception liegen");
        }
    }

    private String normalize(String value) {

        if (value == null) {
            return null;
        }

        String trimmed = value.trim();

        return trimmed.isEmpty()
                ? null
                : trimmed;
    }

    private CorpsMembershipResponse toResponse(
            CorpsMembership membership) {

        UUID leibburschId = membership.getLeibbursch() == null
                ? null
                : membership
                        .getLeibbursch()
                        .getId();

        return new CorpsMembershipResponse(
                membership.getId(),
                membership.getCorps().getId(),
                membership.getCorps().getName(),
                membership.getNameInCorps(),
                membership.getCorpsListNumber(),
                membership.getBandNumber(),
                membership.getBrackets(),
                membership.getMembershipStatus(),
                membership.getAdmissionDate(),
                membership.getReceptionDate(),
                membership.getPhilistrationDate(),
                membership.getReceptionPhoto(),
                leibburschId);
    }
}