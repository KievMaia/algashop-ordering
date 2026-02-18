package com.algaworks.algashop.ordering.domain.model.service;

import com.algaworks.algashop.ordering.domain.model.entity.PaymentMethodEnum;
import com.algaworks.algashop.ordering.domain.model.entity.order.OrderTestDataBuilder;
import com.algaworks.algashop.ordering.domain.model.entity.shoppingcart.ShoppingCartTestDataBuilder;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class CheckoutServiceTest {

    @InjectMocks
    private CheckoutService checkoutService;

    @Test
    public void checkoutValidShoppingCart() {
        var shoppingCart = ShoppingCartTestDataBuilder.aShoppingCart().withItems(true).build();

        var billing = OrderTestDataBuilder.aBilling();
        var shipping = OrderTestDataBuilder.aShipping();

        var expectedTotal = shoppingCart.totalAmount().value().add(shipping.cost().value());
        var expectedItemsCount = shoppingCart.totalItems().value();

        var checkouted = checkoutService.checkout(shoppingCart, billing, shipping, PaymentMethodEnum.CREDIT_CARD);

        Assertions.assertThat(checkouted.totalItems().value()).isEqualTo(expectedItemsCount);
        Assertions.assertThat(checkouted.totalAmount().value()).isEqualByComparingTo(expectedTotal);
        Assertions.assertThat(shoppingCart.isEmpty()).isTrue();

    }
}