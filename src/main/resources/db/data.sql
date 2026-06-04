INSERT OR IGNORE INTO Currencies(code, full_name, sign)
VALUES
    ('USD', 'US Dollar', '$'),
    ('EUR', 'Euro', '€'),
    ('JPY', 'Japanese Yen', '¥');


INSERT OR IGNORE INTO ExchangeRates(base_currency_id, target_currency_id, rate)
VALUES
    (1, 2, 0.870000), -- USD -> EUR
    (2, 1, 1.149425), -- EUR -> USD
    (1, 3, 0.740000), -- USD -> JPY
    (3, 1, 1.351351), -- JPY -> USD
    (2, 3, 0.850000), -- EUR -> JPY
    (3, 2, 1.176471); -- JPY -> EUR