package com.algaworks.algashop.ordering.domain.model.factory;

import com.algaworks.algashop.ordering.domain.model.order.OrderTestDataBuilder;
import com.algaworks.algashop.ordering.domain.model.order.OrderFactory;
import com.algaworks.algashop.ordering.domain.model.order.PaymentMethodEnum;
import com.algaworks.algashop.ordering.domain.model.product.ProductTestDataBuilder;
import com.algaworks.algashop.ordering.domain.model.commons.Quantity;
import com.algaworks.algashop.ordering.domain.model.customer.CustomerId;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;


class OrderFactoryTest {

    @Test
    public void shouldGenerateFilledOrderThatCanBePlaced() {
        var shipping = OrderTestDataBuilder.aShipping();
        var billing = OrderTestDataBuilder.aBilling();

        var paymentMethodEnum = PaymentMethodEnum.GATEWAY_BALANCE;
        var product = ProductTestDataBuilder.aProduct().build();

        var quantity = new Quantity(1);
        var customerId = new CustomerId();

        var order = OrderFactory.filled(customerId, shipping, billing, paymentMethodEnum, product, quantity);

        Assertions.assertWith(order,
                o -> Assertions.assertThat(o.shipping()).isEqualTo(shipping),
                o -> Assertions.assertThat(o.billing()).isEqualTo(billing),
                o -> Assertions.assertThat(o.paymentMethod()).isEqualTo(paymentMethodEnum),
                o -> Assertions.assertThat(o.items()).isNotEmpty(),
                o -> Assertions.assertThat(o.customerId()).isNotNull(),
                o -> Assertions.assertThat(o.isDraft()).isTrue()
                );

        order.place();

        Assertions.assertThat(order.isPlaced()).isTrue();
    }
}