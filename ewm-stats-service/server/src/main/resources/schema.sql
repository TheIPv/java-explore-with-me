DROP TABLE IF EXISTS hits;

CREATE TABLE IF NOT EXISTS hits
(
    id  INTEGER GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    app VARCHAR(32),
    uri VARCHAR(128),
    ip  VARCHAR(16),
    timestamp timestamp WITHOUT TIME ZONE
);