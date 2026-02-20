package com.algaworks.algashop.ordering.domain.model.service;

import com.algaworks.algashop.ordering.domain.model.entity.ShoppingCart;
import com.algaworks.algashop.ordering.domain.model.entity.customer.CustomerTestDataBuilder;
import com.algaworks.algashop.ordering.domain.model.entity.shoppingcart.ShoppingCartTestDataBuilder;
import com.algaworks.algashop.ordering.domain.model.exception.CustomerAlreadyHaveShoppingCartException;
import com.algaworks.algashop.ordering.domain.model.exception.CustomerNotFoundException;
import com.algaworks.algashop.ordering.domain.model.repository.Customers;
import com.algaworks.algashop.ordering.domain.model.repository.ShoppingCarts;
import com.algaworks.algashop.ordering.domain.model.valueobject.Money;
import com.algaworks.algashop.ordering.domain.model.valueobject.Quantity;
import com.algaworks.algashop.ordering.domain.model.valueobject.id.CustomerId;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ShoppingServiceTest {

    @Mock
    private Customers customers;

    @Mock
    private ShoppingCarts shoppingCarts;

    @InjectMocks
    private ShoppingService shoppingService;

    @Test
    void startShoppingSuccessfully() {
        final var customerId = givenNewCustomer();

        whenCustomerFound(customerId);
        whenCustomerNotHaveShoppingCart(customerId);

        final var shoppingCart = whenStartShopping(customerId);

        thenShoppingCartWasCreated(shoppingCart, customerId);
        thenMethodsWasCalled(customerId);
    }

    @Test
    void givenNonExistingCustomer_whenStartShopping_shouldThrowCustomerNotFoundException() {
        var customerId = new CustomerId();

        when(customers.exists(customerId)).thenReturn(false);

        assertThatExceptionOfType(CustomerNotFoundException.class)
                .isThrownBy(() -> shoppingService.startShopping(customerId));

        verify(customers).exists(customerId);
        verify(shoppingCarts, never()).ofCustomer(any());
    }

    @Test
    void givenExistingCustomerAndExistingShoppingCart_whenStartShopping_shouldThrowCustomerAlreadyHaveShoppingCartException() {
        var customerId = CustomerTestDataBuilder.DEFAULT_CUSTOMER_ID;
        var existingCart = ShoppingCartTestDataBuilder.aShoppingCart().customerId(customerId).build();

        when(customers.exists(customerId)).thenReturn(true);
        when(shoppingCarts.ofCustomer(customerId)).thenReturn(Optional.of(existingCart));

        assertThatExceptionOfType(CustomerAlreadyHaveShoppingCartException.class)
                .isThrownBy(() -> shoppingService.startShopping(customerId));

        verify(customers).exists(customerId);
        verify(shoppingCarts).ofCustomer(customerId);
    }

    private void thenMethodsWasCalled(final CustomerId customerId) {
        verify(customers).exists(customerId);
        verify(shoppingCarts).ofCustomer(customerId);
    }

    private void thenShoppingCartWasCreated(final ShoppingCart shoppingCart, final CustomerId customerId) {
        Assertions.assertThat(shoppingCart).isNotNull();
        Assertions.assertThat(shoppingCart.customerId()).isEqualTo(customerId);
        Assertions.assertThat(shoppingCart.isEmpty()).isTrue();
        Assertions.assertThat(shoppingCart.totalAmount()).isEqualTo(Money.ZERO);
        Assertions.assertThat(shoppingCart.totalItems()).isEqualTo(Quantity.ZERO);
        Assertions.assertThat(shoppingCart.isEmpty()).isTrue();
    }

    private ShoppingCart whenStartShopping(final CustomerId customerId) {
        return shoppingService.startShopping(customerId);
    }

    private CustomerId givenNewCustomer() {
        return CustomerTestDataBuilder.DEFAULT_CUSTOMER_ID;
    }

    private void whenCustomerNotHaveShoppingCart(final CustomerId customerId) {
        when(shoppingCarts.ofCustomer(customerId)).thenReturn(Optional.empty());
    }

    private void whenCustomerFound(final CustomerId customerId) {
        when(customers.exists(customerId)).thenReturn(true);
    }
}