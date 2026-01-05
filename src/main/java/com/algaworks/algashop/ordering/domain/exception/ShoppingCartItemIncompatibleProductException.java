package com.algaworks.algashop.ordering.domain.exception;

import com.algaworks.algashop.ordering.domain.valueobject.id.ProductId;

import static com.algaworks.algashop.ordering.domain.exception.ErrorMessages.ERROR_REFRESH_INCOMPATIBLE_PRODUCT;

public class ShoppingCartItemIncompatibleProductException extends DomainException {
    private ShoppingCartItemIncompatibleProductException(String message) {
        super(message);
    }

    public static ShoppingCartItemIncompatibleProductException incompatibleProduct(ProductId productId) {
        return new ShoppingCartItemIncompatibleProductException(
                String.format(ERROR_REFRESH_INCOMPATIBLE_PRODUCT, productId));
    }
}
