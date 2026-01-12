package com.algaworks.algashop.ordering.domain.model.valueobject;

import java.util.Objects;

public record Phone(String value) {
    public Phone {
        Objects.requireNonNull(value);
        if (value.isBlank()) {
            throw new IllegalArgumentException();
        }
        value = value.trim();
    }

    @Override
    public String toString() {
        return value;
    }
}
