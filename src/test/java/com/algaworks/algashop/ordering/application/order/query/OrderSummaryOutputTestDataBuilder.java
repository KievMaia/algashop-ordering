package com.algaworks.algashop.ordering.application.order.query;

import com.algaworks.algashop.ordering.domain.model.customer.CustomerId;
import com.algaworks.algashop.ordering.domain.model.order.OrderId;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

public class OrderSummaryOutputTestDataBuilder {

    public static OrderSummaryOutput.OrderSummaryOutputBuilder placedOrder() {
        return placedOrder(new OrderId().toString());
    }

    public static OrderSummaryOutput.OrderSummaryOutputBuilder placedOrder(String orderId) {
        return OrderSummaryOutput.builder()
                .id(orderId)
                .customerMinimalOutput(CustomerMinimalOutput.builder()
                        .id(new CustomerId().value())
                        .firstName("John")
                        .lastName("Doe")
                        .document("12345")
                        .email("johndoe@email.com")
                        .phone("1191234564")
                        .build())
                .totalItems(2)
                .totalAmount(new BigDecimal("41.98"))
                .placedAt(OffsetDateTime.now())
                .paidAt(null)
                .canceledAt(null)
                .readyAt(null)
                .status("PLACED")
                .paymentMethod("GATEWAY_BALANCE");
    }

    public static OrderSummaryOutput.OrderSummaryOutputBuilder placedOrderAlt1() {
        return placedOrder("01226N0693HDE")
                .totalItems(1)
                .totalAmount(new BigDecimal("19.99"));
    }
}
