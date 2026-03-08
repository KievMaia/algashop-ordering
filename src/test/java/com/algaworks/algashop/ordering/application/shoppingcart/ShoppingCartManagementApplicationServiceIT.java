package com.algaworks.algashop.ordering.application.shoppingcart;

import com.algaworks.algashop.ordering.application.customer.management.CustomerManagementApplicationService;
import com.algaworks.algashop.ordering.domain.model.customer.CustomerAlreadyHaveShoppingCartException;
import com.algaworks.algashop.ordering.domain.model.customer.CustomerNotFoundException;
import com.algaworks.algashop.ordering.domain.model.product.ProductCatalogService;
import com.algaworks.algashop.ordering.domain.model.product.ProductNotFoundException;
import com.algaworks.algashop.ordering.domain.model.product.ProductOutOfStockException;
import com.algaworks.algashop.ordering.domain.model.product.ProductTestDataBuilder;
import com.algaworks.algashop.ordering.domain.model.shoppingcart.ShoppingCartDoesNotContainItemException;
import com.algaworks.algashop.ordering.domain.model.shoppingcart.ShoppingCarts;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

import static com.algaworks.algashop.ordering.application.customer.management.CustomerInputTestDateBuilder.aCustomer;

@SpringBootTest
@Transactional
class ShoppingCartManagementApplicationServiceIT {

    @Autowired
    private ShoppingCartManagementApplicationService shoppingCartManagementApplicationService;

    @Autowired
    private CustomerManagementApplicationService customerManagementApplicationService;

    @MockitoBean
    private ProductCatalogService productCatalogService;

    @Autowired
    private ShoppingCarts shoppingCarts;

    @Test
    public void shouldAddItemSuccessfully() {
        var customer = aCustomer().build();

        var customerId = customerManagementApplicationService.create(customer);

        var shoppingCartId = shoppingCartManagementApplicationService.createNew(customerId);

        var product = ProductTestDataBuilder.aProduct().build();
        var productId = product.id();

        var shoppingCartItemInput = ShoppingCartItemInput.builder()
                .quantity(1)
                .shoppingCartId(shoppingCartId.value())
                .productId(productId.value())
                .build();

        Mockito.when(productCatalogService.ofId(Mockito.any()))
                .thenReturn(Optional.of(product));

        shoppingCartManagementApplicationService.addItem(shoppingCartItemInput);

        var shoppingCart = shoppingCarts.ofId(shoppingCartId).get();

        Assertions.assertThat(shoppingCart.items().stream().findFirst()).isPresent();
    }

    @Test
    public void shouldThrowExceptionWhenAddItemThenNotFoundShoppingCart() {
        var product = ProductTestDataBuilder.aProduct().build();
        var productId = product.id();

        var shoppingCartItemInput = ShoppingCartItemInput.builder()
                .quantity(1)
                .shoppingCartId(UUID.randomUUID())
                .productId(productId.value())
                .build();

        Assertions.assertThatThrownBy(
                        () -> shoppingCartManagementApplicationService.addItem(shoppingCartItemInput))
                .isInstanceOf(ShoppingCartNotFoundException.class);
    }

    @Test
    public void shouldThrowExceptionWhenProductNotFound() {
        var customer = aCustomer().build();

        var customerId = customerManagementApplicationService.create(customer);

        var shoppingCartId = shoppingCartManagementApplicationService.createNew(customerId);

        var shoppingCartItemInput = ShoppingCartItemInput.builder()
                .quantity(1)
                .shoppingCartId(shoppingCartId.value())
                .productId(UUID.randomUUID())
                .build();

        Mockito.when(productCatalogService.ofId(Mockito.any()))
                .thenReturn(Optional.empty());

        Assertions.assertThatThrownBy(
                () -> shoppingCartManagementApplicationService.addItem(shoppingCartItemInput)
        ).isInstanceOf(ProductNotFoundException.class);
    }

