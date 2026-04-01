-- V1__create_medicine_table.sql

CREATE TABLE medicine
(
    id                BIGINT PRIMARY KEY,
    medicine_name     VARCHAR(255) NOT NULL,
    short_description TEXT,
    quantity          INT          NOT NULL,
    medicine_type     VARCHAR(255) NOT NULL,
    created_at        TIMESTAMP    NOT NULL,
    updated_at        TIMESTAMP    NOT NULL
);
