package org.timur.roadmap.currencyexchange.model;

import java.math.BigDecimal;

public record Conversion(
        Currency baseCurrency,
        Currency targetCurrency,
        BigDecimal rate,
        BigDecimal amount,
        BigDecimal convertedAmount
) {}
