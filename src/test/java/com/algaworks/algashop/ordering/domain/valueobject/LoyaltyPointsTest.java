package com.algaworks.algashop.ordering.domain.valueobject;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

class LoyaltyPointsTest {

    @Test
    void shouldGenerateWithValue() {
        var loyaltyPoints = new LoyaltyPoints(10);
        assertThat(loyaltyPoints.value()).isEqualTo(10);
    }

    @Test
    void shouldAddValue() {
        var loyaltyPoints = new LoyaltyPoints(10);
        assertThat(loyaltyPoints.add(5).value()).isEqualTo(15);
    }

    @Test
    void shouldNotAddValue() {
        var loyaltyPoints = new LoyaltyPoints(10);
        assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() -> loyaltyPoints.add(-5));
        assertThat(loyaltyPoints.value()).isEqualTo(10);
    }

}