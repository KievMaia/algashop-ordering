package com.algaworks.algashop.ordering.domain.model.repository;

import com.algaworks.algashop.ordering.domain.model.entity.OrderStatusEnum;
import com.algaworks.algashop.ordering.domain.model.entity.customer.CustomerTestDataBuilder;
import com.algaworks.algashop.ordering.domain.model.entity.order.OrderTestDataBuilder;
import com.algaworks.algashop.ordering.domain.model.valueobject.Money;
import com.algaworks.algashop.ordering.domain.model.valueobject.id.CustomerId;
import com.algaworks.algashop.ordering.domain.model.valueobject.id.OrderId;
import com.algaworks.algashop.ordering.infrastructure.persistence.assembler.CustomerPersistenceEntityAssembler;
import com.algaworks.algashop.ordering.infrastructure.persistence.assembler.OrderPersistenceEntityAssembler;
import com.algaworks.algashop.ordering.infrastructure.persistence.disassembler.CustomerPersistenceEntityDisassembler;
import com.algaworks.algashop.ordering.infrastructure.persistence.disassembler.OrderPersistenceEntityDisassembler;
import com.algaworks.algashop.ordering.infrastructure.persistence.provider.CustomersPersistenceProvider;
import com.algaworks.algashop.ordering.infrastructure.persistence.provider.OrdersPersistenceProvider;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.orm.ObjectOptimisticLockingFailureException;

import java.time.Year;

import static com.algaworks.algashop.ordering.domain.model.entity.customer.CustomerTestDataBuilder.DEFAULT_CUSTOMER_ID;
import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@Import({
        OrdersPersistenceProvider.class,
        OrderPersistenceEntityAssembler.class,
        OrderPersistenceEntityDisassembler.class,
        CustomersPersistenceProvider.class,
        CustomerPersistenceEntityAssembler.class,
        CustomerPersistenceEntityDisassembler.class
})
class OrdersIT {

    private final Orders orders;
    private final Customers customers;

    CustomerId customerId = DEFAULT_CUSTOMER_ID;

    @Autowired
    public OrdersIT(Orders orders, final Customers customers) {
        this.orders = orders;
        this.customers = customers;
    }

    @BeforeEach
    public void setup() {
        if (!customers.exists(DEFAULT_CUSTOMER_ID)){
            customers.add(
                    CustomerTestDataBuilder.existingCustomer().build()
            );
        }
    }

    @Test
    public void shouldPersistAndFind() {
        var originalOrder = OrderTestDataBuilder.anOrder().build();
        var orderId = originalOrder.id();
        orders.add(originalOrder);

        var possibleOrder = orders.ofId(orderId);
        assertThat(possibleOrder).isPresent();

        var savedOrder = possibleOrder.get();

        assertThat(savedOrder).satisfies(
                s -> assertThat(s.id()).isEqualTo(orderId),
                s -> assertThat(s.customerId()).isEqualTo(originalOrder.customerId()),
                s -> assertThat(s.totalAmount()).isEqualTo(originalOrder.totalAmount()),
                s -> assertThat(s.totalItems()).isEqualTo(originalOrder.totalItems()),
                s -> assertThat(s.placedAt()).isEqualTo(originalOrder.placedAt()),
                s -> assertThat(s.paidAt()).isEqualTo(originalOrder.paidAt()),
                s -> assertThat(s.canceledAt()).isEqualTo(originalOrder.canceledAt()),
                s -> assertThat(s.readyAt()).isEqualTo(originalOrder.readyAt()),
                s -> assertThat(s.status()).isEqualTo(originalOrder.status()),
                s -> assertThat(s.paymentMethod()).isEqualTo(originalOrder.paymentMethod())
        );
    }

    @Test
    public void shouldUpdateExistingOrder() {
        var originalOrder = OrderTestDataBuilder.anOrder()
                .orderStatusEnum(OrderStatusEnum.PLACED)
                .build();

        orders.add(originalOrder);

        originalOrder = orders.ofId(originalOrder.id()).orElseThrow();

        originalOrder.markAsPaid();

        orders.add(originalOrder);

        originalOrder = orders.ofId(originalOrder.id()).orElseThrow();

        Assertions.assertThat(originalOrder.isPaid()).isTrue();
    }