    @Test
    public void shouldThrowExceptionWhenProductOutOfStock() {
        var customer = aCustomer().build();

        var customerId = customerManagementApplicationService.create(customer);

        var shoppingCartId = shoppingCartManagementApplicationService.createNew(customerId);

        var product = ProductTestDataBuilder.aProduct().inStock(false).build();

        Mockito.when(productCatalogService.ofId(Mockito.any()))
                .thenReturn(Optional.of(product));

        var shoppingCartItemInput = ShoppingCartItemInput.builder()
                .quantity(1)
                .shoppingCartId(shoppingCartId.value())
                .productId(product.id().value())
                .build();

        Assertions.assertThatThrownBy(
                () -> shoppingCartManagementApplicationService.addItem(shoppingCartItemInput)
        ).isInstanceOf(ProductOutOfStockException.class);
    }

    @Test
    public void shouldCreateNewShoppingCartSuccessfully() {
        var customer = aCustomer().build();

        var customerId = customerManagementApplicationService.create(customer);

        var shoppingCartId = shoppingCartManagementApplicationService.createNew(customerId);

        var shoppingCart = shoppingCarts.ofId(shoppingCartId).get();

        Assertions.assertThat(shoppingCartId).isEqualTo(shoppingCart.id());
    }

    @Test
    public void shouldThrowExceptionWhenTryCreateNewShoppingCartClientNotFound() {
        Assertions.assertThatThrownBy(
                () -> shoppingCartManagementApplicationService.createNew(UUID.randomUUID())
        ).isInstanceOf(CustomerNotFoundException.class);
    }

    @Test
    public void shouldThrowExceptionWhenTryCreateNewShoppingCartForCustomerThatAlreadyHaveAShoppingCart() {
        var customer = aCustomer().build();

        var customerId = customerManagementApplicationService.create(customer);

        shoppingCartManagementApplicationService.createNew(customerId);

        Assertions.assertThatThrownBy(
                () -> shoppingCartManagementApplicationService.createNew(customerId)
        ).isInstanceOf(CustomerAlreadyHaveShoppingCartException.class);
    }

    @Test
    public void shouldRemoveItemSuccessfully() {
        var customer = aCustomer().build();

        var customerId = customerManagementApplicationService.create(customer);

        var shoppingCartId = shoppingCartManagementApplicationService.createNew(customerId);

        var product = ProductTestDataBuilder.aProduct().build();
        var productId = product.id();

        Mockito.when(productCatalogService.ofId(Mockito.any()))
                .thenReturn(Optional.of(product));

        var shoppingCartItemInput = ShoppingCartItemInput.builder()
                .quantity(1)
                .shoppingCartId(shoppingCartId.value())
                .productId(productId.value())
                .build();

        shoppingCartManagementApplicationService.addItem(shoppingCartItemInput);

        var shoppingCart = shoppingCarts.ofId(shoppingCartId).get();
        var shoppingCartItem = shoppingCart.items().stream().findFirst().get();

        shoppingCartManagementApplicationService.removeItem(
                shoppingCartId.value(),
                shoppingCartItem.id().value()
        );

        var updatedShoppingCart = shoppingCarts.ofId(shoppingCartId).get();

        Assertions.assertThat(updatedShoppingCart.items()).isEmpty();
    }

    @Test
    public void shouldThrowExceptionWhenTryRemoveItemOfInexistentShoppingcart() {
        var customer = aCustomer().build();

        var customerId = customerManagementApplicationService.create(customer);

        var shoppingCartId = shoppingCartManagementApplicationService.createNew(customerId);

        var product = ProductTestDataBuilder.aProduct().build();
        var productId = product.id();

        Mockito.when(productCatalogService.ofId(Mockito.any()))
                .thenReturn(Optional.of(product));

        var shoppingCartItemInput = ShoppingCartItemInput.builder()
                .quantity(1)
                .shoppingCartId(shoppingCartId.value())
                .productId(productId.value())
                .build();

        shoppingCartManagementApplicationService.addItem(shoppingCartItemInput);

        var shoppingCart = shoppingCarts.ofId(shoppingCartId).get();
        var shoppingCartItem = shoppingCart.items().stream().findFirst().get();

        Assertions.assertThatThrownBy(
                () -> shoppingCartManagementApplicationService.removeItem(
                        UUID.randomUUID(),
                        shoppingCartItem.id().value()
                )
        ).isInstanceOf(ShoppingCartNotFoundException.class);
    }

