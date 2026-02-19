package com.algaworks.algashop.ordering.domain.model.service;

import com.algaworks.algashop.ordering.domain.model.entity.PaymentMethodEnum;
import com.algaworks.algashop.ordering.domain.model.entity.ShoppingCart;
import com.algaworks.algashop.ordering.domain.model.entity.order.OrderTestDataBuilder;
import com.algaworks.algashop.ordering.domain.model.entity.product.ProductTestDataBuilder;
import com.algaworks.algashop.ordering.domain.model.entity.shoppingcart.ShoppingCartTestDataBuilder;
import com.algaworks.algashop.ordering.domain.model.exception.ShoppingCartCantProceedToCheckoutException;
import com.algaworks.algashop.ordering.domain.model.valueobject.Product;
import com.algaworks.algashop.ordering.domain.model.valueobject.Quantity;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

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

        assertThat(checkouted.totalItems().value()).isEqualTo(expectedItemsCount);
        assertThat(checkouted.totalAmount().value()).isEqualByComparingTo(expectedTotal);
        assertThat(shoppingCart.isEmpty()).isTrue();

    }

    @Test
    public void shouldThrowExceptionWhenCheckoutHaveUnavailableItems() {
        var shoppingCart = ShoppingCartTestDataBuilder.aShoppingCart()
                .withItems(false)
                .build();
        var product = ProductTestDataBuilder.aProduct().build();
        shoppingCart.addItem(product, new Quantity(1));

        var unavailableProduct = ProductTestDataBuilder.aProduct().inStock(false).build();
        shoppingCart.refreshItem(unavailableProduct);

        var billing = OrderTestDataBuilder.aBilling();
        var shipping = OrderTestDataBuilder.aShipping();

        assertThatExceptionOfType(ShoppingCartCantProceedToCheckoutException.class)
                .isThrownBy(() ->
                        checkoutService.checkout(
                                shoppingCart,
                                billing,
                                shipping,
                                PaymentMethodEnum.CREDIT_CARD)
                );
        assertThat(shoppingCart.isEmpty()).isFalse();
        assertThat(shoppingCart.items()).hasSize(1);
    }

    @Test
    public void shouldThrowExceptionWhenCheckoutHaveShoppingCartEmpty() {
        var shoppingCart = ShoppingCartTestDataBuilder.aShoppingCart()
                .withItems(false)
                .build();

        var billing = OrderTestDataBuilder.aBilling();
        var shipping = OrderTestDataBuilder.aShipping();

        assertThatExceptionOfType(ShoppingCartCantProceedToCheckoutException.class)
                .isThrownBy(() ->
                        checkoutService.checkout(
                                shoppingCart,
                                billing,
                                shipping,
                                PaymentMethodEnum.CREDIT_CARD)
                );
        assertThat(shoppingCart.isEmpty()).isTrue();
    }

    @Test
    void givenShoppingCartWithUnavailableItems_whenCheckout_shouldNotModifyShoppingCartState() {
        var shoppingCart = ShoppingCart.startShopping(ShoppingCartTestDataBuilder.aShoppingCart().customerId);
        Product productInStock = ProductTestDataBuilder.aProduct().build();
        shoppingCart.addItem(productInStock, new Quantity(2));

        var productAlt = ProductTestDataBuilder.aProductAltRamMemory().build();
        shoppingCart.addItem(productAlt, new Quantity(1));

        Product productAltUnavailable = ProductTestDataBuilder.aProductAltRamMemory().id(productAlt.id()).inStock(false).build();
        shoppingCart.refreshItem(productAltUnavailable);

        var billingInfo = OrderTestDataBuilder.aBilling();
        var shippingInfo = OrderTestDataBuilder.aShipping();
        var paymentMethod = PaymentMethodEnum.CREDIT_CARD;

        assertThatExceptionOfType(ShoppingCartCantProceedToCheckoutException.class)
                .isThrownBy(() -> checkoutService.checkout(shoppingCart, billingInfo, shippingInfo, paymentMethod));

        assertThat(shoppingCart.isEmpty()).isFalse();

        var expectedTotalAmount = productInStock.price()
                .multiply(new Quantity(2)).add(productAlt.price());
        assertThat(shoppingCart.totalAmount()).isEqualTo(expectedTotalAmount);
        assertThat(shoppingCart.totalItems()).isEqualTo(new Quantity(3));
        assertThat(shoppingCart.items()).hasSize(2);
    }
}