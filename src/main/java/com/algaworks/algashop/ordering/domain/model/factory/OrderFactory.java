package com.algaworks.algashop.ordering.domain.model.factory;

import com.algaworks.algashop.ordering.domain.model.entity.Order;
import com.algaworks.algashop.ordering.domain.model.entity.PaymentMethodEnum;
import com.algaworks.algashop.ordering.domain.model.valueobject.Billing;
import com.algaworks.algashop.ordering.domain.model.valueobject.Product;
import com.algaworks.algashop.ordering.domain.model.valueobject.Quantity;
import com.algaworks.algashop.ordering.domain.model.valueobject.Shipping;
import com.algaworks.algashop.ordering.domain.model.valueobject.id.CustomerId;

import java.util.Objects;

public class OrderFactory {

    private OrderFactory() {
    }

    public static Order filled(
            CustomerId customerId,
            Shipping shipping,
            Billing billing,
            PaymentMethodEnum paymentMethodEnum,
            Product product,
            Quantity productQuantity) {
        Objects.requireNonNull(customerId);
        Objects.requireNonNull(shipping);
        Objects.requireNonNull(billing);
        Objects.requireNonNull(paymentMethodEnum);
        Objects.requireNonNull(product);
        Objects.requireNonNull(productQuantity);

        var order = Order.draft(customerId);

        order.changeBilling(billing);
        order.changeShipping(shipping);
        order.changePaymentMethod(paymentMethodEnum);
        order.addItem(product, productQuantity);

        return order;
    }
}
