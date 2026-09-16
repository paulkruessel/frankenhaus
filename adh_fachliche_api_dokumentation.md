**Online-Adressverzeichnis**

Fachliche und API-Dokumentation

**Corps Franconia Tübingen**

Zweck, Datenmodell auf fachlicher Ebene, Authentifizierung, Datenschutz
und REST-Endpunkte

Stand: 16. September 2026

# 1. Zweck der Anwendung

Die Anwendung stellt ein geschütztes, webbasiertes Mitglieder- und
Adressverzeichnis für ein Corps bereit. Sie dient dazu, personenbezogene
Daten, Kontaktdaten, berufliche Angaben, Adressen und
Corps-Mitgliedschaften zentral zu pflegen und innerhalb des berechtigten
Nutzerkreises auffindbar zu machen.

Jeder Nutzer besitzt ein eigenes Konto und verwaltet sein persönliches
Profil, seine Adressen und seine Corps-Mitgliedschaften selbst. Andere
authentifizierte Nutzer können das Mitgliederverzeichnis durchsuchen und
Detailinformationen abrufen. Welche Informationen dabei sichtbar sind,
wird feldgenau durch das Datenschutz- und Freigabemodell gesteuert.

- Zentrale Pflege persönlicher und beruflicher Stammdaten.

- Verwaltung mehrerer Adressen pro Nutzer.

- Abbildung einer oder mehrerer Corps-Mitgliedschaften.

- Mitgliederverzeichnis mit Suche und Pagination.

- Feldgenaue Sichtbarkeitsregeln für Profildaten, Adressen und
  Corps-Mitgliedschaften.

- Authentifizierung mit kurzlebigem Access Token und langfristigem
  Refresh Token.

# 2. Fachliches Funktionsmodell

Die Anwendung unterscheidet fachlich zwischen Benutzerkonto,
persönlichem Profil, Adresse, Corps, Corps-Mitgliedschaft und
Datenschutzregel. Diese Bausteine sind miteinander verknüpft, bleiben
aber inhaltlich getrennte Verantwortungsbereiche.

| **Bereich**          | **Fachliche Bedeutung**                                                             |
|----------------------|-------------------------------------------------------------------------------------|
| Benutzerkonto        | Identität für Anmeldung, Berechtigungen und Zuordnung aller nutzerbezogenen Daten.  |
| Profil               | Persönliche, soziale und berufliche Informationen eines Nutzers.                    |
| Adresse              | Eine konkrete Anschrift des Nutzers; mehrere Adressen sind möglich.                 |
| Corps                | Stammdatensatz eines Corps mit eindeutigem Namen und stabiler interner Identität.   |
| Corps-Mitgliedschaft | Verknüpft einen Nutzer mit einem Corps und enthält corpsspezifische Mitgliedsdaten. |
| Datenschutzregel     | Legt fest, wer ein einzelnes Feld einer konkreten Ressource sehen darf.             |

# 3. Authentifizierung und Sitzung

Geschützte Endpunkte werden mit einem Bearer Access Token aufgerufen.
Der Access Token ist 15 Minuten gültig. Für längere Sitzungen wird
zusätzlich ein Refresh Token verwendet, der als geschütztes Cookie
geführt wird und 30 Tage gültig ist. Über den Refresh-Endpunkt wird ein
neuer Access Token ausgestellt, ohne dass E-Mail und Passwort erneut
eingegeben werden müssen.

- Access Token: 15 Minuten gültig; wird als Bearer Token gesendet.

- Refresh Token: 30 Tage gültig; wird als Cookie verwendet.

- Register und Login stellen Access Token und Refresh Cookie bereit.

- Refresh ersetzt den bisherigen Refresh Token und liefert einen neuen
  Access Token.

- Logout widerruft den Refresh Token und entfernt das Cookie.

# 4. Datenschutz- und Sichtbarkeitsmodell

Jede schützbare Information kann individuell freigegeben werden. Eine
Freigabe kann entweder an einen oder mehrere konkrete Nutzer oder an
einen oder mehrere Mitgliedsstatus wie AH, IdC oder Fuchs gebunden
werden.

