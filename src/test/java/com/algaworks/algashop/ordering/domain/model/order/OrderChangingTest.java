package com.algaworks.algashop.ordering.domain.model.order;

import com.algaworks.algashop.ordering.domain.model.product.ProductTestDataBuilder;
import com.algaworks.algashop.ordering.domain.model.commons.Quantity;
import com.algaworks.algashop.ordering.domain.model.customer.CustomerId;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static com.algaworks.algashop.ordering.domain.model.order.OrderStatusEnum.CANCELED;
import static com.algaworks.algashop.ordering.domain.model.order.OrderStatusEnum.PAID;
import static com.algaworks.algashop.ordering.domain.model.order.OrderStatusEnum.PLACED;

public class OrderChangingTest {

    @Nested
    @DisplayName("Given a DRAFT order")
    class DraftOrder {

        private Order order;

        @BeforeEach
        void setUp() {
            order = Order.draft(new CustomerId());
        }

        @Test
        @DisplayName("Should allow adding items")
        void shouldAllowAddItem() {
            var product = ProductTestDataBuilder.aProductAltMousePad().build();
            order.addItem(product, new Quantity(3));
            Assertions.assertThat(order.items()).isNotEmpty();
        }

        @Test
        @DisplayName("Should allow changing shipping address")
        void shouldAllowChangeShipping() {
            var shipping = OrderTestDataBuilder.aShipping();
            order.changeShipping(shipping);
            Assertions.assertThat(order.shipping().address().street()).isEqualTo(shipping.address().street());
        }

        @Test
        @DisplayName("Should allow changing billing address")
        void shouldAllowChangeBilling() {
            var billingAlt = OrderTestDataBuilder.aBillingAlt();
            order.changeBilling(billingAlt);
            Assertions.assertThat(order.billing().address().street()).isEqualTo(billingAlt.address().street());
        }

        @Test
        @DisplayName("Should allow changing payment method")
        void shouldAllowChangePaymentMethod() {
            var method = PaymentMethodEnum.CREDIT_CARD;
            order.changePaymentMethod(method);
            Assertions.assertThat(order.paymentMethod()).isEqualTo(method);
        }
    }

    @Nested
    @DisplayName("Given an order that is NOT in DRAFT")
    class NonDraftOrder {

        @Test
        @DisplayName("Should block changes when status is PLACED")
        void shouldBlockWhenPlaced() {
            var order = OrderTestDataBuilder.anOrder().orderStatusEnum(PLACED).build();
            assertBlock(order);
        }

        @Test
        @DisplayName("Should block changes when status is PAID")
        void shouldBlockWhenPaid() {
            var order = OrderTestDataBuilder.anOrder().orderStatusEnum(PAID).build();
            assertBlock(order);
        }

        @Test
        @DisplayName("Should block changes when status is CANCELED")
        void shouldBlockWhenCanceled() {
            var order = OrderTestDataBuilder.anOrder().orderStatusEnum(CANCELED).build();
            assertBlock(order);
        }

        @Test
        @DisplayName("Should block changes after transitioning from DRAFT to PLACED")
        void shouldBlockAfterTransition() {
            var order = OrderTestDataBuilder.anOrder().build();
            order.place();
            assertBlock(order);
        }

        private void assertBlock(Order order) {
            Assertions.assertThatExceptionOfType(OrderCannotBeEditedException.class)
                    .isThrownBy(() -> order.addItem(ProductTestDataBuilder.aProductAltMousePad().build(), new Quantity(1)));
        }
    }
}