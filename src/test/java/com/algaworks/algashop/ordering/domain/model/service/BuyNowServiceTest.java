package com.algaworks.algashop.ordering.domain.model.service;

import com.algaworks.algashop.ordering.domain.model.order.BuyNowService;
import com.algaworks.algashop.ordering.domain.model.order.PaymentMethodEnum;
import com.algaworks.algashop.ordering.domain.model.order.OrderTestDataBuilder;
import com.algaworks.algashop.ordering.domain.model.product.ProductTestDataBuilder;
import com.algaworks.algashop.ordering.domain.model.product.ProductOutOfStockException;
import com.algaworks.algashop.ordering.domain.model.commons.Money;
import com.algaworks.algashop.ordering.domain.model.commons.Quantity;
import com.algaworks.algashop.ordering.domain.model.customer.CustomerId;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class BuyNowServiceTest {

    @InjectMocks
    private BuyNowService buyNowService;

    @Test
    public void buyNowValidProduct() {
        var product = ProductTestDataBuilder.aProduct().build();
        var customerId = new CustomerId();
        var billing = OrderTestDataBuilder.aBilling();
        var shipping = OrderTestDataBuilder.aShipping();

        var order = buyNowService.buyNow(
                product,
                customerId,
                billing,
                shipping,
                new Quantity(1),
                PaymentMethodEnum.CREDIT_CARD
        );

        Assertions.assertThat(order).isNotNull();
        Assertions.assertThat(order.customerId()).isEqualTo(customerId);
        Assertions.assertThat(order.paymentMethod()).isNotNull();
        Assertions.assertThat(order.billing()).isNotNull();
        Assertions.assertThat(order.shipping()).isNotNull();

        Assertions.assertThat(order.items()).hasSize(1);
        Assertions.assertThat(order.isPlaced()).isTrue();

        Assertions.assertThat(order.totalAmount()).isEqualTo(new Money("3010"));
        Assertions.assertThat(order.totalItems()).isEqualTo(new Quantity(1));
    }

    @Test
    public void buyNowProductOutOfStock() {
        var product = ProductTestDataBuilder.aProductUnavailable().build();
        var customerId = new CustomerId();

        var billing = OrderTestDataBuilder.aBilling();
        var shipping = OrderTestDataBuilder.aShipping();

        Assertions.assertThatExceptionOfType(ProductOutOfStockException.class)
                .isThrownBy(() ->
                        buyNowService.buyNow(
                                product,
                                customerId,
                                billing,
                                shipping,
                                new Quantity(1),
                                PaymentMethodEnum.CREDIT_CARD)
                );
    }

    @Test
    public void buyNowProductInvalidQuantity() {
        var product = ProductTestDataBuilder.aProduct().build();
        var customerId = new CustomerId();

        var billing = OrderTestDataBuilder.aBilling();
        var shipping = OrderTestDataBuilder.aShipping();

        Assertions.assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() ->
                        buyNowService.buyNow(
                                product,
                                customerId,
                                billing,
                                shipping,
                                Quantity.ZERO,
                                PaymentMethodEnum.CREDIT_CARD)
                );
    }
}