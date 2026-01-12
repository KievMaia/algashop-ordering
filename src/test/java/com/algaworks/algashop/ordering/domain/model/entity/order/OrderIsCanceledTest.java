package com.algaworks.algashop.ordering.domain.model.entity.order;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import static com.algaworks.algashop.ordering.domain.model.entity.OrderStatusEnum.CANCELED;
import static com.algaworks.algashop.ordering.domain.model.entity.OrderStatusEnum.DRAFT;
import static com.algaworks.algashop.ordering.domain.model.entity.OrderStatusEnum.PAID;
import static com.algaworks.algashop.ordering.domain.model.entity.OrderStatusEnum.PLACED;
import static com.algaworks.algashop.ordering.domain.model.entity.OrderStatusEnum.READY;

public class OrderIsCanceledTest {

    @Test
    public void givenCanceledOrder_whenCanceledOrder_shouldReturnTrue() {
        var order = OrderTestDataBuilder.anOrder().orderStatusEnum(CANCELED).build();

        Assertions.assertThat(order.isCanceled()).isTrue();
    }

    @Test
    public void givenDraftOrder_whenCanceledOrder_shouldReturnTrue() {
        var order = OrderTestDataBuilder.anOrder().orderStatusEnum(DRAFT).build();

        Assertions.assertThat(order.isCanceled()).isFalse();
    }

    @Test
    public void givenPlacedOrder_whenCanceledOrder_shouldReturnTrue() {
        var order = OrderTestDataBuilder.anOrder().orderStatusEnum(PLACED).build();

        Assertions.assertThat(order.isCanceled()).isFalse();
    }

    @Test
    public void givenPaidOrder_whenCanceledOrder_shouldReturnTrue() {
        var order = OrderTestDataBuilder.anOrder().orderStatusEnum(PAID).build();

        Assertions.assertThat(order.isCanceled()).isFalse();
    }

    @Test
    public void givenReadyOrder_whenCanceledOrder_shouldReturnTrue() {
        var order = OrderTestDataBuilder.anOrder().orderStatusEnum(READY).build();

        Assertions.assertThat(order.isCanceled()).isFalse();
    }
}
