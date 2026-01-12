package com.algaworks.algashop.ordering.domain.model.entity.order;

import com.algaworks.algashop.ordering.domain.model.entity.Order;
import com.algaworks.algashop.ordering.domain.model.entity.product.ProductTestDataBuilder;
import com.algaworks.algashop.ordering.domain.model.exception.OrderCannotBeEditedException;
import com.algaworks.algashop.ordering.domain.model.exception.OrderDoesNotContainOrderItemException;
import com.algaworks.algashop.ordering.domain.model.valueobject.Money;
import com.algaworks.algashop.ordering.domain.model.valueobject.Quantity;
import com.algaworks.algashop.ordering.domain.model.valueobject.id.CustomerId;
import com.algaworks.algashop.ordering.domain.model.valueobject.id.OrderItemId;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import static com.algaworks.algashop.ordering.domain.model.entity.OrderStatusEnum.PLACED;

public class OrderRemoveItemTest {

    @Test
    public void givenDraftOrder_whenRemoveItem_shouldRecalculate() {
        var order = Order.draft(new CustomerId());

        var productMouse = ProductTestDataBuilder.aProductAltMousePad().build();
        var productMemory = ProductTestDataBuilder.aProductAltRamMemory().build();

        order.addItem(productMouse, new Quantity(1));
        order.addItem(productMemory, new Quantity(1));

        Assertions.assertThat(order.items().size()).isEqualTo(2);
        Assertions.assertThat(order.totalAmount()).isEqualTo(new Money("250"));

        var orderItemId = order.items().stream().filter(item ->
                item.productId().equals(productMouse.id())).findFirst();
        order.removeItem(orderItemId.get().id());

        Assertions.assertWith(order,
                (i) -> Assertions.assertThat(i.items().size()).isEqualTo(1),
                (i) -> Assertions.assertThat(order.totalAmount()).isEqualTo(new Money("150")),
                (i) -> Assertions.assertThat(order.totalItems()).isEqualTo(new Quantity(1))
                );
    }

    @Test
    public void shouldTryRemoveANonexistentItem() {
        var order = OrderTestDataBuilder.anOrder().build();

        Assertions.assertThatExceptionOfType(OrderDoesNotContainOrderItemException.class)
                .isThrownBy(() -> order.removeItem(new OrderItemId()));

        Assertions.assertWith(order,
                (i) -> Assertions.assertThat(i.totalAmount()).isEqualTo(new Money("30160.00")),
                (i) -> Assertions.assertThat(i.totalItems()).isEqualTo(new Quantity(3))
        );
    }

    @Test
    public void shouldTryRemoveAPlacedOrderItem() {
        var order = OrderTestDataBuilder.anOrder().orderStatusEnum(PLACED).build();

        Assertions.assertThatExceptionOfType(OrderCannotBeEditedException.class)
                .isThrownBy(() -> order.removeItem(new OrderItemId()));

        Assertions.assertWith(order,
                (i) -> Assertions.assertThat(i.totalAmount()).isEqualTo(new Money("30160.00")),
                (i) -> Assertions.assertThat(i.totalItems()).isEqualTo(new Quantity(3))
        );
    }
}
