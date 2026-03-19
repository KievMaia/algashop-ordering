package com.algaworks.algashop.ordering.application.order.query;

import com.algaworks.algashop.ordering.application.utility.PageFilter;
import com.algaworks.algashop.ordering.domain.model.customer.Customer;
import com.algaworks.algashop.ordering.domain.model.customer.CustomerTestDataBuilder;
import com.algaworks.algashop.ordering.domain.model.customer.Customers;
import com.algaworks.algashop.ordering.domain.model.order.OrderStatusEnum;
import com.algaworks.algashop.ordering.domain.model.order.OrderTestDataBuilder;
import com.algaworks.algashop.ordering.domain.model.order.Orders;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.transaction.annotation.Transactional;

import static com.algaworks.algashop.ordering.domain.model.order.OrderStatusEnum.CANCELED;
import static com.algaworks.algashop.ordering.domain.model.order.OrderStatusEnum.DRAFT;
import static com.algaworks.algashop.ordering.domain.model.order.OrderStatusEnum.PAID;
import static com.algaworks.algashop.ordering.domain.model.order.OrderStatusEnum.PLACED;
import static com.algaworks.algashop.ordering.domain.model.order.OrderStatusEnum.READY;

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
                order.totalAmount()
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

        var page = orderQueryService.filter(new PageFilter(3, 0));

        Assertions.assertThat(page.getTotalPages()).isEqualTo(2);
        Assertions.assertThat(page.getTotalElements()).isEqualTo(5);
        Assertions.assertThat(page.getNumberOfElements()).isEqualTo(3);
    }
}