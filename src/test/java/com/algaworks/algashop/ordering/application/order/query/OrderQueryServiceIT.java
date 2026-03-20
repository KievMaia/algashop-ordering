package com.algaworks.algashop.ordering.application.order.query;

import com.algaworks.algashop.ordering.domain.model.customer.CustomerId;
import com.algaworks.algashop.ordering.domain.model.customer.CustomerTestDataBuilder;
import com.algaworks.algashop.ordering.domain.model.customer.Customers;
import com.algaworks.algashop.ordering.domain.model.order.OrderTestDataBuilder;
import com.algaworks.algashop.ordering.domain.model.order.Orders;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static com.algaworks.algashop.ordering.domain.model.order.OrderStatusEnum.*;

@SpringBootTest
@Transactional
class OrderQueryServiceIT {

    @Autowired
    private OrderQueryService orderQueryService;

    @Autowired
    private Orders orders;

    @Autowired
    private Customers customers;

    @Test
    public void shouldFindById() {
        var customer = CustomerTestDataBuilder.existingCustomer().build();
        customers.add(customer);

        var order = OrderTestDataBuilder.anOrder().customerId(customer.id()).build();
        orders.add(order);

        var output = orderQueryService.findById(order.id().toString());

        Assertions.assertThat(output).extracting(
                OrderDetailOutput::getId,
                OrderDetailOutput::getTotalAmount
        ).containsExactly(
                order.id().toString(),
                order.totalAmount().value()
        );
    }

    @Test
    public void shouldFilterByPage() {
        var customer = CustomerTestDataBuilder.existingCustomer().build();
        customers.add(customer);

        var order = OrderTestDataBuilder.anOrder().orderStatusEnum(DRAFT).withItems(false).customerId(customer.id()).build();
        var order2 = OrderTestDataBuilder.anOrder().orderStatusEnum(PLACED).withItems(false).customerId(customer.id()).build();
        var order3 = OrderTestDataBuilder.anOrder().orderStatusEnum(PAID).withItems(false).customerId(customer.id()).build();
        var order4 = OrderTestDataBuilder.anOrder().orderStatusEnum(READY).withItems(false).customerId(customer.id()).build();
        var order5 = OrderTestDataBuilder.anOrder().orderStatusEnum(CANCELED).withItems(false).customerId(customer.id()).build();
        orders.add(order);
        orders.add(order2);
        orders.add(order3);
        orders.add(order4);
        orders.add(order5);

        var page = orderQueryService.filter(new OrderFilter(3,0));

        Assertions.assertThat(page.getTotalPages()).isEqualTo(2);
        Assertions.assertThat(page.getTotalElements()).isEqualTo(5);
        Assertions.assertThat(page.getNumberOfElements()).isEqualTo(3);
    }

    @Test
    public void shouldFilterByCustomerId() {
        var customer1 = CustomerTestDataBuilder.existingCustomer().build();
        customers.add(customer1);
        var order = OrderTestDataBuilder.anOrder().orderStatusEnum(DRAFT).withItems(false).customerId(customer1.id()).build();
        var order2 = OrderTestDataBuilder.anOrder().orderStatusEnum(PLACED).withItems(false).customerId(customer1.id()).build();
        orders.add(order);
        orders.add(order2);

        var customer2 = CustomerTestDataBuilder.existingCustomer().id(new CustomerId()).build();
        customers.add(customer2);
        var order3 = OrderTestDataBuilder.anOrder().orderStatusEnum(PAID).withItems(false).customerId(customer2.id()).build();
        var order4 = OrderTestDataBuilder.anOrder().orderStatusEnum(READY).withItems(false).customerId(customer2.id()).build();
        var order5 = OrderTestDataBuilder.anOrder().orderStatusEnum(CANCELED).withItems(false).customerId(customer2.id()).build();
        orders.add(order3);
        orders.add(order4);
        orders.add(order5);

        var filter = new OrderFilter();
        filter.setCustomerId(customer1.id().value());

        var page = orderQueryService.filter(filter);

        Assertions.assertThat(page.getTotalPages()).isEqualTo(1);
        Assertions.assertThat(page.getTotalElements()).isEqualTo(2);
        Assertions.assertThat(page.getNumberOfElements()).isEqualTo(2);
    }
}