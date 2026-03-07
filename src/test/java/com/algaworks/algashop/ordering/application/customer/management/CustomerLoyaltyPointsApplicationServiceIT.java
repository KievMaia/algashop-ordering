package com.algaworks.algashop.ordering.application.customer.management;

import com.algaworks.algashop.ordering.domain.model.commons.Quantity;
import com.algaworks.algashop.ordering.domain.model.customer.*;
import com.algaworks.algashop.ordering.domain.model.order.*;
import com.algaworks.algashop.ordering.domain.model.product.ProductTestDataBuilder;
import io.hypersistence.tsid.TSID;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

import static com.algaworks.algashop.ordering.application.customer.management.CustomerInputTestDateBuilder.aCustomer;
import static com.algaworks.algashop.ordering.domain.model.order.OrderStatusEnum.READY;

@SpringBootTest
@Transactional
class CustomerLoyaltyPointsApplicationServiceIT {

    @Autowired
    private CustomerLoyaltyPointsApplicationService customerLoyaltyPointsApplicationService;

    @Autowired
    private CustomerManagementApplicationService customerManagementApplicationService;

    @Autowired
    private Orders orders;

    @Test
    public void shouldAddPointsSuccessfully() {
        var customer = aCustomer().build();

        var customerId = customerManagementApplicationService.create(customer);

        var ordered =
                OrderTestDataBuilder.anOrder()
                        .customerId(new CustomerId(customerId))
                        .orderStatusEnum(READY)
                        .build();

        orders.add(ordered);

        customerLoyaltyPointsApplicationService.addLoyaltyPoints(customerId, ordered.id().value());

        var customerOutput = customerManagementApplicationService.findById(customerId);

        Assertions.assertThat(customerOutput.getLoyaltyPoints()).isEqualTo(30);
    }

    @Test
    public void shouldThrowExceptionWhenAddPointsToInexistentCustomer() {
        var customer = aCustomer().build();

        var customerId = customerManagementApplicationService.create(customer);
        var ordered =
                OrderTestDataBuilder.anOrder()
                        .customerId(new CustomerId(customerId))
                        .orderStatusEnum(READY)
                        .build();

        orders.add(ordered);

        Assertions.assertThatThrownBy(
                        () -> customerLoyaltyPointsApplicationService.addLoyaltyPoints(
                                UUID.randomUUID(),
                                ordered.id().value()))
                .isInstanceOf(CustomerNotFoundException.class);
    }

    @Test
    public void shouldThrowExceptionWhenAddPointsToInexistentOrder() {
        var customer = aCustomer().build();

        var customerId = customerManagementApplicationService.create(customer);

        Assertions.assertThatThrownBy(
                        () -> customerLoyaltyPointsApplicationService.addLoyaltyPoints(
                                customerId,
                                TSID.fast()))
                .isInstanceOf(OrderNotFoundException.class);
    }

    @Test
    public void shouldThrowExceptionWhenAddPointsToArchivedCustomer() {
        var customer = aCustomer().build();

        var customerId = customerManagementApplicationService.create(customer);

        customerManagementApplicationService.archive(customerId);

        var ordered =
                OrderTestDataBuilder.anOrder()
                        .customerId(new CustomerId(customerId))
                        .orderStatusEnum(READY)
                        .build();

        orders.add(ordered);

        Assertions.assertThatThrownBy(
                        () -> customerLoyaltyPointsApplicationService.addLoyaltyPoints(
                                customerId,
                                ordered.id().value()))
                .isInstanceOf(CustomerArchivedException.class);
    }

    @Test
    public void shouldThrowExceptionWhenAddPointsToAnotherOrderCustomer() {
        var customer1 = aCustomer().build();
        var customer2 = aCustomer().email("clientb@email.com").build();

        var customerId1 = customerManagementApplicationService.create(customer1);
        var customerId2 = customerManagementApplicationService.create(customer2);

        var ordered =
                OrderTestDataBuilder.anOrder()
                        .customerId(new CustomerId(customerId2))
                        .orderStatusEnum(READY)
                        .build();

        orders.add(ordered);

        Assertions.assertThatThrownBy(
                        () -> customerLoyaltyPointsApplicationService.addLoyaltyPoints(
                                customerId1,
                                ordered.id().value()))
                .isInstanceOf(OrderNotBelongsToCustomerException.class);
    }

    @Test
    public void shouldThrowExceptionWhenAddPointsToNotReadyOrder() {
        var customer = aCustomer().build();

        var customerId = customerManagementApplicationService.create(customer);

        var ordered =
                OrderTestDataBuilder.anOrder()
                        .customerId(new CustomerId(customerId))
                        .orderStatusEnum(OrderStatusEnum.PLACED)
                        .build();

        orders.add(ordered);

        Assertions.assertThatThrownBy(
                        () -> customerLoyaltyPointsApplicationService.addLoyaltyPoints(
                                customerId,
                                ordered.id().value()))
                .isInstanceOf(CantAddLoyaltyPointsOrderIsNotReady.class);
    }

    @Test
    public void shouldTryAddPointsUnderLimit() {
        var customer = aCustomer().build();

        var customerId = customerManagementApplicationService.create(customer);

        var product = ProductTestDataBuilder.aProductAltRamMemory().build();

        var order = OrderTestDataBuilder.anOrder().customerId(new CustomerId(customerId)).withItems(false).orderStatusEnum(OrderStatusEnum.DRAFT).build();
        order.addItem(product, new Quantity(1));
        order.place();
        order.markAsPaid();
        order.markAsReady();

        orders.add(order);

        customerLoyaltyPointsApplicationService.addLoyaltyPoints(customerId, order.id().value());

        var customerOutput = customerManagementApplicationService.findById(customerId);

        Assertions.assertThat(customerOutput.getLoyaltyPoints()).isEqualTo(LoyaltyPoints.ZERO.value());
    }
}