Existiert für ein Feld keine Datenschutzregel, ist dieses Feld für alle
authentifizierten Nutzer sichtbar. Existiert eine Regel, ist das Feld
sichtbar, wenn der Betrachter entweder explizit freigegeben wurde oder
mindestens einen der freigegebenen Mitgliedsstatus besitzt. Der
Eigentümer der Daten sieht seine eigenen Informationen immer
vollständig.

Ist ein Feld für einen Betrachter nicht freigegeben, bleibt die Struktur
der Antwort erhalten; der Wert des Feldes wird jedoch als leerer Wert
(null) ausgegeben. Dadurch entstehen keine separaten Fehlerantworten nur
aufgrund fehlender Feldsichtbarkeit.

- Keine Regel vorhanden → sichtbar.

- Regel vorhanden + Nutzer explizit erlaubt → sichtbar.

- Regel vorhanden + passender Mitgliedsstatus → sichtbar.

- Eigentümer der Daten → immer sichtbar.

- Keine passende Freigabe → Feldwert wird null.

- Leere Freigabelisten beim Speichern bedeuten wieder allgemeine
  Sichtbarkeit.

# 5. API-Konventionen

Die API arbeitet mit JSON. UUIDs werden als Zeichenketten übertragen.
Datumswerte verwenden das Format YYYY-MM-DD. Geschützte Endpunkte
erwarten einen gültigen Bearer Token. Bei ungültigen Eingaben wird eine
strukturierte Fehlerantwort mit Status, Fehlermeldung und gegebenenfalls
Feldfehlern geliefert.

| **Konvention**        | **Bedeutung**                                              |
|-----------------------|------------------------------------------------------------|
| JSON                  | Request- und Response-Daten werden als JSON übertragen.    |
| UUID                  | Ressourcen werden über UUIDs eindeutig adressiert.         |
| Datum                 | Format YYYY-MM-DD, z. B. 2020-05-17.                       |
| Pagination            | Seitenzählung beginnt bei 0.                               |
| Nicht sichtbares Feld | Wird als null ausgegeben, nicht als 403-Fehler.            |
| Löschen               | Erfolgreiches Löschen liefert in der Regel 204 No Content. |

# 6. Endpunktübersicht

| **Methode** | **Pfad**                                         | **Zweck**                                | **Authentifizierung**   |
|-------------|--------------------------------------------------|------------------------------------------|-------------------------|
| POST        | /api/auth/register                               | Nutzerkonto anlegen                      | Nein                    |
| POST        | /api/auth/login                                  | Anmelden                                 | Nein                    |
| POST        | /api/auth/refresh                                | Access Token erneuern                    | Refresh Cookie          |
| POST        | /api/auth/logout                                 | Sitzung beenden                          | Refresh Cookie optional |
| GET         | /api/profile/me                                  | Eigenes Profil laden                     | Bearer Token            |
| PUT         | /api/profile/me                                  | Eigenes Profil vollständig aktualisieren | Bearer Token            |
| GET         | /api/addresses                                   | Eigene Adressen laden                    | Bearer Token            |
| POST        | /api/addresses                                   | Adresse anlegen                          | Bearer Token            |
| PUT         | /api/addresses/{id}                              | Adresse aktualisieren                    | Bearer Token            |
| DELETE      | /api/addresses/{id}                              | Adresse löschen                          | Bearer Token            |
| GET         | /api/corps                                       | Alle Corps-Stammdaten laden              | Bearer Token            |
| POST        | /api/corps                                       | Corps anlegen                            | Bearer Token            |
| GET         | /api/corps-memberships                           | Eigene Corps-Mitgliedschaften laden      | Bearer Token            |
| POST        | /api/corps-memberships                           | Corps-Mitgliedschaft anlegen             | Bearer Token            |
| PUT         | /api/corps-memberships/{id}                      | Corps-Mitgliedschaft aktualisieren       | Bearer Token            |
| DELETE      | /api/corps-memberships/{id}                      | Corps-Mitgliedschaft löschen             | Bearer Token            |
| GET         | /api/members                                     | Mitglieder suchen und paginieren         | Bearer Token            |
| GET         | /api/members/{id}                                | Mitgliedsdetails abrufen                 | Bearer Token            |
| GET         | /api/privacy                                     | Eigene Datenschutzregeln laden           | Bearer Token            |
| PUT         | /api/privacy                                     | Datenschutzregel setzen/ändern           | Bearer Token            |
| DELETE      | /api/privacy/{resourceType}/{resourceId}/{field} | Datenschutzregel entfernen               | Bearer Token            |

