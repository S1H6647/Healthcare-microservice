-- V4__add_dosage_to_medicine.sql

ALTER TABLE medicine ADD COLUMN dosage VARCHAR(50);
ALTER TABLE medicine ADD COLUMN dosage_type VARCHAR(10) NOT NULL DEFAULT 'MG';
