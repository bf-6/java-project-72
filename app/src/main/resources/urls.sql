DROP TABLE IF EXISTS url_checks;
DROP TABLE IF EXISTS urls;

CREATE TABLE urls (
    id BIGINT GENERATED ALWAYS AS IDENTITY NOT NULL,
    name VARCHAR(255) NOT NULL,
    created_at TIMESTAMP,
    PRIMARY KEY (id)
);

CREATE TABLE url_checks (
    id BIGINT GENERATED ALWAYS AS IDENTITY NOT NULL,
    url_id BIGINT REFERENCES urls(id) NOT NULL,
    status_code INT NOT NULL,
    h1 VARCHAR(255),
    title VARCHAR(255),
    description TEXT,
    created_at TIMESTAMP,
    PRIMARY KEY (id)
);