package com.algaworks.algashop.ordering.domain.model.service;

import com.algaworks.algashop.ordering.domain.model.entity.Order;
import com.algaworks.algashop.ordering.domain.model.entity.PaymentMethodEnum;
import com.algaworks.algashop.ordering.domain.model.entity.ShoppingCart;
import com.algaworks.algashop.ordering.domain.model.utility.DomainService;
import com.algaworks.algashop.ordering.domain.model.valueobject.Billing;
import com.algaworks.algashop.ordering.domain.model.valueobject.Money;
import com.algaworks.algashop.ordering.domain.model.valueobject.Product;
import com.algaworks.algashop.ordering.domain.model.valueobject.Shipping;

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
