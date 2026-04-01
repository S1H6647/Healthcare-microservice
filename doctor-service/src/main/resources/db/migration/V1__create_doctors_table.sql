CREATE TABLE doctors
(
    id               BIGSERIAL PRIMARY KEY,
    first_name       VARCHAR(100)   NOT NULL,
    last_name        VARCHAR(100)   NOT NULL,
    email            VARCHAR(150)   NOT NULL UNIQUE,
    phone            VARCHAR(20),
    specialization   VARCHAR(100)   NOT NULL,
    license_number   VARCHAR(50)    NOT NULL UNIQUE,
    available_days   VARCHAR(100), -- e.g. "MONDAY,WEDNESDAY,FRIDAY"
    consultation_fee DECIMAL(10, 2) NOT NULL,
    created_at       TIMESTAMP      NOT NULL DEFAULT NOW(),
    updated_at       TIMESTAMP      NOT NULL DEFAULT NOW()
);