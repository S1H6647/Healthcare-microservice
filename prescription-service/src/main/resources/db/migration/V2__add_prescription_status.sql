ALTER TABLE prescription
    ADD COLUMN prescription_status VARCHAR(20) NOT NULL DEFAULT 'PENDING';

ALTER TABLE prescription
    ADD CONSTRAINT chk_prescription_status
    CHECK (prescription_status IN ('PENDING', 'APPROVED', 'DISPENSED'));
