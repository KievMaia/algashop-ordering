package com.algaworks.algashop.ordering.domain.model.order;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import static com.algaworks.algashop.ordering.domain.model.order.OrderStatusEnum.*;

public class OrderIsReadyTest {

    @Test
    void givenOrderWithStatusReady_whenIsReady_shouldReturnTrue() {
        var order = OrderTestDataBuilder.anOrder().orderStatusEnum(READY).build();

        Assertions.assertThat(order.isReady()).isTrue();
    }

    @Test
    void givenOrderWithStatusPaid_whenIsReady_shouldReturnFalse() {
        var order = OrderTestDataBuilder.anOrder().orderStatusEnum(PAID).build();

        Assertions.assertThat(order.isReady()).isFalse();
    }

    @Test
    void givenOrderWithStatusDraft_whenIsReady_shouldReturnFalse() {
        var order = OrderTestDataBuilder.anOrder().orderStatusEnum(DRAFT).build();

        Assertions.assertThat(order.isReady()).isFalse();
    }

    @Test
    void givenOrderWithStatusCanceled_whenIsReady_shouldReturnFalse() {
        var order = OrderTestDataBuilder.anOrder().orderStatusEnum(CANCELED).build();

        Assertions.assertThat(order.isReady()).isFalse();
    }
}