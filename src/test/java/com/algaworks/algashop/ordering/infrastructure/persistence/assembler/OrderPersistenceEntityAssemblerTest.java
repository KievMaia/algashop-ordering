package com.algaworks.algashop.ordering.infrastructure.persistence.assembler;

import com.algaworks.algashop.ordering.domain.model.entity.order.OrderTestDataBuilder;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

class OrderPersistenceEntityAssemblerTest {

    private final OrderPersistenceEntityAssembler assembler = new OrderPersistenceEntityAssembler();

    @Test
    void shouldConvertToDomain() {
        var order = OrderTestDataBuilder.anOrder().build();
        var orderPersistenceEntity = assembler.fromDomain(order);

        Assertions.assertThat(orderPersistenceEntity).satisfies(
                o -> Assertions.assertThat(o.getId()).isEqualTo(order.id().value().toLong()),
                o -> Assertions.assertThat(o.getCustomerId()).isEqualTo(order.customerId().value()),
                o -> Assertions.assertThat(o.getTotalAmount()).isEqualTo(order.totalAmount().value()),
                o -> Assertions.assertThat(o.getTotalItems()).isEqualTo(order.totalItems().value()),
                o -> Assertions.assertThat(o.getStatus()).isEqualTo(order.status().name()),
                o -> Assertions.assertThat(o.getPaymentMethod()).isEqualTo(order.paymentMethod().name()),
                o -> Assertions.assertThat(o.getPlacedAt()).isEqualTo(order.placedAt()),
                o -> Assertions.assertThat(o.getPaidAt()).isEqualTo(order.paidAt()),
                o -> Assertions.assertThat(o.getCanceledAt()).isEqualTo(order.canceledAt()),
                o -> Assertions.assertThat(o.getReadyAt()).isEqualTo(order.readyAt())
        );
    }

    @Test
    void shouldMerge() {

    }
}