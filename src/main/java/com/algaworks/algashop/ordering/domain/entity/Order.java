package com.algaworks.algashop.ordering.domain.entity;


import com.algaworks.algashop.ordering.domain.valueobject.BillingInfo;
import com.algaworks.algashop.ordering.domain.valueobject.Money;
import com.algaworks.algashop.ordering.domain.valueobject.ProductName;
import com.algaworks.algashop.ordering.domain.valueobject.Quantity;
import com.algaworks.algashop.ordering.domain.valueobject.ShippingInfo;
import com.algaworks.algashop.ordering.domain.valueobject.id.CustomerId;
import com.algaworks.algashop.ordering.domain.valueobject.id.OrderId;
import com.algaworks.algashop.ordering.domain.valueobject.id.ProductId;
import lombok.Builder;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.Collections;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

public class Order {

    private OrderId id;
    private CustomerId customerId;

    private Money totalAmount;
    private Quantity totalItems;

    private OffsetDateTime placedAt;
    private OffsetDateTime paidAt;
    private OffsetDateTime canceledAt;
    private OffsetDateTime readydAt;

    private BillingInfo billing;
    private ShippingInfo shipping;

    private OrderStatusEnum status;
    private PaymentMethodEnum paymentMethod;

    private Money shippingCost;
    private LocalDate expectedDeliveryDate;

    private Set<OrderItem> items;

    @Builder(builderClassName = "ExistingOrderBuilder", builderMethodName = "existing")
    public Order(OrderId id, CustomerId customerId, Money totalAmount, Quantity totalItems, OffsetDateTime placedAt, OffsetDateTime paidAt, OffsetDateTime canceledAt, OffsetDateTime readydAt, BillingInfo billing, ShippingInfo shipping, OrderStatusEnum status, PaymentMethodEnum paymentMethod, Money shippingCost, LocalDate expectedDeliveryDate, Set<OrderItem> items) {
        this.setId(id);
        this.setCustomerId(customerId);
        this.setTotalAmount(totalAmount);
        this.setTotalItems(totalItems);
        this.setPlacedAt(placedAt);
        this.setPaidAt(paidAt);
        this.setCanceledAt(canceledAt);
        this.setReadydAt(readydAt);
        this.setBilling(billing);
        this.setShipping(shipping);
        this.setStatus(status);
        this.setPaymentMethod(paymentMethod);
        this.setShippingCost(shippingCost);
        this.setExpectedDeliveryDate(expectedDeliveryDate);
        this.setItems(items);
    }

    public static Order draft(CustomerId customerId) {
        return new Order(
                new OrderId(),
                customerId,
                Money.ZERO,
                Quantity.ZERO,
                null,
                null,
                null,
                null,
                null,
                null,
                OrderStatusEnum.DRAFT,
                null,
                null,
                null,
                new HashSet<>()
        );
    }

    public void addItem(ProductId productId, ProductName productName, Money price, Quantity quantity) {
        var orderItem = OrderItem.brandNew()
                .orderId(this.id())
                .price(price)
                .quantity(quantity)
                .productName(productName)
                .productId(productId)
                .build();

        if (this.items == null) {
            this.items = new HashSet<>();
        }

        this.items.add(orderItem);
    }

    public OrderId id() {
        return id;
    }

    public CustomerId customerId() {
        return customerId;
    }

    public Money totalAmount() {
        return totalAmount;
    }

    public Quantity totalItems() {
        return totalItems;
    }

    public OffsetDateTime placedAt() {
        return placedAt;
    }

    public OffsetDateTime paidAt() {
        return paidAt;
    }

    public OffsetDateTime canceledAt() {
        return canceledAt;
    }

    public OffsetDateTime readydAt() {
        return readydAt;
    }

    public BillingInfo billing() {
        return billing;
    }

    public ShippingInfo shipping() {
        return shipping;
    }

    public OrderStatusEnum status() {
        return status;
    }

    public PaymentMethodEnum paymentMethod() {
        return paymentMethod;
    }

    public Money shippingCost() {
        return shippingCost;
    }

    public LocalDate expectedDeliveryDate() {
        return expectedDeliveryDate;
    }

    public Set<OrderItem> items() {
        return Collections.unmodifiableSet(this.items);
    }

    private void setId(OrderId id) {
        Objects.requireNonNull(id);
        this.id = id;
    }

    private void setCustomerId(CustomerId customerId) {
        Objects.requireNonNull(customerId);
        this.customerId = customerId;
    }

    private void setTotalAmount(Money totalAmount) {
        Objects.requireNonNull(totalAmount);
        this.totalAmount = totalAmount;
    }

    private void setTotalItems(Quantity totalItems) {
        Objects.requireNonNull(totalItems);
        this.totalItems = totalItems;
    }

    private void setPlacedAt(OffsetDateTime placedAt) {
        this.placedAt = placedAt;
    }

    private void setPaidAt(OffsetDateTime paidAt) {
        this.paidAt = paidAt;
    }

    private void setCanceledAt(OffsetDateTime canceledAt) {
        this.canceledAt = canceledAt;
    }

    private void setReadydAt(OffsetDateTime readydAt) {
        this.readydAt = readydAt;
    }

    private void setBilling(BillingInfo billing) {
        this.billing = billing;
    }

    private void setShipping(ShippingInfo shipping) {
        this.shipping = shipping;
    }

    private void setStatus(OrderStatusEnum status) {
        Objects.requireNonNull(status);
        this.status = status;
    }

    private void setPaymentMethod(PaymentMethodEnum paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    private void setShippingCost(Money shippingCost) {
        this.shippingCost = shippingCost;
    }

    private void setExpectedDeliveryDate(LocalDate expectedDeliveryDate) {
        this.expectedDeliveryDate = expectedDeliveryDate;
    }

    private void setItems(Set<OrderItem> items) {
        Objects.requireNonNull(items);
        this.items = items;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Order order = (Order) o;
        return Objects.equals(id, order.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}
