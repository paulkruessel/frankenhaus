# Kurze Roadmap
### 1. JPA-Modell implementieren
`User`, `PersonProfile`, `Address`, `Corps`, `CorpsMembership` und `MembershipStatus` passend zum bestehenden DB-Schema.
### 2. Repositories erstellen
Vor allem `UserRepository`, `ProfileRepository`, `CorpsRepository` und CorpsMembershipRepository.
### 3. Backend einmal vollständig starten
Mit `ddl-auto=validate` prüfen, ob JPA-Modell und Flyway-Schema wirklich exakt zusammenpassen.
### 4. Authentifizierung implementieren
Registrierung, Passwort-Hashing mit BCrypt, Login und Spring-Security-Konfiguration. Danach festlegen, ob du Cookie/Session oder JWT verwenden willst.

### 5. Eigenes Profil implementieren
Zunächst:

`GET /api/profile/me`\
`PUT /api/profile/me`
### 6. Adressverwaltung ergänzen
Mehrere Adressen pro Nutzer erstellen, bearbeiten und löschen.
### 7. Corps-Mitgliedschaften implementieren
Corps auswählen/anlegen, Mitgliedschaft pflegen, Status, Daten und Leibbursch-Beziehung verwalten.

### 8. Mitgliederverzeichnis bereitstellen
Etwa:

`GET /api/members` \
`GET /api/members/{id}`

inklusive Suche und später Pagination.

### 9. Datenschutz/Sichtbarkeit einbauen
Besonders für Telefonnummer, Geburtsdatum, Adresse und andere persönliche Daten.
### 10. Angular-Frontend aufsetzen
Erst wenn Authentifizierung und Profil-API im Backend funktionieren: Angular + TailwindCSS + Optimus UI anbinden.