# 7. Authentifizierungs-Endpunkte

### POST /api/auth/register

**Zweck:** Legt ein neues Benutzerkonto und das zugehörige Basisprofil
an.

**Authentifizierung:** Nicht erforderlich.

**Input:** JSON mit email, password, firstName und lastName. E-Mail muss
gültig sein. Passwort muss 12–128 Zeichen lang sein. Vor- und Nachname
dürfen jeweils maximal 255 Zeichen lang sein.

**Output:** JSON mit accessToken, tokenType und expiresIn. Zusätzlich
wird ein Refresh Cookie gesetzt.

**Typische Statuscodes:** 201 Created; 400 bei Validierungsfehlern; 409
wenn die E-Mail bereits vergeben ist.

### POST /api/auth/login

**Zweck:** Authentifiziert einen vorhandenen Nutzer.

**Authentifizierung:** Nicht erforderlich.

**Input:** JSON mit email und password.

**Output:** JSON mit accessToken, tokenType und expiresIn. Zusätzlich
wird ein neuer Refresh Cookie gesetzt.

**Typische Statuscodes:** 200 OK; 401 bei ungültigen Zugangsdaten; 403
bei deaktiviertem Konto.

### POST /api/auth/refresh

**Zweck:** Stellt ohne erneute Passwortanmeldung einen neuen Access
Token aus.

**Authentifizierung:** Refresh Cookie erforderlich.

**Input:** Kein JSON-Body. Das Cookie refresh_token muss mitgesendet
werden.

**Output:** JSON mit neuem accessToken, tokenType und expiresIn. Der
Refresh Token wird rotiert und als neues Cookie gesetzt.

**Typische Statuscodes:** 200 OK; 401 bei ungültigem oder abgelaufenem
Refresh Token.

### POST /api/auth/logout

**Zweck:** Beendet die aktuelle Refresh-Sitzung.

**Authentifizierung:** Refresh Cookie optional.

**Input:** Kein JSON-Body.

**Output:** Kein Response-Body; das Refresh Cookie wird entfernt.

**Typische Statuscodes:** 204 No Content.

# 8. Profil-Endpunkte

### GET /api/profile/me

**Zweck:** Lädt das vollständige eigene Profil.

**Authentifizierung:** Bearer Token erforderlich.

**Input:** Kein Request-Body.

**Output:** Profilobjekt mit Profil-ID, User-ID, E-Mail sowie allen
persönlichen, sozialen und beruflichen Feldern.

**Typische Statuscodes:** 200 OK; 404 falls kein Profil vorhanden ist.

### PUT /api/profile/me

**Zweck:** Aktualisiert das eigene Profil.

**Authentifizierung:** Bearer Token erforderlich.

**Input:** JSON mit den unten beschriebenen Profilfeldern. E-Mail und
Nachname sind Pflichtfelder; weitere Felder sind optional.

**Output:** Das vollständig aktualisierte Profilobjekt.

**Typische Statuscodes:** 200 OK; 400 bei Validierungsfehlern.

