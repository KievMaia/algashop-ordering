package com.algaworks.algashop.ordering.domain.model.valueobject;

import com.algaworks.algashop.ordering.domain.model.commons.Money;
import com.algaworks.algashop.ordering.domain.model.commons.Quantity;
import org.junit.jupiter.api.Test;
import java.math.BigDecimal;
import java.math.RoundingMode;

import static org.junit.jupiter.api.Assertions.*;

class MoneyTest {
    @Test
    void shouldCreateMoneyWithValidValue() {
        var money = new Money(new BigDecimal("10.50"));
        assertEquals(new BigDecimal("10.50").setScale(2, RoundingMode.HALF_EVEN), money.value());
    }

    @Test
    void shouldCreateMoneyFromString() {
        var money = new Money("15.00");
        assertEquals(new BigDecimal("15.00").setScale(2, RoundingMode.HALF_EVEN), money.value());
    }

    @Test
    void shouldThrowExceptionWhenValueIsNegative() {
        assertThrows(IllegalArgumentException.class, () -> new Money("-1"));
    }

    @Test
    void shouldThrowExceptionWhenValueIsNull() {
        assertThrows(NullPointerException.class, () -> new Money((BigDecimal) null));
    }

    @Test
    void shouldAddMoneyValues() {
        var m1 = new Money("10.00");
        var m2 = new Money("5.50");
        var result = m1.add(m2);
        assertEquals(new Money("15.50"), result);
    }

    @Test
    void shouldMultiplyByQuantity() {
        var money = new Money("10.00");
        var quantity = new Quantity(3);
        var result = money.multiply(quantity);
        assertEquals(new Money("30.00"), result);
    }

    @Test
    void shouldThrowExceptionWhenQuantityIsZeroOrNegative() {
        var money = new Money("10.00");
        assertThrows(IllegalArgumentException.class, () -> money.multiply(new Quantity(0)));
    }

    @Test
    void shouldDivideMoneyValues() {
        var m1 = new Money("10.00");
        var m2 = new Money("4.00");
        var result = m1.divide(m2);
        assertEquals(new Money("2.50"), result);
    }

    @Test
    void shouldCompareMoneyValues() {
        var smaller = new Money("5.00");
        var larger = new Money("10.00");
        assertTrue(smaller.compareTo(larger) < 0);
        assertTrue(larger.compareTo(smaller) > 0);
        assertEquals(0, smaller.compareTo(new Money("5.00")));
    }
}
