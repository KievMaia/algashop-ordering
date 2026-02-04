package com.algaworks.algashop.ordering.domain.model.valueobject;

import java.util.Objects;

public record ProductName(String value) {
    public ProductName {
        Objects.requireNonNull(value);
        if (value.isBlank()) {
            throw new IllegalArgumentException("Name must not be blank");
        }
        value = value.trim();
    }

    public static ProductName of(String value) {
        return new ProductName(value);
    }

    @Override
    public String toString() {
        return value;
    }
}
