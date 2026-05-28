package com.example;

public record Currency(
        int id,
        String code,
        String fullName,
        String sign
) {}