| **Feld**              | **Pflicht** | **Erwartung**                       |
|-----------------------|-------------|-------------------------------------|
| email                 | Ja          | Gültige E-Mail, max. 320 Zeichen    |
| title                 | Nein        | Titel, max. 100 Zeichen             |
| firstName             | Nein        | Vorname, max. 255 Zeichen           |
| lastName              | Ja          | Nachname, max. 255 Zeichen          |
| mobilePhone           | Nein        | Mobiltelefon, max. 100 Zeichen      |
| birthDate             | Nein        | Geburtsdatum, nicht in der Zukunft  |
| birthPlace            | Nein        | Geburtsort, max. 255 Zeichen        |
| wikipediaUrl          | Nein        | HTTP(S)-URL, max. 2048 Zeichen      |
| linkedinUrl           | Nein        | HTTP(S)-URL, max. 2048 Zeichen      |
| xingUrl               | Nein        | HTTP(S)-URL, max. 2048 Zeichen      |
| facebookUrl           | Nein        | HTTP(S)-URL, max. 2048 Zeichen      |
| twitterUrl            | Nein        | HTTP(S)-URL, max. 2048 Zeichen      |
| instagramUrl          | Nein        | HTTP(S)-URL, max. 2048 Zeichen      |
| additionalInformation | Nein        | Freitext, max. 10000 Zeichen        |
| academicDegree        | Nein        | Akademischer Grad, max. 255 Zeichen |
| fieldOfStudy          | Nein        | Studienfach, max. 255 Zeichen       |
| jobTitle              | Nein        | Berufsbezeichnung, max. 255 Zeichen |
| company               | Nein        | Unternehmen, max. 255 Zeichen       |
| position              | Nein        | Position, max. 255 Zeichen          |
| website               | Nein        | HTTP(S)-URL, max. 2048 Zeichen      |
| employmentStatus      | Nein        | Berufsstatus, max. 100 Zeichen      |

# 9. Adress-Endpunkte

Ein Nutzer kann mehrere Adressen besitzen. Jede Adresse wird über eine
eigene UUID identifiziert. Bei Änderungen oder Löschungen kann nur auf
eigene Adressen zugegriffen werden.

### GET /api/addresses

**Zweck:** Lädt alle eigenen Adressen.

**Authentifizierung:** Bearer Token erforderlich.

**Input:** Kein Request-Body.

**Output:** Liste von Adressobjekten mit id, type, street, houseNumber,
postalCode, city und country.

**Typische Statuscodes:** 200 OK.

### POST /api/addresses

**Zweck:** Legt eine neue eigene Adresse an.

**Authentifizierung:** Bearer Token erforderlich.

**Input:** JSON mit allen Adressfeldern.

**Output:** Neu angelegtes Adressobjekt inklusive ID.

**Typische Statuscodes:** 201 Created; 400 bei Validierungsfehlern.

### PUT /api/addresses/{id}

**Zweck:** Ersetzt die Daten einer eigenen Adresse.

**Authentifizierung:** Bearer Token erforderlich.

**Input:** Pfadparameter id = UUID der Adresse; JSON mit allen
Adressfeldern.

**Output:** Aktualisiertes Adressobjekt.

**Typische Statuscodes:** 200 OK; 404 bei unbekannter oder fremder
Adresse.

### DELETE /api/addresses/{id}

**Zweck:** Löscht eine eigene Adresse.

**Authentifizierung:** Bearer Token erforderlich.

**Input:** Pfadparameter id = UUID der Adresse.

**Output:** Kein Response-Body.

**Typische Statuscodes:** 204 No Content; 404 bei unbekannter oder
fremder Adresse.

| **Feld**    | **Pflicht** | **Erwartung**                           |
|-------------|-------------|-----------------------------------------|
| type        | Ja          | PRIVATE, SECONDARY, BUSINESS oder OTHER |
| street      | Ja          | Straße, max. 255 Zeichen                |
| houseNumber | Ja          | Hausnummer, max. 50 Zeichen             |
| postalCode  | Ja          | Postleitzahl, max. 50 Zeichen           |
| city        | Ja          | Ort, max. 255 Zeichen                   |
| country     | Ja          | Land, max. 255 Zeichen                  |

# 10. Corps-Stammdaten

Corps werden als eigenständige Stammdaten geführt. Der Name eines Corps
ist eindeutig, dient aber nicht als dauerhafte Identität. Dadurch kann
ein Corps umbenannt werden, ohne dass bestehende Mitgliedschaften ihre
Zuordnung verlieren.

### GET /api/corps

**Zweck:** Lädt alle bekannten Corps.

