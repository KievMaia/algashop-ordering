package com.algaworks.algashop.ordering.domain.model.valueobject;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class QuantityTest {

    @Test
    void shouldCreateQuantityWhenValueIsZeroOrPositive() {
        var zero = new Quantity(0);
        var three = new Quantity(3);
        assertEquals(0, zero.value());
        assertEquals(3, three.value());
    }

    @Test
    void shouldThrowExceptionWhenValueIsNegative() {
        assertThrows(IllegalArgumentException.class, () -> new Quantity(-1));
    }

    @Test
    void shouldThrowExceptionWhenValueIsNull() {
        assertThrows(NullPointerException.class, () -> new Quantity(null));
    }

    @Test
    void shouldExposeConstantZero() {
        assertEquals(0, Quantity.ZERO.value());
    }

    @Test
    void shouldAddQuantitiesCorrectly() {
        var q1 = new Quantity(2);
        var q2 = new Quantity(3);
        var result = q1.add(q2);
        assertEquals(new Quantity(5), result);
    }

    @Test
    void shouldThrowExceptionWhenAddingNullQuantity() {
        var q1 = new Quantity(2);
        assertThrows(NullPointerException.class, () -> q1.add(null));
    }

    @Test
    void shouldCompareQuantitiesCorrectly() {
        var smaller = new Quantity(1);
        var equal = new Quantity(1);
        var greater = new Quantity(5);
        assertTrue(smaller.compareTo(greater) < 0);
        assertTrue(greater.compareTo(smaller) > 0);
        assertEquals(0, smaller.compareTo(equal));
    }

    @Test
    void shouldThrowExceptionWhenComparingWithNull() {
        var q1 = new Quantity(1);
        assertThrows(NullPointerException.class, () -> q1.compareTo(null));
    }

    @Test
    void shouldReturnTrueWhenQuantityIsAtLeastOne() {
        var q = new Quantity(1);
        var result = q.isAtLeastOne();
        assertTrue(result);
    }

    @Test
    void shouldReturnFalseWhenQuantityIsZero() {
        var q = new Quantity(0);
        var result = q.isAtLeastOne();
        assertFalse(result);
    }

    @Test
    void shouldReturnValueAsString() {
        var q = new Quantity(7);
        var result = q.toString();
        assertEquals("7", result);
    }
}
