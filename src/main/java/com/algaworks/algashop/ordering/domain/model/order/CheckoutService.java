package com.algaworks.algashop.ordering.domain.model.order;

import com.algaworks.algashop.ordering.domain.model.shoppingcart.ShoppingCart;
import com.algaworks.algashop.ordering.domain.model.DomainService;
import com.algaworks.algashop.ordering.domain.model.commons.Money;
import com.algaworks.algashop.ordering.domain.model.product.Product;

@DomainService
public class CheckoutService {

    public Order checkout(
            ShoppingCart shoppingCart,
            Billing billing,
            Shipping shipping,
            PaymentMethodEnum paymentMethodEnum
    ) {
        shoppingCart.ensureIsReadyForCheckout();

        var order = Order.draft(shoppingCart.customerId());

        order.changeBilling(billing);
        order.changeShipping(shipping);
        order.changePaymentMethod(paymentMethodEnum);

        shoppingCart.items().forEach(item -> order.addItem(Product.builder()
                        .id(item.productId())
                        .name(item.name())
                        .price(new Money(item.price().value()))
                        .inStock(item.isAvailable())
                        .build(),
                item.quantity()));

        order.place();
        shoppingCart.empty();

        return order;
    }
}
