package com.algaworks.algashop.ordering.domain.model.order;

import com.algaworks.algashop.ordering.domain.model.commons.Money;
import com.algaworks.algashop.ordering.domain.model.commons.Quantity;
import com.algaworks.algashop.ordering.domain.model.customer.CustomerTestDataBuilder;
import com.algaworks.algashop.ordering.domain.model.customer.LoyaltyPoints;
import com.algaworks.algashop.ordering.domain.model.product.Product;
import com.algaworks.algashop.ordering.domain.model.product.ProductTestDataBuilder;
import com.algaworks.algashop.ordering.domain.model.shoppingcart.ShoppingCart;
import com.algaworks.algashop.ordering.domain.model.shoppingcart.ShoppingCartCantProceedToCheckoutException;
import com.algaworks.algashop.ordering.domain.model.shoppingcart.ShoppingCartTestDataBuilder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

@ExtendWith(MockitoExtension.class)
class CheckoutServiceTest {

    private CheckoutService checkoutService;

    @Mock
    private Orders orders;

    @BeforeEach
    void setUp() {
        var specification =
                new CustomerHaveFreeShippingSpecification(orders, new LoyaltyPoints(100), 2, new LoyaltyPoints(2000));
        checkoutService = new CheckoutService(specification);
    }

    @Test
    public void checkoutValidShoppingCart() {
        var customer = CustomerTestDataBuilder.existingCustomer().build();
        var shoppingCart = ShoppingCartTestDataBuilder.aShoppingCart().customerId(customer.id()).withItems(true).build();

        var billing = OrderTestDataBuilder.aBilling();
        var shipping = OrderTestDataBuilder.aShipping();

        var expectedTotal = shoppingCart.totalAmount().value().add(shipping.cost().value());
        var expectedItemsCount = shoppingCart.totalItems().value();

        var checkouted = checkoutService.checkout(customer, shoppingCart, billing, shipping, PaymentMethodEnum.CREDIT_CARD);

        assertThat(checkouted.totalItems().value()).isEqualTo(expectedItemsCount);
        assertThat(checkouted.totalAmount().value()).isEqualByComparingTo(expectedTotal);
        assertThat(shoppingCart.isEmpty()).isTrue();

    }

    @Test
    public void shouldThrowExceptionWhenCheckoutHaveUnavailableItems() {
        var customer = CustomerTestDataBuilder.existingCustomer().build();
        var shoppingCart = ShoppingCartTestDataBuilder.aShoppingCart().customerId(customer.id()).withItems(false).build();
        var product = ProductTestDataBuilder.aProduct().build();
        shoppingCart.addItem(product, new Quantity(1));

        var unavailableProduct = ProductTestDataBuilder.aProduct().inStock(false).build();
        shoppingCart.refreshItem(unavailableProduct);

        var billing = OrderTestDataBuilder.aBilling();
        var shipping = OrderTestDataBuilder.aShipping();

        assertThatExceptionOfType(ShoppingCartCantProceedToCheckoutException.class)
                .isThrownBy(() ->
                        checkoutService.checkout(
                                customer,
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
        var customer = CustomerTestDataBuilder.existingCustomer().build();
        var shoppingCart = ShoppingCartTestDataBuilder.aShoppingCart().customerId(customer.id()).withItems(false).build();

        var billing = OrderTestDataBuilder.aBilling();
        var shipping = OrderTestDataBuilder.aShipping();

        assertThatExceptionOfType(ShoppingCartCantProceedToCheckoutException.class)
                .isThrownBy(() ->
                        checkoutService.checkout(
                                customer,
                                shoppingCart,
                                billing,
                                shipping,
                                PaymentMethodEnum.CREDIT_CARD)
                );
        assertThat(shoppingCart.isEmpty()).isTrue();
    }

    @Test
    void givenShoppingCartWithUnavailableItems_whenCheckout_shouldNotModifyShoppingCartState() {
        var customer = CustomerTestDataBuilder.existingCustomer().build();
        var shoppingCart = ShoppingCart.startShopping(customer.id());
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
                .isThrownBy(() -> checkoutService.checkout(customer, shoppingCart, billingInfo, shippingInfo, paymentMethod));

        assertThat(shoppingCart.isEmpty()).isFalse();

        var expectedTotalAmount = productInStock.price()
                .multiply(new Quantity(2)).add(productAlt.price());
        assertThat(shoppingCart.totalAmount()).isEqualTo(expectedTotalAmount);
        assertThat(shoppingCart.totalItems()).isEqualTo(new Quantity(3));
        assertThat(shoppingCart.items()).hasSize(2);
    }

    @Test
    public void givenValidShoppingCartAndCustomerWithFreeShipping_whenCheckout_shouldReturnPlacedOrderWithFreeShipping() {
        var customer = CustomerTestDataBuilder.existingCustomer().loyaltyPoints(new LoyaltyPoints(3000)).build();
        var shoppingCart = ShoppingCartTestDataBuilder.aShoppingCart().customerId(customer.id()).withItems(true).build();

        var billing = OrderTestDataBuilder.aBilling();
        var shipping = OrderTestDataBuilder.aShipping();

        var expectedTotal = shoppingCart.totalAmount().value();
        var expectedItemsCount = shoppingCart.totalItems().value();

        var order = checkoutService.checkout(customer, shoppingCart, billing, shipping, PaymentMethodEnum.CREDIT_CARD);

        assertThat(order.totalItems().value()).isEqualTo(expectedItemsCount);
        assertThat(order.totalAmount().value()).isEqualByComparingTo(expectedTotal);
        assertThat(shoppingCart.isEmpty()).isTrue();

        assertThat(order.shipping()).isEqualTo(shipping.toBuilder().cost(Money.ZERO).build());

    }
}