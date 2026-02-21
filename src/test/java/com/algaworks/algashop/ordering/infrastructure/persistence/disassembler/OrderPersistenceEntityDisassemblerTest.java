package com.algaworks.algashop.ordering.infrastructure.persistence.disassembler;

import com.algaworks.algashop.ordering.domain.model.order.OrderStatusEnum;
import com.algaworks.algashop.ordering.domain.model.order.PaymentMethodEnum;
import com.algaworks.algashop.ordering.domain.model.commons.Money;
import com.algaworks.algashop.ordering.domain.model.commons.Quantity;
import com.algaworks.algashop.ordering.domain.model.order.OrderId;
import com.algaworks.algashop.ordering.infrastructure.persistence.entity.OrderPersistenceEntityTestDataBuilder;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

class OrderPersistenceEntityDisassemblerTest {

    private final OrderPersistenceEntityDisassembler disassembler = new OrderPersistenceEntityDisassembler();

    @Test
    public void shouldConvertFromPersistence() {
        var persistenceEntity = OrderPersistenceEntityTestDataBuilder.existingOrder().build();

        var domainEntity = disassembler.toDomainEntity(persistenceEntity);

        Assertions.assertThat(domainEntity).satisfies(
                d -> Assertions.assertThat(d.id()).isEqualTo(new OrderId(persistenceEntity.getId())),
                d -> Assertions.assertThat(d.customerId().value()).isEqualTo((persistenceEntity.getCustomer().getId())),
                d -> Assertions.assertThat(d.totalAmount()).isEqualTo(new Money(persistenceEntity.getTotalAmount())),
                d -> Assertions.assertThat(d.totalItems()).isEqualTo(new Quantity(persistenceEntity.getTotalItems())),
                d -> Assertions.assertThat(d.placedAt()).isEqualTo(persistenceEntity.getPlacedAt()),
                d -> Assertions.assertThat(d.paidAt()).isEqualTo(persistenceEntity.getPaidAt()),
                d -> Assertions.assertThat(d.canceledAt()).isEqualTo(persistenceEntity.getCanceledAt()),
                d -> Assertions.assertThat(d.readyAt()).isEqualTo(persistenceEntity.getReadyAt()),
                d -> Assertions.assertThat(d.status()).isEqualTo(OrderStatusEnum.valueOf(persistenceEntity.getStatus())),
                d -> Assertions.assertThat(d.paymentMethod()).isEqualTo(PaymentMethodEnum.valueOf(persistenceEntity.getPaymentMethod()))
        );
    }

    @Test
    public void shouldConvertFromDomainEntity() {
        var persistenceEntity = OrderPersistenceEntityTestDataBuilder.existingOrder().build();

        var domainEntity = disassembler.toDomainEntity(persistenceEntity);

        Assertions.assertThat(domainEntity.items()).isNotEmpty();
        Assertions.assertThat(domainEntity.items()).hasSize(2);
    }

}