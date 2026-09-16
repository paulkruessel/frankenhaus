package de.franconia.tuebingen.adh.privacy;

public enum PrivacyField {

    EMAIL(PrivacyResourceType.PROFILE),
    TITLE(PrivacyResourceType.PROFILE),
    FIRST_NAME(PrivacyResourceType.PROFILE),
    LAST_NAME(PrivacyResourceType.PROFILE),
    MOBILE_PHONE(PrivacyResourceType.PROFILE),
    BIRTH_DATE(PrivacyResourceType.PROFILE),
    BIRTH_PLACE(PrivacyResourceType.PROFILE),
    WIKIPEDIA_URL(PrivacyResourceType.PROFILE),
    LINKEDIN_URL(PrivacyResourceType.PROFILE),
    XING_URL(PrivacyResourceType.PROFILE),
    FACEBOOK_URL(PrivacyResourceType.PROFILE),
    TWITTER_URL(PrivacyResourceType.PROFILE),
    INSTAGRAM_URL(PrivacyResourceType.PROFILE),
    ADDITIONAL_INFORMATION(PrivacyResourceType.PROFILE),
    ACADEMIC_DEGREE(PrivacyResourceType.PROFILE),
    FIELD_OF_STUDY(PrivacyResourceType.PROFILE),
    JOB_TITLE(PrivacyResourceType.PROFILE),
    COMPANY(PrivacyResourceType.PROFILE),
    POSITION(PrivacyResourceType.PROFILE),
    WEBSITE(PrivacyResourceType.PROFILE),
    EMPLOYMENT_STATUS(PrivacyResourceType.PROFILE),

    ADDRESS_TYPE(PrivacyResourceType.ADDRESS),
    ADDRESS_STREET(PrivacyResourceType.ADDRESS),
    ADDRESS_HOUSE_NUMBER(PrivacyResourceType.ADDRESS),
    ADDRESS_POSTAL_CODE(PrivacyResourceType.ADDRESS),
    ADDRESS_CITY(PrivacyResourceType.ADDRESS),
    ADDRESS_COUNTRY(PrivacyResourceType.ADDRESS),

    CORPS_NAME(PrivacyResourceType.CORPS_MEMBERSHIP),
    NAME_IN_CORPS(PrivacyResourceType.CORPS_MEMBERSHIP),
    CORPS_LIST_NUMBER(PrivacyResourceType.CORPS_MEMBERSHIP),
    BAND_NUMBER(PrivacyResourceType.CORPS_MEMBERSHIP),
    BRACKETS(PrivacyResourceType.CORPS_MEMBERSHIP),
    MEMBERSHIP_STATUS(PrivacyResourceType.CORPS_MEMBERSHIP),
    ADMISSION_DATE(PrivacyResourceType.CORPS_MEMBERSHIP),
    RECEPTION_DATE(PrivacyResourceType.CORPS_MEMBERSHIP),
    PHILISTRATION_DATE(PrivacyResourceType.CORPS_MEMBERSHIP),
    RECEPTION_PHOTO(PrivacyResourceType.CORPS_MEMBERSHIP),
    LEIBBURSCH(PrivacyResourceType.CORPS_MEMBERSHIP);

    private final PrivacyResourceType resourceType;

    PrivacyField(
            PrivacyResourceType resourceType
    ) {
        this.resourceType = resourceType;
    }

    public PrivacyResourceType resourceType() {
        return resourceType;
    }

    public boolean supports(
            PrivacyResourceType resourceType
    ) {
        return this.resourceType == resourceType;
    }
}