    @Test
    public void shouldNotAllowStaleUpdates() {
        var originalOrder = OrderTestDataBuilder.anOrder()
                .orderStatusEnum(OrderStatusEnum.PLACED)
                .build();

        orders.add(originalOrder);

        var orderT1 = orders.ofId(originalOrder.id()).orElseThrow();
        var orderT2 = orders.ofId(originalOrder.id()).orElseThrow();

        orderT1.markAsPaid();
        orders.add(orderT1);

        orderT2.markAsCancelled();

        var savedOrder = orders.ofId(originalOrder.id()).orElseThrow();

        Assertions.assertThat(savedOrder.canceledAt()).isNull();
        Assertions.assertThat(savedOrder.paidAt()).isNotNull();

        Assertions.assertThatExceptionOfType(ObjectOptimisticLockingFailureException.class)
                .isThrownBy(() -> orders.add(orderT2));
    }

    @Test
    public void shouldCountExistingOrders() {
        Assertions.assertThat(orders.count()).isZero();

        var order1 = OrderTestDataBuilder.anOrder().build();
        var order2 = OrderTestDataBuilder.anOrder().build();

        orders.add(order1);
        orders.add(order2);

        Assertions.assertThat(orders.count()).isEqualTo(2);
    }

    @Test
    public void shouldReturnIfOrderExists() {
        var order = OrderTestDataBuilder.anOrder().build();
        orders.add(order);

        Assertions.assertThat(orders.exists(order.id())).isTrue();
        Assertions.assertThat(orders.exists(new OrderId())).isFalse();
    }

    @Test
    public void shouldListExistingOrderByYear() {
        orders.add(OrderTestDataBuilder.anOrder().orderStatusEnum(OrderStatusEnum.PLACED).build());
        orders.add(OrderTestDataBuilder.anOrder().orderStatusEnum(OrderStatusEnum.PLACED).build());
        orders.add(OrderTestDataBuilder.anOrder().orderStatusEnum(OrderStatusEnum.CANCELED).build());
        orders.add(OrderTestDataBuilder.anOrder().orderStatusEnum(OrderStatusEnum.DRAFT).build());

        var listedOrders = orders.placedByCustomerYear(customerId, Year.now());
        Assertions.assertThat(listedOrders).isNotEmpty();
        Assertions.assertThat(listedOrders.size()).isEqualTo(2);

        listedOrders = orders.placedByCustomerYear(customerId, Year.now().minusYears(1));
        Assertions.assertThat(listedOrders).isEmpty();

        listedOrders = orders.placedByCustomerYear(new CustomerId(), Year.now());
        Assertions.assertThat(listedOrders).isEmpty();
    }

    @Test
    public void shouldReturnTotalSoldByCustomer() {
        var order1 = OrderTestDataBuilder.anOrder().orderStatusEnum(OrderStatusEnum.PAID).build();
        var order2 = OrderTestDataBuilder.anOrder().orderStatusEnum(OrderStatusEnum.PAID).build();

        orders.add(order1);
        orders.add(order2);

        orders.add(
                OrderTestDataBuilder.anOrder().orderStatusEnum(OrderStatusEnum.CANCELED).build()
        );
        orders.add(
                OrderTestDataBuilder.anOrder().orderStatusEnum(OrderStatusEnum.PLACED).build()
        );

        var expectedTotalAmount = order1.totalAmount().add(order2.totalAmount());

        Assertions.assertThat(orders.totalSoldForCustomer(customerId)).isEqualTo(expectedTotalAmount);

        Assertions.assertThat(orders.totalSoldForCustomer(new CustomerId())).isEqualTo(Money.ZERO);
    }

    @Test
    public void shouldReturnSalesQuantityByCustomer() {
        var order1 = OrderTestDataBuilder.anOrder().orderStatusEnum(OrderStatusEnum.PAID).build();
        var order2 = OrderTestDataBuilder.anOrder().orderStatusEnum(OrderStatusEnum.PAID).build();

        orders.add(order1);
        orders.add(order2);

        orders.add(
                OrderTestDataBuilder.anOrder().orderStatusEnum(OrderStatusEnum.CANCELED).build()
        );
        orders.add(
                OrderTestDataBuilder.anOrder().orderStatusEnum(OrderStatusEnum.PLACED).build()
        );

        Assertions.assertThat(orders.salesQuantityByCustomerInYear(customerId, Year.now())).isEqualTo(2L);
        Assertions.assertThat(orders.salesQuantityByCustomerInYear(customerId, Year.now().minusYears(1))).isZero();
    }
}