**Authentifizierung:** Bearer Token erforderlich.

**Input:** Kein Request-Body.

**Output:** Liste mit id und name je Corps.

**Typische Statuscodes:** 200 OK.

### POST /api/corps

**Zweck:** Legt einen neuen Corps-Stammdatensatz an.

**Authentifizierung:** Bearer Token erforderlich.

**Input:** JSON mit name. Der Name ist Pflicht und auf 255 Zeichen
begrenzt.

**Output:** Corpsobjekt mit id und name.

**Typische Statuscodes:** 201 Created; 409 wenn der Name bereits
existiert.

# 11. Corps-Mitgliedschaften

Eine Corps-Mitgliedschaft verbindet einen Nutzer mit einem Corps und
enthält corpsspezifische Daten. Beim Anlegen wird das Corps über seinen
Namen ausgewählt; intern bleibt die Verknüpfung stabil, auch wenn der
Corpsname später geändert wird.

### GET /api/corps-memberships

**Zweck:** Lädt alle eigenen Corps-Mitgliedschaften.

**Authentifizierung:** Bearer Token erforderlich.

**Input:** Kein Request-Body.

**Output:** Liste der Mitgliedschaften inklusive Corps-ID, Corpsname,
Status, Daten und optionaler Leibbursch-Zuordnung.

**Typische Statuscodes:** 200 OK.

### POST /api/corps-memberships

**Zweck:** Legt eine neue Corps-Mitgliedschaft für den angemeldeten
Nutzer an.

**Authentifizierung:** Bearer Token erforderlich.

**Input:** JSON mit corpsName und optionalen Mitgliedschaftsdaten. Pro
Nutzer und Corps kann nur eine Mitgliedschaft bestehen.

**Output:** Neu angelegte Corps-Mitgliedschaft.

**Typische Statuscodes:** 201 Created; 404 wenn das Corps nicht
existiert; 409 bei bereits vorhandener Mitgliedschaft; 400 bei
ungültigen Daten.

### PUT /api/corps-memberships/{id}

**Zweck:** Aktualisiert die Daten einer eigenen Corps-Mitgliedschaft.
Das Corps selbst wird dabei nicht gewechselt.

**Authentifizierung:** Bearer Token erforderlich.

**Input:** Pfadparameter id = Mitgliedschafts-UUID; JSON mit den
veränderbaren Mitgliedschaftsdaten ohne corpsName.

**Output:** Aktualisierte Corps-Mitgliedschaft.

**Typische Statuscodes:** 200 OK; 404 bei unbekannter oder fremder
Mitgliedschaft; 400 bei ungültigen Daten.

### DELETE /api/corps-memberships/{id}

**Zweck:** Löscht eine eigene Corps-Mitgliedschaft.

**Authentifizierung:** Bearer Token erforderlich.

**Input:** Pfadparameter id = Mitgliedschafts-UUID.

**Output:** Kein Response-Body.

**Typische Statuscodes:** 204 No Content; 404 bei unbekannter oder
fremder Mitgliedschaft.

| **Feld**               | **Pflicht**  | **Erwartung**                                             |
|------------------------|--------------|-----------------------------------------------------------|
| corpsName              | Nur POST: Ja | Name des bereits vorhandenen Corps                        |
| nameInCorps            | Nein         | Name im Corps, max. 255 Zeichen                           |
| corpsListNumber        | Nein         | Nummer in der Corpsliste, max. 100 Zeichen                |
| bandNumber             | Nein         | Bandnummer, max. 100 Zeichen                              |
| brackets               | Nein         | Klammerungen, max. 255 Zeichen                            |
| membershipStatus       | Nein         | CK, IdC, iaIdC, AHIdc, CB, iaCB, AH, Fuchs, AHidC oder EM |
| admissionDate          | Nein         | Admission als YYYY-MM-DD                                  |
| receptionDate          | Nein         | Reception als YYYY-MM-DD; nicht vor Admission             |
| philistrationDate      | Nein         | Philistrierung als YYYY-MM-DD; nicht vor Reception        |
| receptionPhoto         | Nein         | Verweis auf Receptionsfoto, max. 2048 Zeichen             |
| leibburschMembershipId | Nein         | UUID einer Mitgliedschaft im selben Corps                 |

