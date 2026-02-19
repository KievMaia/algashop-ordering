package com.algaworks.algashop.ordering.domain.model.service;

import com.algaworks.algashop.ordering.domain.model.entity.ShoppingCart;
import com.algaworks.algashop.ordering.domain.model.exception.CustomerAlreadyHaveShoppingCartException;
import com.algaworks.algashop.ordering.domain.model.exception.CustomerNotFoundException;
import com.algaworks.algashop.ordering.domain.model.repository.Customers;
import com.algaworks.algashop.ordering.domain.model.repository.ShoppingCarts;
import com.algaworks.algashop.ordering.domain.model.utility.DomainService;
import com.algaworks.algashop.ordering.domain.model.valueobject.id.CustomerId;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;

@DomainService
@RequiredArgsConstructor
public class ShoppingService {

    @Autowired
    private final Customers customers;

    @Autowired
    private final ShoppingCarts shoppingCarts;

    public ShoppingCart startShopping(CustomerId customerId) {
        verifyIfCustomerExists(customerId);
        checkIfTheShoppingCartAlreadyExists(customerId);
        return ShoppingCart.startShopping(customerId);
    }

    private void checkIfTheShoppingCartAlreadyExists(CustomerId customerId) {
        shoppingCarts.ofCustomer(customerId).orElseThrow(CustomerAlreadyHaveShoppingCartException::new);
    }

    private void verifyIfCustomerExists(CustomerId customerId) {
        customers.ofId(customerId).orElseThrow(CustomerNotFoundException::new);
    }
}
