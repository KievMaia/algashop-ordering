package com.algaworks.algashop.ordering.domain.model.order;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import static com.algaworks.algashop.ordering.domain.model.order.OrderStatusEnum.CANCELED;
import static com.algaworks.algashop.ordering.domain.model.order.OrderStatusEnum.DRAFT;
import static com.algaworks.algashop.ordering.domain.model.order.OrderStatusEnum.PAID;
import static com.algaworks.algashop.ordering.domain.model.order.OrderStatusEnum.PLACED;
import static com.algaworks.algashop.ordering.domain.model.order.OrderStatusEnum.READY;

public class OrderMarkAsReadyTest {

    @Test
    public void givenPaidOrder_whenMarkAsReady_shouldUpdateStatusAndTimestamp() {
        var order = OrderTestDataBuilder.anOrder().orderStatusEnum(PAID).build();

        order.markAsReady();

        Assertions.assertWith(order,
                (o) -> Assertions.assertThat(o.status()).isEqualTo(READY),
                (o) -> Assertions.assertThat(o.readyAt()).isNotNull()
        );
    }

    @Test
    public void givenDraftOrder_whenMarkAsReady_shouldThrowExceptionAndNotChangeState() {
        var order = OrderTestDataBuilder.anOrder().orderStatusEnum(DRAFT).build();

        Assertions.assertThatExceptionOfType(OrderStatusCannotBeChangedException.class)
                .isThrownBy(order::markAsReady);

        Assertions.assertWith(order,
                (o) -> Assertions.assertThat(o.status()).isEqualTo(DRAFT),
                (o) -> Assertions.assertThat(o.readyAt()).isNull()
        );
    }

    @Test
    public void givenPlacedOrder_whenMarkAsReady_shouldThrowExceptionAndNotChangeState() {
        var order = OrderTestDataBuilder.anOrder().orderStatusEnum(PLACED).build();

        Assertions.assertThatExceptionOfType(OrderStatusCannotBeChangedException.class)
                .isThrownBy(order::markAsReady);

        Assertions.assertWith(order,
                (o) -> Assertions.assertThat(o.status()).isEqualTo(PLACED),
                (o) -> Assertions.assertThat(o.readyAt()).isNull()
        );
    }

    @Test
    public void givenReadyOrder_whenMarkAsReadyAgain_ShouldThrowException() {
        var order = OrderTestDataBuilder.anOrder().orderStatusEnum(READY).build();

        Assertions.assertThatExceptionOfType(OrderStatusCannotBeChangedException.class)
                .isThrownBy(order::markAsReady);

        Assertions.assertWith(order,
                (o) -> Assertions.assertThat(o.status()).isEqualTo(READY),
                (o) -> Assertions.assertThat(o.readyAt()).isNotNull()
        );
    }

    @Test
    public void givenCanceledOrder_whenChangeStatus_ShouldGenerateException() {
        var order = OrderTestDataBuilder.anOrder().orderStatusEnum(CANCELED).build();

        Assertions.assertThatExceptionOfType(OrderStatusCannotBeChangedException.class)
                .isThrownBy(order::markAsReady);

        Assertions.assertThat(order.readyAt()).isNull();
    }
}
