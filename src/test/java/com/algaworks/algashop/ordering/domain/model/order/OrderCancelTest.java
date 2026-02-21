package com.algaworks.algashop.ordering.domain.model.order;

import com.algaworks.algashop.ordering.domain.model.customer.CustomerId;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import static com.algaworks.algashop.ordering.domain.model.order.OrderStatusEnum.CANCELED;
import static com.algaworks.algashop.ordering.domain.model.order.OrderStatusEnum.DRAFT;
import static com.algaworks.algashop.ordering.domain.model.order.OrderStatusEnum.PAID;
import static com.algaworks.algashop.ordering.domain.model.order.OrderStatusEnum.PLACED;
import static com.algaworks.algashop.ordering.domain.model.order.OrderStatusEnum.READY;

public class OrderCancelTest {

    @Test
    void givenEmptyOrder_whenCancel_shouldAllow() {
        var order = Order.draft(new CustomerId());

        order.markAsCancelled();

        Assertions.assertWith(order,
                (i) -> Assertions.assertThat(i.status()).isEqualTo(CANCELED),
                (i) -> Assertions.assertThat(i.isCanceled()).isTrue(),
                (i) -> Assertions.assertThat(i.canceledAt()).isNotNull()
        );
    }

    @Test
    public void givenFilledOrder_whenCancel_shouldAllow() {
        var order = OrderTestDataBuilder.anOrder().orderStatusEnum(DRAFT).build();

        order.markAsCancelled();

        Assertions.assertWith(order,
                (o) -> Assertions.assertThat(o.status()).isEqualTo(CANCELED),
                (o) -> Assertions.assertThat(o.isCanceled()).isTrue(),
                (o) -> Assertions.assertThat(o.canceledAt()).isNotNull()
        );
    }

    @Test
    public void givenPlacedOrder_whenCanceledOrder_shouldPermitSuccessfully() {
        var order = OrderTestDataBuilder.anOrder().orderStatusEnum(PLACED).build();

        order.markAsCancelled();

        Assertions.assertWith(order,
                (o) -> Assertions.assertThat(o.status()).isEqualTo(CANCELED),
                (o) -> Assertions.assertThat(o.canceledAt()).isNotNull()
        );
    }

    @Test
    public void givenPaidOrder_whenCanceledOrder_shouldPermitSuccessfully() {
        var order = OrderTestDataBuilder.anOrder().orderStatusEnum(PAID).build();

        order.markAsCancelled();

        Assertions.assertWith(order,
                (o) -> Assertions.assertThat(o.status()).isEqualTo(CANCELED),
                (o) -> Assertions.assertThat(o.canceledAt()).isNotNull()
        );
    }

    @Test
    public void givenReadyOrder_whenCanceledOrder_shouldPermitSuccessfully() {
        var order = OrderTestDataBuilder.anOrder().orderStatusEnum(READY).build();

        order.markAsCancelled();

        Assertions.assertWith(order,
                (o) -> Assertions.assertThat(o.status()).isEqualTo(CANCELED),
                (o) -> Assertions.assertThat(o.canceledAt()).isNotNull()
        );
    }

    @Test
    public void givenCanceledOrder_whenCanceledOrder_shouldPermitSuccessfully() {
        var order = OrderTestDataBuilder.anOrder().orderStatusEnum(CANCELED).build();

        Assertions.assertThatExceptionOfType(OrderStatusCannotBeChangedException.class)
                .isThrownBy(order::markAsCancelled);

        Assertions.assertWith(order,
                (o) -> Assertions.assertThat(o.isCanceled()).isTrue(),
                (o) -> Assertions.assertThat(o.canceledAt()).isNotNull()
        );
    }
}
