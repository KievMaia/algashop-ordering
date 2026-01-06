package com.algaworks.algashop.ordering.domain.entity.shoppingcart;

import com.algaworks.algashop.ordering.domain.entity.ShoppingCart;
import com.algaworks.algashop.ordering.domain.entity.ShoppingCartItem;
import com.algaworks.algashop.ordering.domain.entity.product.ProductTestDataBuilder;
import com.algaworks.algashop.ordering.domain.exception.ShoppingCartItemIncompatibleProductException;
import com.algaworks.algashop.ordering.domain.valueobject.Product;
import com.algaworks.algashop.ordering.domain.valueobject.Quantity;
import com.algaworks.algashop.ordering.domain.valueobject.id.CustomerId;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

public class ShoppingCartItemTest {

    @Test
    public void shouldGenerateEmptyShoppingCartItem() {
        var customerId = new CustomerId();
        var shoppingCart = ShoppingCart.startShopping(customerId);
        var product = ProductTestDataBuilder.aProductAltMousePad().build();
        var shoppingCartItem = ShoppingCartItem.brandNew()
                .shoppingCartId(shoppingCart.id())
                .product(product)
                .quantity(new Quantity(1))
                .build();

        Assertions.assertWith(shoppingCartItem,
                sci -> Assertions.assertThat(sci.id()).isNotNull(),
                sci -> Assertions.assertThat(sci.productId()).isEqualTo(product.id()),
                sci -> Assertions.assertThat(sci.shoppingCartId()).isEqualTo(shoppingCart.id()),
                sc -> Assertions.assertThat(shoppingCartItem.isAvailable()).isTrue()
        );
    }

    @Test
    public void shouldThrowExceptionWhenUpdateQuantityToZero() {
        var shoppingCartItem = ShoppingCartItemTestDataBuilder.aShoppingCartItem().build();
        var invalidQuantity = Quantity.ZERO;

        Assertions.assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() -> shoppingCartItem.changeQuantity(invalidQuantity));
    }

    @Test
    void givenShoppingCartItem_whenTryToRefreshWithDifferentProductIds_shouldThrowException() {
        var customerId = new CustomerId();
        var shoppingCart = ShoppingCart.startShopping(customerId);
        var product = ProductTestDataBuilder.aProductAltMousePad().build();
        var shoppingCartItem = ShoppingCartItem.brandNew()
                .shoppingCartId(shoppingCart.id())
                .product(product)
                .quantity(new Quantity(1))
                .build();
        Product productWithDifferentId = ProductTestDataBuilder.aProduct().build();

        Assertions.assertThatThrownBy(() -> shoppingCartItem.refresh(productWithDifferentId))
                .isInstanceOf(ShoppingCartItemIncompatibleProductException.class);
    }
}