# 12. Mitgliederverzeichnis, Suche und Pagination

Das Mitgliederverzeichnis erlaubt es authentifizierten Nutzern, andere
aktive Mitglieder zu finden und deren freigegebene Informationen
abzurufen. Datenschutzregeln werden sowohl in der Listenansicht als auch
in der Detailansicht berücksichtigt.

### GET /api/members

**Zweck:** Durchsucht das Mitgliederverzeichnis und liefert ein
paginiertes Ergebnis.

**Authentifizierung:** Bearer Token erforderlich.

**Input:** Query-Parameter: q = optionaler Suchbegriff (max. 255
Zeichen), page = Seitennummer ab 0, size = Anzahl Ergebnisse pro Seite
von 1 bis 100. Standard: page=0, size=20.

**Output:** Paginiertes Objekt mit content, page, size,
numberOfElements, totalElements, totalPages, first und last. Jeder
Listeneintrag enthält id, title, firstName, lastName, email,
academicDegree, jobTitle, company und position; nicht freigegebene
Felder erscheinen als null.

**Typische Statuscodes:** 200 OK; 400 bei ungültigen Pagination- oder
Suchparametern.

### GET /api/members/{id}

**Zweck:** Lädt die Detailansicht eines aktiven Mitglieds.

**Authentifizierung:** Bearer Token erforderlich.

**Input:** Pfadparameter id = User-UUID des Zielnutzers.

**Output:** Objekt mit profile, addresses und corpsMemberships.
Datenschutzregeln werden für jedes einzelne Feld angewendet; nicht
sichtbare Werte werden als null zurückgegeben.

**Typische Statuscodes:** 200 OK; 404 wenn das Mitglied nicht existiert
oder nicht aktiv ist.

# 13. Datenschutz-Endpunkte

Datenschutzregeln gelten immer für ein einzelnes Feld einer konkreten
Ressource. Unterstützte Ressourcentypen sind PROFILE, ADDRESS und
CORPS_MEMBERSHIP.

### GET /api/privacy

**Zweck:** Lädt alle vom angemeldeten Nutzer gesetzten
Datenschutzregeln.

**Authentifizierung:** Bearer Token erforderlich.

**Input:** Kein Request-Body.

**Output:** Liste von Regeln mit resourceType, resourceId, field,
allowedStatuses und allowedUserIds.

**Typische Statuscodes:** 200 OK.

### PUT /api/privacy

**Zweck:** Legt eine Datenschutzregel an oder ersetzt eine bestehende
Regel für dasselbe Feld.

**Authentifizierung:** Bearer Token erforderlich.

**Input:** JSON mit resourceType, resourceId, field, allowedStatuses und
allowedUserIds. Die Ressource muss dem angemeldeten Nutzer gehören.
Status- und Nutzerlisten müssen vorhanden sein; leere Listen bedeuten
allgemeine Sichtbarkeit und entfernen eine vorhandene Einschränkung.

**Output:** Die wirksame Regel. Bei leeren Freigabelisten werden leere
Listen zurückgegeben.

**Typische Statuscodes:** 200 OK; 400 bei inkonsistenter Regel; 404 wenn
die Ressource nicht dem Nutzer gehört oder nicht existiert.

### DELETE /api/privacy/{resourceType}/{resourceId}/{field}

**Zweck:** Entfernt die Einschränkung für ein bestimmtes Feld und macht
dieses Feld damit wieder allgemein sichtbar.

**Authentifizierung:** Bearer Token erforderlich.

**Input:** Pfadparameter: resourceType = PROFILE, ADDRESS oder
CORPS_MEMBERSHIP; resourceId = UUID der Ressource; field = zulässiger
Feldschlüssel.

**Output:** Kein Response-Body.

**Typische Statuscodes:** 204 No Content; 400 bei nicht passender
Feld/Ressourcen-Kombination; 404 bei unbekannter oder fremder Ressource.

