package com.algaworks.algashop.ordering.domain.model.valueobject.id;

import com.algaworks.algashop.ordering.domain.model.utility.IdGenerator;

import java.util.Objects;
import java.util.UUID;

public record ShoppingCartId(UUID value) {
    public ShoppingCartId {
        Objects.requireNonNull(value, "value must not be null");
    }

    public ShoppingCartId() {
        this(IdGenerator.generateTimeBasedUUID());
    }

    public ShoppingCartId(String value) {
        this(UUID.fromString(value));
    }

    @Override
    public String toString() {
        return value.toString();
    }
}
