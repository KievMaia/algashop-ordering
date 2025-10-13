package com.algaworks.algashop.ordering.domain.valueobject;

import java.util.Objects;

public record ProductName(String name) {
    public ProductName {
        Objects.requireNonNull(name);
        if (name.isBlank()) {
            throw new IllegalArgumentException("Name must not be blank");
        }
        name = name.trim();
    }

    @Override
    public String toString() {
        return name;
    }
}
