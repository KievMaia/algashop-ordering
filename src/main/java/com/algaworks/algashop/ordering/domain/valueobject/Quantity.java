package com.algaworks.algashop.ordering.domain.valueobject;

import com.algaworks.algashop.ordering.domain.exception.ErrorMessages;

import java.util.Objects;

public record Quantity(Integer value) implements Comparable<Quantity> {

    public static final Quantity ZERO = new Quantity(0);

    public Quantity {
        Objects.requireNonNull(value);
        if (value < 0) {
            throw new IllegalArgumentException(ErrorMessages.VALIDATION_NEGATIVE_VALUE);
        }
    }

    public Quantity add(Quantity other) {
        Objects.requireNonNull(other);
        return new Quantity(this.value + other.value);
    }

    public boolean isAtLeastOne() {
        return this.value >= 1;
    }

    @Override
    public int compareTo(Quantity o) {
        return this.value.compareTo(o.value);
    }

    @Override
    public String toString() {
        return value.toString();
    }
}
