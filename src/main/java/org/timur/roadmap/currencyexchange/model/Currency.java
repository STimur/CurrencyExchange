package org.timur.roadmap.currencyexchange.model;

public record Currency(
        int id,
        String code,
        String fullName,
        String sign
) {}
