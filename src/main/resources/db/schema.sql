CREATE TABLE IF NOT EXISTS Currencies
(
    id        INTEGER PRIMARY KEY AUTOINCREMENT,
    code      VARCHAR(10)  NOT NULL UNIQUE,
    full_name VARCHAR(100) NOT NULL,
    sign      VARCHAR(10)  NOT NULL
);

CREATE UNIQUE INDEX IF NOT EXISTS idx_currencies_code
    ON Currencies (code);