package com.algaworks.algashop.ordering.domain.model.shoppingcart;

import com.algaworks.algashop.ordering.domain.model.customer.CustomerAlreadyHaveShoppingCartException;
import com.algaworks.algashop.ordering.domain.model.customer.CustomerNotFoundException;
import com.algaworks.algashop.ordering.domain.model.customer.Customers;
import com.algaworks.algashop.ordering.domain.model.DomainService;
import com.algaworks.algashop.ordering.domain.model.customer.CustomerId;
import lombok.RequiredArgsConstructor;

@DomainService
@RequiredArgsConstructor
public class ShoppingService {

    private final Customers customers;

    private final ShoppingCarts shoppingCarts;

    public ShoppingCart startShopping(CustomerId customerId) {
        verifyIfCustomerExists(customerId);
        checkIfTheShoppingCartAlreadyExists(customerId);
        return ShoppingCart.startShopping(customerId);
    }

    private void checkIfTheShoppingCartAlreadyExists(CustomerId customerId) {
        if (shoppingCarts.ofCustomer(customerId).isPresent()) throw new CustomerAlreadyHaveShoppingCartException();
    }

    private void verifyIfCustomerExists(CustomerId customerId) {
        if(!customers.exists(customerId)) throw  new CustomerNotFoundException();
    }
}
