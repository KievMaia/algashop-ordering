package com.algaworks.algashop.ordering.domain.valueobject;

import com.algaworks.algashop.ordering.domain.exception.ErrorMessages;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Objects;

public record Money(BigDecimal value) implements Comparable<Money> {

    private static final RoundingMode ROUNDING_MODE = RoundingMode.HALF_EVEN;
    private static final BigDecimal ZERO = new BigDecimal("0").setScale(0, ROUNDING_MODE);

    public Money {
        Objects.requireNonNull(value);
        value = value.setScale(2, RoundingMode.HALF_EVEN);
        if (value.compareTo(ZERO) < 0) {
            throw new IllegalArgumentException(ErrorMessages.VALIDATION_NEGATIVE_VALUE);
        }
    }

    public Money(String number) {
        this(new BigDecimal(Objects.requireNonNull(number)));
    }

    public Money multiply(Quantity quantity) {
        Objects.requireNonNull(quantity, "Quantity cannot be null");
        if (!quantity.isAtLeastOne()) {
            throw new IllegalArgumentException("Quantity must be at least 1 to multiply");
        }
        return new Money(this.value.multiply(BigDecimal.valueOf(quantity.value())));
    }

    public Money add(Money money) {
        Objects.requireNonNull(money);
        return new Money(this.value.add(money.value()));
    }

    public Money divide(Money money) {
        Objects.requireNonNull(money);
        return new Money(this.value.divide(money.value(), 2, RoundingMode.HALF_EVEN));
    }

    @Override
    public int compareTo(Money o) {
        return value.compareTo(o.value);
    }

    @Override
    public String toString() {
        return value().toString();
    }
}