# 14. Schützbare Felder

| **Ressource**    | **Schützbare Felder**                                                                                                                                                                                                                                                          |
|------------------|--------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| PROFILE          | EMAIL, TITLE, FIRST_NAME, LAST_NAME, MOBILE_PHONE, BIRTH_DATE, BIRTH_PLACE, WIKIPEDIA_URL, LINKEDIN_URL, XING_URL, FACEBOOK_URL, TWITTER_URL, INSTAGRAM_URL, ADDITIONAL_INFORMATION, ACADEMIC_DEGREE, FIELD_OF_STUDY, JOB_TITLE, COMPANY, POSITION, WEBSITE, EMPLOYMENT_STATUS |
| ADDRESS          | ADDRESS_TYPE, ADDRESS_STREET, ADDRESS_HOUSE_NUMBER, ADDRESS_POSTAL_CODE, ADDRESS_CITY, ADDRESS_COUNTRY                                                                                                                                                                         |
| CORPS_MEMBERSHIP | CORPS_NAME, NAME_IN_CORPS, CORPS_LIST_NUMBER, BAND_NUMBER, BRACKETS, MEMBERSHIP_STATUS, ADMISSION_DATE, RECEPTION_DATE, PHILISTRATION_DATE, RECEPTION_PHOTO, LEIBBURSCH                                                                                                        |

# 15. Fehlerverhalten

Die API verwendet HTTP-Statuscodes, um fachliche und technische
Fehlerfälle unterscheidbar zu machen. Nicht vorhandene oder dem Nutzer
nicht gehörende Ressourcen werden bei nutzerbezogenen Änderungen bewusst
gleich behandelt, damit keine fremden Ressourcen über ihre Existenz
erkannt werden können.

| **Status**       | **Bedeutung**                                                                              |
|------------------|--------------------------------------------------------------------------------------------|
| 200 OK           | Anfrage erfolgreich verarbeitet.                                                           |
| 201 Created      | Neue Ressource erfolgreich angelegt.                                                       |
| 204 No Content   | Lösch- oder Logout-Operation erfolgreich ohne Response-Body.                               |
| 400 Bad Request  | Ungültiger Input oder fachlich inkonsistente Daten.                                        |
| 401 Unauthorized | Authentifizierung fehlt oder ist ungültig; bei Login auch falsche Zugangsdaten.            |
| 403 Forbidden    | Konto ist deaktiviert.                                                                     |
| 404 Not Found    | Ressource existiert nicht oder ist für die angeforderte eigene Operation nicht zuordenbar. |
| 409 Conflict     | Eindeutigkeitskonflikt, z. B. E-Mail, Corpsname oder doppelte Mitgliedschaft.              |

# 16. Typische Nutzung

Ein typischer Lebenszyklus eines Nutzers sieht wie folgt aus:

- Registrierung mit E-Mail, Passwort, Vorname und Nachname.

- Pflege des vollständigen Profils.

- Anlage einer oder mehrerer Adressen.

- Auswahl oder Anlage eines Corps und Erfassung der eigenen
  Mitgliedschaft.

- Optional: Einschränkung einzelner Felder auf bestimmte Mitgliedsstatus
  oder konkrete Nutzer.

- Suche nach anderen Mitgliedern im Verzeichnis.

- Abruf fremder Detailprofile; nur freigegebene Felder werden mit Inhalt
  ausgeliefert.

- Erneuerung des Access Tokens über den Refresh-Endpunkt, solange die
  Sitzung gültig ist.

# 17. Zusammenfassung

Die Anwendung bildet ein digitales, geschütztes Adress- und
Mitgliederverzeichnis ab. Sie kombiniert Selbstverwaltung persönlicher
Daten, Corps-spezifische Mitgliedsinformationen, durchsuchbare
Mitgliederlisten und ein feldgenaues Datenschutzmodell. Die API ist so
aufgebaut, dass ein späteres Web-Frontend die fachlichen Funktionen
vollständig über REST/JSON nutzen kann, ohne dass die Sichtbarkeitslogik
im Frontend nachgebildet werden muss.
