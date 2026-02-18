package com.algaworks.algashop.ordering.domain.model.service;

import com.algaworks.algashop.ordering.domain.model.entity.Order;
import com.algaworks.algashop.ordering.domain.model.entity.PaymentMethodEnum;
import com.algaworks.algashop.ordering.domain.model.entity.ShoppingCart;
import com.algaworks.algashop.ordering.domain.model.exception.ShoppingCartCantProceedToCheckoutException;
import com.algaworks.algashop.ordering.domain.model.utility.DomainService;
import com.algaworks.algashop.ordering.domain.model.valueobject.Billing;
import com.algaworks.algashop.ordering.domain.model.valueobject.Money;
import com.algaworks.algashop.ordering.domain.model.valueobject.Product;
import com.algaworks.algashop.ordering.domain.model.valueobject.Shipping;
import com.algaworks.algashop.ordering.domain.model.valueobject.id.ProductId;
import lombok.RequiredArgsConstructor;

@DomainService
@RequiredArgsConstructor
public class CheckoutService {

    public Order checkout(
            ShoppingCart shoppingCart,
            Billing billing,
            Shipping shipping,
            PaymentMethodEnum paymentMethodEnum
    ) {
        verifyUnavailableItemsOrEmptyShoppingCart(shoppingCart);

        var customerId = shoppingCart.customerId();
        var order = Order.draft(customerId);

        order.changeBilling(billing);
        order.changeShipping(shipping);
        order.changePaymentMethod(paymentMethodEnum);

        addItemsToOrder(shoppingCart, order);

        order.place();
        shoppingCart.empty();

        return order;
    }

    private void addItemsToOrder(ShoppingCart shoppingCart, Order order) {
        shoppingCart.items().forEach(item -> {
            order.addItem(Product.builder()
                            .id(new ProductId())
                            .name(item.name())
                            .price(new Money(item.price().value()))
                            .inStock(item.isAvailable())
                    .build(),
                    item.quantity());
        });
    }

    private void verifyUnavailableItemsOrEmptyShoppingCart(ShoppingCart shoppingCart) {
        if (shoppingCart.isEmpty() || shoppingCart.containsUnavailableItems()) {
            throw new ShoppingCartCantProceedToCheckoutException();
        }
    }
}
