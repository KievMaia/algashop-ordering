package com.algaworks.algashop.ordering.domain.entity;

import com.algaworks.algashop.ordering.domain.exception.OrderCannotBeEditedException;
import com.algaworks.algashop.ordering.domain.exception.OrderDoesNotContainOrderItemException;
import com.algaworks.algashop.ordering.domain.valueobject.Money;
import com.algaworks.algashop.ordering.domain.valueobject.Quantity;
import com.algaworks.algashop.ordering.domain.valueobject.id.CustomerId;
import com.algaworks.algashop.ordering.domain.valueobject.id.OrderItemId;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import static com.algaworks.algashop.ordering.domain.entity.OrderStatusEnum.PLACED;

public class OrderRemoveItemTest {

    @Test
    public void shouldRemoveItem() {
        var order = Order.draft(new CustomerId());

        var productMouse = ProductTestDataBuilder.aProductAltMousePad().build();
        var productMemory = ProductTestDataBuilder.aProductAltRamMemory().build();

        order.addItem(productMouse, new Quantity(1));
        order.addItem(productMemory, new Quantity(1));

        Assertions.assertThat(order.items().size()).isEqualTo(2);
        Assertions.assertThat(order.totalAmount()).isEqualTo(new Money("300"));

        var orderItemId = order.items().stream().filter(item ->
                item.productId().equals(productMouse.id())).findFirst();
        order.removeItem(orderItemId.get().id());

        Assertions.assertThat(order.items().size()).isEqualTo(1);
        Assertions.assertThat(order.totalAmount()).isEqualTo(new Money("200"));
    }

    @Test
    public void shouldTryRemoveANonexistentItem() {
        var order = Order.draft(new CustomerId());

        Assertions.assertThatExceptionOfType(OrderDoesNotContainOrderItemException.class)
                .isThrownBy(() -> order.removeItem(new OrderItemId()));
    }

    @Test
    public void shouldTryRemoveAPlacedOrderItem() {
        var order = OrderTestDataBuilder.anOrder().orderStatusEnum(PLACED).build();

        Assertions.assertThatExceptionOfType(OrderCannotBeEditedException.class)
                .isThrownBy(() -> order.removeItem(new OrderItemId()));
    }
}