    @Test
    public void shouldThrowExceptionWhenTryRemoveInexistentItem() {
        var customer = aCustomer().build();

        var customerId = customerManagementApplicationService.create(customer);

        var shoppingCartId = shoppingCartManagementApplicationService.createNew(customerId);

        var product = ProductTestDataBuilder.aProduct().build();
        var productId = product.id();

        Mockito.when(productCatalogService.ofId(Mockito.any()))
                .thenReturn(Optional.of(product));

        var shoppingCartItemInput = ShoppingCartItemInput.builder()
                .quantity(1)
                .shoppingCartId(shoppingCartId.value())
                .productId(productId.value())
                .build();

        shoppingCartManagementApplicationService.addItem(shoppingCartItemInput);

        Assertions.assertThatThrownBy(
                () -> shoppingCartManagementApplicationService.removeItem(
                        shoppingCartId.value(),
                        UUID.randomUUID()
                )
        ).isInstanceOf(ShoppingCartDoesNotContainItemException.class);
    }

    @Test
    public void shouldEmptyShoppingCartSuccessfully() {
        var customer = aCustomer().build();

        var customerId = customerManagementApplicationService.create(customer);

        var shoppingCartId = shoppingCartManagementApplicationService.createNew(customerId);

        var product = ProductTestDataBuilder.aProduct().build();
        var productId = product.id();

        var shoppingCartItemInput = ShoppingCartItemInput.builder()
                .quantity(1)
                .shoppingCartId(shoppingCartId.value())
                .productId(productId.value())
                .build();

        Mockito.when(productCatalogService.ofId(Mockito.any()))
                .thenReturn(Optional.of(product));

        shoppingCartManagementApplicationService.addItem(shoppingCartItemInput);

        shoppingCartManagementApplicationService.empty(shoppingCartId.value());

        var shoppingCart = shoppingCarts.ofId(shoppingCartId).get();

        Assertions.assertThat(shoppingCart.items()).isEmpty();
    }

    @Test
    public void shouldThrowExceptionWhenTryEmptyInexistentShoppingCart() {
        var customer = aCustomer().build();

        var customerId = customerManagementApplicationService.create(customer);

        var shoppingCartId = shoppingCartManagementApplicationService.createNew(customerId);

        var product = ProductTestDataBuilder.aProduct().build();
        var productId = product.id();

        var shoppingCartItemInput = ShoppingCartItemInput.builder()
                .quantity(1)
                .shoppingCartId(shoppingCartId.value())
                .productId(productId.value())
                .build();

        Mockito.when(productCatalogService.ofId(Mockito.any()))
                .thenReturn(Optional.of(product));

        shoppingCartManagementApplicationService.addItem(shoppingCartItemInput);

        Assertions.assertThatThrownBy(
                () -> shoppingCartManagementApplicationService.empty(UUID.randomUUID())
        ).isInstanceOf(ShoppingCartNotFoundException.class);
    }

    @Test
    public void shouldDeleteShoppingCartSuccessfully() {
        var customer = aCustomer().build();

        var customerId = customerManagementApplicationService.create(customer);

        var shoppingCartId = shoppingCartManagementApplicationService.createNew(customerId);

        shoppingCartManagementApplicationService.delete(shoppingCartId.value());

        Assertions.assertThat(shoppingCarts.ofId(shoppingCartId)).isEmpty();
    }

    @Test
    public void shouldThrowExceptionWhenTryDeleteInexistentShoppingCart() {
        Assertions.assertThatThrownBy(
                () -> shoppingCartManagementApplicationService.delete(UUID.randomUUID())
        ).isInstanceOf(ShoppingCartNotFoundException.class);
    }
}