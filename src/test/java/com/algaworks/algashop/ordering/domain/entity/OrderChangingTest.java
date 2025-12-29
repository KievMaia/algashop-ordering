package com.algaworks.algashop.ordering.domain.entity;

import com.algaworks.algashop.ordering.domain.exception.OrderCannotBeEditedException;
import com.algaworks.algashop.ordering.domain.valueobject.Quantity;
import com.algaworks.algashop.ordering.domain.valueobject.id.CustomerId;
import org.assertj.core.api.Assertions;
import org.assertj.core.api.ThrowableAssert;
import org.junit.jupiter.api.Test;

import static com.algaworks.algashop.ordering.domain.entity.OrderStatusEnum.CANCELED;
import static com.algaworks.algashop.ordering.domain.entity.OrderStatusEnum.PAID;
import static com.algaworks.algashop.ordering.domain.entity.OrderStatusEnum.PLACED;
import static com.algaworks.algashop.ordering.domain.entity.OrderStatusEnum.READY;

public class OrderChangingTest {

    @Test
    public void givenDraftOrder_WhenTryModifiedItems_ShouldAllowChange() {
        var order = Order.draft(new CustomerId());

        var product = ProductTestDataBuilder.aProductAltMousePad().build();

        order.addItem(product, new Quantity(3));

        Assertions.assertThat(order.items()).isNotEmpty();
    }

    @Test
    public void givenDraftOrder_WhenTryModifiedShippingAddress_ShouldAllowChange() {
        var order = Order.draft(new CustomerId());
        var shipping = OrderTestDataBuilder.aShipping();
        order.changeShipping(shipping);

        Assertions.assertThat(order.shipping().address().street()).isEqualTo(shipping.address().street());
    }

    @Test
    public void givenDraftOrder_WhenTryModifiedBillingAddress_ShouldAllowChange() {
        var order = Order.draft(new CustomerId());
        var billingAlt = OrderTestDataBuilder.aBillingAlt();
        order.changeBilling(billingAlt);

        Assertions.assertThat(order.billing().address().street()).isEqualTo(billingAlt.address().street());
    }

    @Test
    public void givenDraftOrder_WhenTryModifiedPaymentMethod_ShouldAllowChange() {
        var order = Order.draft(new CustomerId());
        var paymentMethodEnum = PaymentMethodEnum.CREDIT_CARD;
        order.changePaymentMethod(paymentMethodEnum);

        Assertions.assertThat(order.paymentMethod().name()).isEqualTo(paymentMethodEnum.name());
    }

    @Test
    public void givenOrderPlaced_WhenTryModifiedItems_ShouldNotAllow() {
        var order = OrderTestDataBuilder.anOrder().orderStatusEnum(PLACED).build();

        ThrowableAssert.ThrowingCallable addItemTask = () -> order.addItem(
                ProductTestDataBuilder.aProductUnavailable().build(), new Quantity(1));

        Assertions.assertThatExceptionOfType(OrderCannotBeEditedException.class)
                .isThrownBy(addItemTask);
    }

    @Test
    public void givenOrderPaid_WhenTryModifiedItems_ShouldNotAllow() {
        var order = OrderTestDataBuilder.anOrder().orderStatusEnum(PAID).build();

        ThrowableAssert.ThrowingCallable addItemTask = () -> order.addItem(
                ProductTestDataBuilder.aProductUnavailable().build(), new Quantity(1));

        Assertions.assertThatExceptionOfType(OrderCannotBeEditedException.class)
                .isThrownBy(addItemTask);
    }

    @Test
    public void givenOrderReady_WhenTryModifiedItems_ShouldNotAllow() {
        var order = OrderTestDataBuilder.anOrder().orderStatusEnum(READY).build();

        ThrowableAssert.ThrowingCallable addItemTask = () -> order.addItem(
                ProductTestDataBuilder.aProductUnavailable().build(), new Quantity(1));

        Assertions.assertThatExceptionOfType(OrderCannotBeEditedException.class)
                .isThrownBy(addItemTask);
    }

    @Test
    public void givenOrderCancelled_WhenTryModifiedItems_ShouldNotAllow() {
        var order = OrderTestDataBuilder.anOrder().orderStatusEnum(CANCELED).build();

        ThrowableAssert.ThrowingCallable addItemTask = () -> order.addItem(
                ProductTestDataBuilder.aProductUnavailable().build(), new Quantity(1));

        Assertions.assertThatExceptionOfType(OrderCannotBeEditedException.class)
                .isThrownBy(addItemTask);
    }

    @Test
    public void givenOrder_WhenStatusChangesFromDraftToPlaced_ThenChangesShouldBeBlocked() {
        var order = OrderTestDataBuilder.anOrder().build();
        order.place();

        Assertions.assertThatExceptionOfType(OrderCannotBeEditedException.class)
                .isThrownBy(() -> order.changeShipping(OrderTestDataBuilder.aShippingAlt()))
                .withMessageContaining(order.id().value().toString())
                .withMessageContaining(PLACED.name());
    }
}
