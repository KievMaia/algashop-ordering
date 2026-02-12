package com.algaworks.algashop.ordering.domain.model.service;

import com.algaworks.algashop.ordering.domain.model.entity.OrderStatusEnum;
import com.algaworks.algashop.ordering.domain.model.entity.customer.CustomerTestDataBuilder;
import com.algaworks.algashop.ordering.domain.model.entity.order.OrderTestDataBuilder;
import com.algaworks.algashop.ordering.domain.model.entity.product.ProductTestDataBuilder;
import com.algaworks.algashop.ordering.domain.model.valueobject.LoyaltyPoints;
import com.algaworks.algashop.ordering.domain.model.valueobject.Quantity;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

class CustomerLoyaltyPointServiceTest {

    CustomerLoyaltyPointService customerLoyaltyPointService = new CustomerLoyaltyPointService();

    @Test
    public void givenValidCustomerAndOrder_WhenAddingPoints_ShouldAccumulate() {
        var customer = CustomerTestDataBuilder.existingCustomer().build();

        var order = OrderTestDataBuilder.anOrder().orderStatusEnum(OrderStatusEnum.READY).build();

        customerLoyaltyPointService.addPoints(customer, order);

        Assertions.assertThat(customer.loyaltyPoints()).isEqualTo(new LoyaltyPoints(30));
    }

    @Test
    public void givenValidCustomerAndOrderWithLowTotalAmount_WhenAddingPoints_ShouldNotAccumulate() {
        var customer = CustomerTestDataBuilder.existingCustomer().build();

        var product = ProductTestDataBuilder.aProductAltRamMemory().build();

        var order = OrderTestDataBuilder.anOrder().withItems(false).orderStatusEnum(OrderStatusEnum.DRAFT).build();
        order.addItem(product, new Quantity(1));
        order.place();
        order.markAsPaid();
        order.markAsReady();

        customerLoyaltyPointService.addPoints(customer, order);

        Assertions.assertThat(customer.loyaltyPoints()).isEqualTo(new LoyaltyPoints(0));
    }
}