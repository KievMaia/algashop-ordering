package com.algaworks.algashop.ordering.domain.model.valueobject;

import java.util.Objects;

public record ProductName(String name) {
    public ProductName {
        Objects.requireNonNull(name);
        if (name.isBlank()) {
            throw new IllegalArgumentException("Name must not be blank");
        }
        name = name.trim();
    }

    public static ProductName of(String value) {
        return new ProductName(value);
    }

    @Override
    public String toString() {
        return name;
    }
}
