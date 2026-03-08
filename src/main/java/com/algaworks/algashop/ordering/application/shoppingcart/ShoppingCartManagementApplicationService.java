package com.algaworks.algashop.ordering.application.shoppingcart;

import com.algaworks.algashop.ordering.domain.model.commons.Quantity;
import com.algaworks.algashop.ordering.domain.model.customer.CustomerId;
import com.algaworks.algashop.ordering.domain.model.product.ProductCatalogService;
import com.algaworks.algashop.ordering.domain.model.product.ProductId;
import com.algaworks.algashop.ordering.domain.model.product.ProductNotFoundException;
import com.algaworks.algashop.ordering.domain.model.shoppingcart.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ShoppingCartManagementApplicationService {

    private final ShoppingCarts shoppingCarts;

    private final ProductCatalogService productCatalogService;
    private final ShoppingService shoppingService;

    public void addItem(ShoppingCartItemInput input) {
        final var shoppingCart = this.foundById(input.getShoppingCartId());

        var product = productCatalogService.ofId(new ProductId(input.getProductId()))
                .orElseThrow(ProductNotFoundException::new);

        shoppingCart.addItem(product, new Quantity(input.getQuantity()));

        shoppingCarts.add(shoppingCart);
    }

    public ShoppingCartId createNew(UUID rawCustomerId) {
        var shoppingCart = shoppingService.startShopping(new CustomerId(rawCustomerId));

        shoppingCarts.add(shoppingCart);

        return shoppingCart.id();
    }

    public void removeItem(UUID rawShoppingCartId, UUID rawShoppingCartItemId) {
        var shoppingCart = this.foundById(rawShoppingCartId);

        shoppingCart.removeItem(new ShoppingCartItemId(rawShoppingCartItemId));

        shoppingCarts.add(shoppingCart);
    }

    public void empty(UUID rawShoppingCartId) {
        var shoppingCart = this.foundById(rawShoppingCartId);

        shoppingCart.empty();

        shoppingCarts.add(shoppingCart);
    }

    public void delete(UUID rawShoppingCart) {
        var shoppingCart = this.foundById(rawShoppingCart);
        shoppingCarts.remove(new ShoppingCartId(shoppingCart.id().value()));
    }

    public void delete(ShoppingCart shoppingCart) {
        shoppingCarts.remove(shoppingCart);
    }

    private ShoppingCart foundById(final UUID rawShoppingCartId) {
        return shoppingCarts.ofId(new ShoppingCartId(rawShoppingCartId))
                .orElseThrow(ShoppingCartNotFoundException::new);
    }
}
