package com.algaworks.algashop.ordering.application.order;

import com.algaworks.algashop.ordering.domain.model.customer.CustomerTestDataBuilder;
import com.algaworks.algashop.ordering.domain.model.customer.Customers;
import com.algaworks.algashop.ordering.domain.model.order.*;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Transactional
class OrderManagementApplicationServiceIT {

    @Autowired
    private OrderManagementApplicationService orderManagementApplicationService;

    @Autowired
    private Orders orders;

    @Autowired
    private Customers customers;

    @Test
    public void shouldCancelOrderSuccessfully() {
        var customer = CustomerTestDataBuilder.existingCustomer().build();
        customers.add(customer);

        var order = OrderTestDataBuilder.anOrder()
                .customerId(customer.id())
                .orderStatusEnum(OrderStatusEnum.READY)
                .build();

        orders.add(order);

        var orderId = order.id();

        orderManagementApplicationService.cancel(orderId.value().toLong());

        var canceledOrder = orders.ofId(orderId).orElseThrow();

        Assertions.assertThat(canceledOrder.canceledAt()).isNotNull();
    }

    @Test
    public void shouldThrowExceptionWhenCancelOrderInexistent() {
        Assertions.assertThatThrownBy(
                        () -> orderManagementApplicationService.cancel(1L))
                .isInstanceOf(OrderNotFoundException.class);
    }

    @Test
    public void shouldThrowExceptionWhenTryChangeInvalidStatusCancel() {
        var customer = CustomerTestDataBuilder.existingCustomer().build();
        customers.add(customer);

        var order = OrderTestDataBuilder.anOrder()
                .customerId(customer.id())
                .orderStatusEnum(OrderStatusEnum.PLACED)
                .build();

        orders.add(order);

        var orderId = order.id();

        orderManagementApplicationService.cancel(orderId.value().toLong());

        Assertions.assertThatThrownBy(
                        () -> orderManagementApplicationService.cancel(orderId.value().toLong()))
                .isInstanceOf(OrderStatusCannotBeChangedException.class);
    }

    @Test
    public void shouldPaidOrderSuccessfully() {
        var customer = CustomerTestDataBuilder.existingCustomer().build();
        customers.add(customer);

        var order = OrderTestDataBuilder.anOrder()
                .customerId(customer.id())
                .orderStatusEnum(OrderStatusEnum.PLACED)
                .build();

        orders.add(order);

        var orderId = order.id();

        orderManagementApplicationService.markAsPaid(orderId.value().toLong());

        var canceledOrder = orders.ofId(orderId).orElseThrow();

        Assertions.assertThat(canceledOrder.paidAt()).isNotNull();
    }

    @Test
    public void shouldThrowExceptionWhenPaidOrderInexistent() {
        Assertions.assertThatThrownBy(
                        () -> orderManagementApplicationService.markAsPaid(1L))
                .isInstanceOf(OrderNotFoundException.class);
    }

    @Test
    public void shouldThrowExceptionWhenTryChangeInvalidStatusPaid() {
        var customer = CustomerTestDataBuilder.existingCustomer().build();
        customers.add(customer);

        var order = OrderTestDataBuilder.anOrder()
                .customerId(customer.id())
                .orderStatusEnum(OrderStatusEnum.DRAFT)
                .build();

        orders.add(order);

        var orderId = order.id();

        Assertions.assertThatThrownBy(
                        () -> orderManagementApplicationService.markAsPaid(orderId.value().toLong()))
                .isInstanceOf(OrderStatusCannotBeChangedException.class);
    }

    @Test
    public void shouldReadyOrderSuccessfully() {
        var customer = CustomerTestDataBuilder.existingCustomer().build();
        customers.add(customer);

        var order = OrderTestDataBuilder.anOrder()
                .customerId(customer.id())
                .orderStatusEnum(OrderStatusEnum.PAID)
                .build();

        orders.add(order);

        var orderId = order.id();

        orderManagementApplicationService.markAsReady(orderId.value().toLong());

        var canceledOrder = orders.ofId(orderId).orElseThrow();

        Assertions.assertThat(canceledOrder.readyAt()).isNotNull();
    }

    @Test
    public void shouldThrowExceptionWhenReadyOrderInexistent() {
        Assertions.assertThatThrownBy(
                        () -> orderManagementApplicationService.markAsReady(1L))
                .isInstanceOf(OrderNotFoundException.class);
    }

    @Test
    public void shouldThrowExceptionWhenTryChangeInvalidStatusReady() {
        var customer = CustomerTestDataBuilder.existingCustomer().build();
        customers.add(customer);

        var order = OrderTestDataBuilder.anOrder()
                .customerId(customer.id())
                .orderStatusEnum(OrderStatusEnum.PLACED)
                .build();

        orders.add(order);

        var orderId = order.id();

        Assertions.assertThatThrownBy(
                        () -> orderManagementApplicationService.markAsReady(orderId.value().toLong()))
                .isInstanceOf(OrderStatusCannotBeChangedException.class);
    }
}