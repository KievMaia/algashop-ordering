package com.algaworks.algashop.ordering.application.shoppingcart.query;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class ShoppingCartOutputTestDataBuilder {

    private ShoppingCartOutputTestDataBuilder() {
    }

    public static ShoppingCartOutput existing() {
        var shoppingCart = new ShoppingCartOutput();
        shoppingCart.setId(UUID.fromString("277297bf-e586-4389-9f21-b3ce0c3f6580"));
        shoppingCart.setCustomerId(UUID.fromString("6e148bd5-47f6-4022-b9da-07cfaa29f7aa"));
        shoppingCart.setTotalItems(2);
        shoppingCart.setTotalAmount(new BigDecimal("41.98"));
        shoppingCart.setItems(new ArrayList<>(List.of(existingItem())));
        return shoppingCart;
    }

    public static ShoppingCartItemOutput existingItem() {
        var item = new ShoppingCartItemOutput();
        item.setId(UUID.fromString("177297bf-e586-4389-9f21-a3ce0c3f6580"));
        item.setProductId(UUID.fromString("6a6ca34c-e65c-496e-adaf-f872e1784003"));
        item.setName("Desktop Gamer Dive");
        item.setPrice(new BigDecimal("20.99"));
        item.setQuantity(2);
        item.setTotalAmount(new BigDecimal("41.98"));
        item.setAvailable(true);
        return item;
    }
}
