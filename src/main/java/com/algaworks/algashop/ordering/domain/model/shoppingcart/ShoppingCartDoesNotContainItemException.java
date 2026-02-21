package com.algaworks.algashop.ordering.domain.model.shoppingcart;

import com.algaworks.algashop.ordering.domain.model.DomainException;

import static com.algaworks.algashop.ordering.domain.model.ErrorMessages.ERROR_SHOPPING_ITEM_CART_DOES_NOT_CONTAIN_ITEM;


public class ShoppingCartDoesNotContainItemException extends DomainException {
    private ShoppingCartDoesNotContainItemException(String message) {
        super(message);
    }

    public static ShoppingCartDoesNotContainItemException shoppingCartItemIdDoesNotExist(ShoppingCartItemId shoppingCartItemId) {
        return new ShoppingCartDoesNotContainItemException(
                String.format(ERROR_SHOPPING_ITEM_CART_DOES_NOT_CONTAIN_ITEM, shoppingCartItemId));
    }
}
