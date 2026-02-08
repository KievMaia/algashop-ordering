package com.algaworks.algashop.ordering.infrastructure.persistence.assembler;

import com.algaworks.algashop.ordering.domain.model.entity.order.OrderTestDataBuilder;
import com.algaworks.algashop.ordering.infrastructure.persistence.entity.CustomerPersistenceEntity;
import com.algaworks.algashop.ordering.infrastructure.persistence.entity.CustomerPersistenceEntityTestDataBuilder;
import com.algaworks.algashop.ordering.infrastructure.persistence.entity.OrderPersistenceEntityTestDataBuilder;
import com.algaworks.algashop.ordering.infrastructure.persistence.repository.CustomerPersistenceEntityRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.HashSet;
import java.util.UUID;
import java.util.stream.Collectors;

@ExtendWith(MockitoExtension.class)
class OrderPersistenceEntityAssemblerTest {

    @Mock
    private CustomerPersistenceEntityRepository  customerPersistenceEntityRepository;

    @InjectMocks
    private OrderPersistenceEntityAssembler assembler;

    @BeforeEach
    public void setUp() {
        Mockito.when(customerPersistenceEntityRepository.getReferenceById(Mockito.any(UUID.class)))
                .then(a -> {
                    UUID customerId = a.getArgument(0);
                    return CustomerPersistenceEntityTestDataBuilder.aCustomer().id(customerId).build();
                });
    }

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
    void givenOrderWithNoItems_shouldRemovePersistenceEntityItems() {
        var order = OrderTestDataBuilder.anOrder().withItems(false).build();

        var orderPersistenceEntity = OrderPersistenceEntityTestDataBuilder.existingOrder().build();

        Assertions.assertThat(order.items()).isEmpty();
        Assertions.assertThat(orderPersistenceEntity.getItems()).isNotEmpty();

        assembler.merge(orderPersistenceEntity, order);

        Assertions.assertThat(orderPersistenceEntity.getItems()).isEmpty();
    }

    @Test
    public void givenOrderWithItems_shouldAddToPersistenceEntity() {
        var order = OrderTestDataBuilder.anOrder().withItems(true).build();
        var persistenceEntity = OrderPersistenceEntityTestDataBuilder.existingOrder().items(new HashSet<>()).build();

        Assertions.assertThat(order.items()).isNotEmpty();
        Assertions.assertThat(persistenceEntity.getItems()).isEmpty();

        assembler.merge(persistenceEntity, order);

        Assertions.assertThat(persistenceEntity.getItems()).isNotEmpty();
        Assertions.assertThat(order.items()).hasSize(order.items().size());
    }

    @Test
    public void givenOrderWithItems_whenMerge_shouldRemoveMergeCorrectly() {
        var order = OrderTestDataBuilder.anOrder().withItems(true).build();

        Assertions.assertThat(order.items().size()).isEqualTo(2);

        var orderItemPersistenceEntities = order.items().stream().map(assembler::fromDomain)
                .collect(Collectors.toSet());

        var persistenceEntity =
                OrderPersistenceEntityTestDataBuilder.existingOrder().items(new HashSet<>())
                        .items(orderItemPersistenceEntities)
                        .build();

        var orderItem = order.items().iterator().next();
        order.removeItem(orderItem.id());

        assembler.merge(persistenceEntity, order);

        Assertions.assertThat(persistenceEntity.getItems()).isNotEmpty();
        Assertions.assertThat(order.items()).hasSize(order.items().size());
    }
}