package com.algaworks.algashop.ordering.domain.entity.shoppingcart;

import com.algaworks.algashop.ordering.domain.entity.ShoppingCart;
import com.algaworks.algashop.ordering.domain.entity.product.ProductTestDataBuilder;
import com.algaworks.algashop.ordering.domain.exception.ProductOutOfStockException;
import com.algaworks.algashop.ordering.domain.exception.ShoppingCartDoesNotContainItemException;
import com.algaworks.algashop.ordering.domain.exception.ShoppingCartDoesNotContainProductException;
import com.algaworks.algashop.ordering.domain.valueobject.Money;
import com.algaworks.algashop.ordering.domain.valueobject.Quantity;
import com.algaworks.algashop.ordering.domain.valueobject.id.CustomerId;
import com.algaworks.algashop.ordering.domain.valueobject.id.ShoppingCartItemId;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

class ShoppingCartTest {

    @Test
    public void shouldGenerateEmptyShoppingCart() {
        var customerId = new CustomerId();
        var shoppingCart = ShoppingCart.startShopping(customerId);

        Assertions.assertWith(shoppingCart,
                sc -> Assertions.assertThat(sc.id()).isNotNull(),
                sc -> Assertions.assertThat(sc.customerId()).isEqualTo(customerId),
                sc -> Assertions.assertThat(sc.totalAmount()).isEqualTo(Money.ZERO),
                sc -> Assertions.assertThat(sc.totalItems()).isEqualTo(Quantity.ZERO),
                sc -> Assertions.assertThat(sc.createdAt()).isNotNull(),
                sc -> Assertions.assertThat(sc.items()).isEmpty()
                );
    }

    @Test
    public void givenShoppingCartProduct_WhenAddItemOutOfStock_ShouldGenerateException() {
        var customerId = new CustomerId();
        var shoppingCart = ShoppingCart.startShopping(customerId);

        var unavailableProduct = ProductTestDataBuilder.aProductUnavailable().build();

        Assertions.assertThatExceptionOfType(ProductOutOfStockException.class)
                .isThrownBy(() -> shoppingCart.addItem(unavailableProduct, new Quantity(1)));
    }

    @Test
    public void givenTwoShoppingCartProduct_WhenAddTwice_ShouldAddAndUpdatePriceCorrectly() {
        var customerId = new CustomerId();
        var shoppingCart = ShoppingCart.startShopping(customerId);

        var altRamMemory1 = ProductTestDataBuilder.aProductAltRamMemory().build();

        shoppingCart.addItem(altRamMemory1, new Quantity(2));

        Assertions.assertWith(shoppingCart,
                sc -> Assertions.assertThat(sc.totalItems()).isEqualTo(new Quantity(2)),
                sc -> Assertions.assertThat(sc.totalAmount()).isEqualTo(new Money("400"))
                );
    }

    @Test
    public void givenTwoDifferentShoppingCartProduct_WhenAddTwice_ShouldAddAndUpdatePriceCorrectly() {
        var customerId = new CustomerId();
        var shoppingCart = ShoppingCart.startShopping(customerId);

        var altRamMemory = ProductTestDataBuilder.aProductAltRamMemory().build();
        var altMousePad = ProductTestDataBuilder.aProductAltMousePad().build();

        shoppingCart.addItem(altRamMemory, new Quantity(2));
        shoppingCart.addItem(altMousePad, new Quantity(2));

        Assertions.assertWith(shoppingCart,
                sc -> Assertions.assertThat(sc.totalItems()).isEqualTo(new Quantity(4)),
                sc -> Assertions.assertThat(sc.totalAmount()).isEqualTo(new Money("600"))
        );
    }

    @Test
    public void shouldThrowException_WhenRemoveNotExistentItem() {
        var customerId = new CustomerId();
        var shoppingCart = ShoppingCart.startShopping(customerId);

        Assertions.assertThatExceptionOfType(ShoppingCartDoesNotContainItemException.class)
                .isThrownBy(() -> shoppingCart.removeItem(new ShoppingCartItemId()));
    }

    @Test
    public void givenShoppingCartProductItems_WhenRemoveItems_ShouldUpdateAndReturnCorrectlyTotals() {
        var customerId = new CustomerId();
        var shoppingCart = ShoppingCart.startShopping(customerId);

        var altRamMemory = ProductTestDataBuilder.aProductAltRamMemory().build();
        var altMousePad = ProductTestDataBuilder.aProductAltMousePad().build();

        shoppingCart.addItem(altRamMemory, new Quantity(2));
        shoppingCart.addItem(altMousePad, new Quantity(2));

        shoppingCart.empty();

        Assertions.assertWith(shoppingCart,
                sc -> Assertions.assertThat(sc.items()).isEmpty(),
                sc -> Assertions.assertThat(sc.totalAmount()).isEqualTo(Money.ZERO),
                sc -> Assertions.assertThat(sc.isEmpty()).isTrue()
        );
    }

    @Test
    public void givenShoppingCartProduct_WhenRefreshIncompatibleItem_ShouldThrowException() {
        var customerId = new CustomerId();
        var shoppingCart = ShoppingCart.startShopping(customerId);

        var altRamMemory = ProductTestDataBuilder.aProductAltRamMemory().build();
        var altMousePad = ProductTestDataBuilder.aProductAltMousePad().build();

        shoppingCart.addItem(altRamMemory, new Quantity(2));

        Assertions.assertThatExceptionOfType(ShoppingCartDoesNotContainProductException.class)
                .isThrownBy(() -> shoppingCart.refreshItem(altMousePad));
    }
}