package com.algaworks.algashop.ordering.domain.valueobject.id;

import io.hypersistence.tsid.TSID;

import java.util.Objects;

public record OrderItemId(TSID value) {
    public OrderItemId {
        Objects.requireNonNull(value, "value must not be null");
    }

    public OrderItemId(Long value) {
        this(TSID.from(value));
    }

    public OrderItemId(String value) {
        this(TSID.from(value));
    }

    @Override
    public String toString() {
        return value.toString();
    }
}
