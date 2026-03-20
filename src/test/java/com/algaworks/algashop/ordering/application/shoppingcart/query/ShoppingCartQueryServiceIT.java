package com.algaworks.algashop.ordering.application.shoppingcart.query;

import com.algaworks.algashop.ordering.domain.model.customer.CustomerTestDataBuilder;
import com.algaworks.algashop.ordering.domain.model.customer.Customers;
import com.algaworks.algashop.ordering.domain.model.shoppingcart.ShoppingCartTestDataBuilder;
import com.algaworks.algashop.ordering.domain.model.shoppingcart.ShoppingCarts;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Transactional
class ShoppingCartQueryServiceIT {

    @Autowired
    private ShoppingCartQueryService shoppingCartQueryService;

    @Autowired
    private ShoppingCarts shoppingCarts;

    @Autowired
    private Customers customers;

    @Test
    public void shouldFindById() {
        var customer = CustomerTestDataBuilder.existingCustomer().build();
        customers.add(customer);

        var shoppingCart = ShoppingCartTestDataBuilder.aShoppingCart().customerId(customer.id()).withItems(true).build();
        shoppingCarts.add(shoppingCart);

        var shoppingCartId = shoppingCart.id();

        var output = shoppingCartQueryService.findById(shoppingCartId.value());

        Assertions.assertThat(output).extracting(
                ShoppingCartOutput::getId,
                ShoppingCartOutput::getCustomerId
        ).containsExactly(
                output.getId(),
                output.getCustomerId()
        );
    }

    @Test
    public void shouldFindByCustomerId() {
        var customer = CustomerTestDataBuilder.existingCustomer().build();
        customers.add(customer);

        var shoppingCart = ShoppingCartTestDataBuilder.aShoppingCart().customerId(customer.id()).withItems(true).build();
        shoppingCarts.add(shoppingCart);

        var output = shoppingCartQueryService.findByCustomerId(customer.id().value());

        Assertions.assertThat(output).extracting(
                ShoppingCartOutput::getId,
                ShoppingCartOutput::getCustomerId
        ).containsExactly(
                output.getId(),
                output.getCustomerId()
        );
    }
}