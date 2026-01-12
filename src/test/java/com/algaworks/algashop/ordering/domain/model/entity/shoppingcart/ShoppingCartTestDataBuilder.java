package com.algaworks.algashop.ordering.domain.model.entity.shoppingcart;

import com.algaworks.algashop.ordering.domain.model.entity.ShoppingCart;
import com.algaworks.algashop.ordering.domain.model.entity.product.ProductTestDataBuilder;
import com.algaworks.algashop.ordering.domain.model.valueobject.Quantity;
import com.algaworks.algashop.ordering.domain.model.valueobject.id.CustomerId;

public class ShoppingCartTestDataBuilder {
    private boolean withItems = true;

    private ShoppingCartTestDataBuilder() {}

    public static ShoppingCartTestDataBuilder aShoppingCart() {
        return new ShoppingCartTestDataBuilder();
    }

    public ShoppingCart build() {
        ShoppingCart shoppingCart = ShoppingCart.startShopping(new CustomerId());

        if (this.withItems) {
            var mousePad = ProductTestDataBuilder.aProductAltMousePad().build();
            var ramMemory = ProductTestDataBuilder.aProductAltRamMemory().build();

            shoppingCart.addItem(mousePad, Quantity.of(1));
            shoppingCart.addItem(ramMemory, Quantity.of(1));
        }

        return shoppingCart;
    }

    public ShoppingCartTestDataBuilder withItems(boolean withItems) {
        this.withItems = withItems;
        return this;
    }
}