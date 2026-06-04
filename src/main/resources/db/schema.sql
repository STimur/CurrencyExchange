CREATE TABLE IF NOT EXISTS Currencies
(
    id        INTEGER PRIMARY KEY AUTOINCREMENT,
    code      VARCHAR(10)  NOT NULL UNIQUE,
    full_name VARCHAR(100) NOT NULL,
    sign      VARCHAR(10)  NOT NULL
);

CREATE UNIQUE INDEX IF NOT EXISTS idx_currencies_code
    ON Currencies (code);


CREATE TABLE IF NOT EXISTS ExchangeRates
(
    id               INTEGER PRIMARY KEY AUTOINCREMENT,
    base_currency_id   INTEGER        NOT NULL,
    target_currency_id INTEGER        NOT NULL,
    rate             DECIMAL(6) NOT NULL,

    FOREIGN KEY (base_currency_id)
        REFERENCES Currencies (id),

    FOREIGN KEY (target_currency_id)
        REFERENCES Currencies (id)
);

CREATE UNIQUE INDEX IF NOT EXISTS idx_exchange_rates_currency_pair
    ON ExchangeRates (base_currency_id, target_currency_id);