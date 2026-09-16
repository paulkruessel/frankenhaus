CREATE TABLE users (
    id UUID PRIMARY KEY,
    email VARCHAR(320) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    enabled BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL
);

CREATE TABLE person_profiles (
    id UUID PRIMARY KEY,
    user_id UUID NOT NULL UNIQUE REFERENCES users(id) ON DELETE CASCADE,

    title VARCHAR(100),
    first_name VARCHAR(255),
    last_name VARCHAR(255) NOT NULL,

    mobile_phone VARCHAR(100),
    birth_date DATE,
    birth_place VARCHAR(255),

    wikipedia_url TEXT,
    linkedin_url TEXT,
    xing_url TEXT,
    facebook_url TEXT,
    twitter_url TEXT,
    instagram_url TEXT,

    additional_information TEXT,

    academic_degree VARCHAR(255),
    field_of_study VARCHAR(255),
    job_title VARCHAR(255),
    company VARCHAR(255),
    position VARCHAR(255),
    website TEXT,
    employment_status VARCHAR(100)
);

CREATE TABLE corps (
    id UUID PRIMARY KEY,
    name VARCHAR(255) NOT NULL UNIQUE
);

CREATE TABLE corps_memberships (
    id UUID PRIMARY KEY,

    user_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    corps_id UUID NOT NULL REFERENCES corps(id),

    corps_name VARCHAR(255),
    corps_list_number VARCHAR(100),
    band_number VARCHAR(100),
    brackets VARCHAR(255),

    membership_status VARCHAR(50),

    admission_date DATE,
    reception_date DATE,
    philistration_date DATE,

    reception_photo TEXT,

    leibbursch_id UUID REFERENCES corps_memberships(id),

    UNIQUE (user_id, corps_id)
);

CREATE TABLE addresses (
    id UUID PRIMARY KEY,
    user_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,

    type VARCHAR(50),

    street VARCHAR(255),
    house_number VARCHAR(50),
    postal_code VARCHAR(50),
    city VARCHAR(255),
    country VARCHAR(255)
);