package com.algaworks.algashop.ordering.domain.model.shoppingcart;

import com.algaworks.algashop.ordering.domain.model.DomainException;
import com.algaworks.algashop.ordering.domain.model.product.ProductId;

import static com.algaworks.algashop.ordering.domain.model.ErrorMessages.ERROR_REFRESH_INCOMPATIBLE_PRODUCT;

public class ShoppingCartItemIncompatibleProductException extends DomainException {
    private ShoppingCartItemIncompatibleProductException(String message) {
        super(message);
    }

    public static ShoppingCartItemIncompatibleProductException incompatibleProduct(ProductId productId) {
        return new ShoppingCartItemIncompatibleProductException(
                String.format(ERROR_REFRESH_INCOMPATIBLE_PRODUCT, productId));
    }
}
