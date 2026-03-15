package com.algaworks.algashop.ordering.domain.model.order;

import com.algaworks.algashop.ordering.domain.model.commons.Money;
import com.algaworks.algashop.ordering.domain.model.commons.Quantity;
import com.algaworks.algashop.ordering.domain.model.customer.CustomerId;
import com.algaworks.algashop.ordering.domain.model.customer.CustomerTestDataBuilder;
import com.algaworks.algashop.ordering.domain.model.customer.LoyaltyPoints;
import com.algaworks.algashop.ordering.domain.model.product.ProductOutOfStockException;
import com.algaworks.algashop.ordering.domain.model.product.ProductTestDataBuilder;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Year;

import static com.algaworks.algashop.ordering.domain.model.order.PaymentMethodEnum.CREDIT_CARD;
import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class BuyNowServiceTest {

    private BuyNowService buyNowService;

    @Mock
    private Orders orders;

    @BeforeEach
    void setUp() {
        var specification =
                new CustomerHaveFreeShippingSpecification(orders, new LoyaltyPoints(100), 2, new LoyaltyPoints(2000));
        buyNowService = new BuyNowService(specification);
    }

    @Test
    public void buyNowValidProduct() {
        var product = ProductTestDataBuilder.aProduct().build();
        var customer = CustomerTestDataBuilder.existingCustomer().build();
        var billing = OrderTestDataBuilder.aBilling();
        var shipping = OrderTestDataBuilder.aShipping();

        var order = buyNowService.buyNow(
                product,
                customer,
                billing,
                shipping,
                new Quantity(1),
                CREDIT_CARD
        );

        assertThat(order).isNotNull();
        assertThat(order.customerId()).isEqualTo(customer.id());
        assertThat(order.paymentMethod()).isNotNull();
        assertThat(order.billing()).isNotNull();
        assertThat(order.shipping()).isNotNull();

        assertThat(order.items()).hasSize(1);
        assertThat(order.isPlaced()).isTrue();

        assertThat(order.totalAmount()).isEqualTo(new Money("3010"));
        assertThat(order.totalItems()).isEqualTo(new Quantity(1));
    }

    @Test
    public void buyNowProductOutOfStock() {
        var product = ProductTestDataBuilder.aProductUnavailable().build();
        var customer = CustomerTestDataBuilder.existingCustomer().build();

        var billing = OrderTestDataBuilder.aBilling();
        var shipping = OrderTestDataBuilder.aShipping();

        Assertions.assertThatExceptionOfType(ProductOutOfStockException.class)
                .isThrownBy(() ->
                                    buyNowService.buyNow(
                                            product,
                                            customer,
                                            billing,
                                            shipping,
                                            new Quantity(1),
                                            CREDIT_CARD)
                );
    }

    @Test
    public void buyNowProductInvalidQuantity() {
        var product = ProductTestDataBuilder.aProduct().build();
        var customer = CustomerTestDataBuilder.existingCustomer().build();

        var billing = OrderTestDataBuilder.aBilling();
        var shipping = OrderTestDataBuilder.aShipping();

        Assertions.assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() ->
                                    buyNowService.buyNow(
                                            product,
                                            customer,
                                            billing,
                                            shipping,
                                            Quantity.ZERO,
                                            CREDIT_CARD)
                );
    }

    @Test
    void givenCustomerWithFreeShipping_whenBuyNow_shouldReturnPlacedOrderWithFreeShipping() {
        Mockito.when(orders.salesQuantityByCustomerInYear(
                Mockito.any(CustomerId.class),
                Mockito.any(Year.class)
        )).thenReturn(2L);

        var product = ProductTestDataBuilder.aProduct().build();
        var customer = CustomerTestDataBuilder.existingCustomer().loyaltyPoints(new LoyaltyPoints(100)).build();
        var billingInfo = OrderTestDataBuilder.aBilling();
        var shippingInfo = OrderTestDataBuilder.aShipping();
        var quantity = new Quantity(3);
        var paymentMethod = CREDIT_CARD;

        var order = buyNowService.buyNow(product, customer, billingInfo, shippingInfo, quantity, paymentMethod);

        assertThat(order).isNotNull();
        assertThat(order.id()).isNotNull();
        assertThat(order.customerId()).isEqualTo(customer.id());
        assertThat(order.billing()).isEqualTo(billingInfo);
        assertThat(order.shipping()).isEqualTo(shippingInfo.toBuilder().cost(Money.ZERO).build());
        assertThat(order.paymentMethod()).isEqualTo(paymentMethod);
        assertThat(order.isPlaced()).isTrue();

        assertThat(order.items()).hasSize(1);
        assertThat(order.items().iterator().next().productId()).isEqualTo(product.id());
        assertThat(order.items().iterator().next().quantity()).isEqualTo(quantity);
        assertThat(order.items().iterator().next().price()).isEqualTo(product.price());

        var expectedTotalAmount = product.price().multiply(quantity);
        assertThat(order.totalAmount()).isEqualTo(expectedTotalAmount);
        assertThat(order.totalItems()).isEqualTo(quantity);
    }
}