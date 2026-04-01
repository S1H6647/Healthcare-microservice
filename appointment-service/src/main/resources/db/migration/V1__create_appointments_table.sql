CREATE TYPE appointment_status AS ENUM (
    'PENDING',
    'CONFIRMED',
    'CANCELLED',
    'COMPLETED'
);

CREATE TABLE appointments (
      id              BIGSERIAL PRIMARY KEY,
      patient_id      BIGINT NOT NULL,
      doctor_id       BIGINT NOT NULL,
      patient_name    VARCHAR(200) NOT NULL,
      doctor_name     VARCHAR(200) NOT NULL,
      specialization  VARCHAR(100) NOT NULL,
      appointment_date DATE NOT NULL,
      start_time      TIME NOT NULL,
      end_time        TIME NOT NULL,
      status          appointment_status NOT NULL DEFAULT 'PENDING',
      notes           TEXT,
      created_at      TIMESTAMP NOT NULL DEFAULT NOW(),
      updated_at      TIMESTAMP NOT NULL DEFAULT NOW()
);
