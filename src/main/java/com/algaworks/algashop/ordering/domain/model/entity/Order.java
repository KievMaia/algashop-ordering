package com.algaworks.algashop.ordering.domain.model.entity;


import com.algaworks.algashop.ordering.domain.model.exception.OrderCannotBePlacedException;
import com.algaworks.algashop.ordering.domain.model.exception.OrderCannotBeEditedException;
import com.algaworks.algashop.ordering.domain.model.exception.OrderDoesNotContainOrderItemException;
import com.algaworks.algashop.ordering.domain.model.exception.OrderInvalidShippingDeliveryDateException;
import com.algaworks.algashop.ordering.domain.model.exception.OrderStatusCannotBeChangedException;
import com.algaworks.algashop.ordering.domain.model.valueobject.Billing;
import com.algaworks.algashop.ordering.domain.model.valueobject.Money;
import com.algaworks.algashop.ordering.domain.model.valueobject.Product;
import com.algaworks.algashop.ordering.domain.model.valueobject.Quantity;
import com.algaworks.algashop.ordering.domain.model.valueobject.Shipping;
import com.algaworks.algashop.ordering.domain.model.valueobject.id.CustomerId;
import com.algaworks.algashop.ordering.domain.model.valueobject.id.OrderId;
import com.algaworks.algashop.ordering.domain.model.valueobject.id.OrderItemId;
import lombok.Builder;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.Collections;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

import static com.algaworks.algashop.ordering.domain.model.entity.OrderStatusEnum.DRAFT;

public class Order implements AggregateRoot<OrderId> {

    private OrderId id;
    private CustomerId customerId;

    private Money totalAmount;
    private Quantity totalItems;

    private OffsetDateTime placedAt;
    private OffsetDateTime paidAt;
    private OffsetDateTime canceledAt;
    private OffsetDateTime readyAt;

    private Billing billing;
    private Shipping shipping;

    private OrderStatusEnum status;
    private PaymentMethodEnum paymentMethod;

    private Set<OrderItem> items;

    private Long version;

    @Builder(builderClassName = "ExistingOrderBuilder", builderMethodName = "existing")
    public Order(OrderId id, Long version, CustomerId customerId, Money totalAmount, Quantity totalItems,
                 OffsetDateTime placedAt, OffsetDateTime paidAt, OffsetDateTime canceledAt, OffsetDateTime readyAt,
                 Billing billing, Shipping shipping, OrderStatusEnum status, PaymentMethodEnum paymentMethod,
                 Set<OrderItem> items) {
        this.setId(id);
        this.setVersion(version);
        this.setCustomerId(customerId);
        this.setTotalAmount(totalAmount);
        this.setTotalItems(totalItems);
        this.setPlacedAt(placedAt);
        this.setPaidAt(paidAt);
        this.setCanceledAt(canceledAt);
        this.setReadyAt(readyAt);
        this.setBilling(billing);
        this.setShipping(shipping);
        this.setStatus(status);
        this.setPaymentMethod(paymentMethod);
        this.setItems(items);
    }

    public static Order draft(CustomerId customerId) {
        return new Order(
                new OrderId(),
                null,
                customerId,
                Money.ZERO,
                Quantity.ZERO,
                null,
                null,
                null,
                null,
                null,
                null,
                DRAFT,
                null,
                new HashSet<>()
        );
    }

    public void addItem(Product product, Quantity quantity) {
        this.verifyIfChangeable();
        Objects.requireNonNull(product);
        Objects.requireNonNull(quantity);

        product.checkOutOfStock();

        var orderItem = OrderItem.brandNew()
                .orderId(this.id())
                .quantity(quantity)
                .product(product)
                .build();

        if (this.items == null) {
            this.items = new HashSet<>();
        }
        this.items.add(orderItem);
        this.recalculateTotals();
    }

    public void removeItem(OrderItemId orderItemId) {
        Objects.requireNonNull(orderItemId, "Order Item id is required");
        this.verifyIfChangeable();
        var orderItem = this.findOrderItem(orderItemId);
        items.remove(orderItem);
        this.recalculateTotals();
    }

    public void place() {
        this.verifyIfCanChangeToPlaced();
        this.changeStatus(OrderStatusEnum.PLACED);
        this.setPlacedAt(OffsetDateTime.now());
    }

    public void markAsPaid() {
        this.changeStatus(OrderStatusEnum.PAID);
        this.setPaidAt(OffsetDateTime.now());
    }

    public void markAsReady() {
        this.changeStatus(OrderStatusEnum.READY);
        this.setReadyAt(OffsetDateTime.now());
    }

    public void markAsCancelled() {
        this.changeStatus(OrderStatusEnum.CANCELED);
        this.setCanceledAt(OffsetDateTime.now());
    }

    public void changePaymentMethod(PaymentMethodEnum paymentMethod) {
        this.verifyIfChangeable();
        Objects.requireNonNull(paymentMethod);
        this.setPaymentMethod(paymentMethod);
    }

    public void changeBilling(Billing billing) {
        Objects.requireNonNull(billing);
        this.verifyIfChangeable();
        this.setBilling(billing);
    }

    public void changeShipping(Shipping newShipping) {
        Objects.requireNonNull(newShipping);
        this.verifyIfChangeable();

        if (newShipping.expectedDate().isBefore(LocalDate.now())) {
            throw new OrderInvalidShippingDeliveryDateException(this.id());
        }

        this.setShipping(newShipping);
        this.recalculateTotals();
    }

    public void changeItemQuantity(OrderItemId orderItemId, Quantity quantity) {
        Objects.requireNonNull(orderItemId);
        Objects.requireNonNull(quantity);
        this.verifyIfChangeable();

        var orderItem = this.findOrderItem(orderItemId);
        orderItem.changeQuantity(quantity);

        this.recalculateTotals();
    }

    public boolean isDraft() {
        return DRAFT.equals(this.status());
    }

    public boolean isPlaced() {
        return OrderStatusEnum.PLACED.equals(this.status());
    }

    public boolean isCanceled() {
        return OrderStatusEnum.CANCELED.equals(this.status());
    }

    public boolean isPaid() {
        return OrderStatusEnum.PAID.equals(this.status());
    }

    public boolean isReady() {
        return OrderStatusEnum.READY.equals(this.status());
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

    public OffsetDateTime readyAt() {
        return readyAt;
    }

    public Billing billing() {
        return billing;
    }

    public Shipping shipping() {
        return shipping;
    }

    public OrderStatusEnum status() {
        return status;
    }

    public PaymentMethodEnum paymentMethod() {
        return paymentMethod;
    }

    public Set<OrderItem> items() {
        return Collections.unmodifiableSet(this.items);
    }

    private void recalculateTotals() {
        var totalItemsAmount = this.items().stream().map(i -> i.totalAmount().value())
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        var totalItemsQuantity = this.items.stream().map(i -> i.quantity().value())
                .reduce(0, Integer::sum);

        BigDecimal shippingCost;
        if (this.shipping() == null) {
            shippingCost = BigDecimal.ZERO;
        } else {
            shippingCost = this.shipping.cost().value();
        }

        var totalAmount = totalItemsAmount.add(shippingCost);
        this.setTotalAmount(new Money(totalAmount));
        this.setTotalItems(new Quantity(totalItemsQuantity));
    }

    private void changeStatus(OrderStatusEnum newStatus) {
        Objects.requireNonNull(newStatus);
        if(this.status().canNotChangeTo(newStatus)) {
            throw new OrderStatusCannotBeChangedException(this.id(), this.status(), newStatus);
        }
        this.setStatus(newStatus);
    }

    private void verifyIfCanChangeToPlaced() {
        if (this.shipping == null) {
            throw OrderCannotBePlacedException.noShippingInfo(this.id());
        }
        if (this.billing == null) {
            throw OrderCannotBePlacedException.noBillingInfo(this.id());
        }
        if (this.paymentMethod == null) {
            throw OrderCannotBePlacedException.noPaymentMethodEnum(this.id());
        }
        if (this.items == null) {
            throw OrderCannotBePlacedException.noItems(this.id());
        }
    }

    private OrderItem findOrderItem(OrderItemId orderItemId) {
        Objects.requireNonNull(orderItemId);
        return this.items.stream()
                .filter(i -> i.id().equals(orderItemId))
                .findFirst()
                .orElseThrow(() -> new OrderDoesNotContainOrderItemException(this.id, orderItemId));
    }

    private void verifyIfChangeable() {
        if (!this.isDraft()){
            throw OrderCannotBeEditedException.statusDifferentOfDraft(this.id(), this.status());
        }
    }

    public Long version() {
        return version;
    }

    private void setVersion(Long version) {
        this.version = version;
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

    private void setReadyAt(OffsetDateTime readydAt) {
        this.readyAt = readydAt;
    }

    private void setBilling(Billing billing) {
        this.billing = billing;
    }

    private void setShipping(Shipping shipping) {
        this.shipping = shipping;
    }

    private void setStatus(OrderStatusEnum status) {
        Objects.requireNonNull(status);
        this.status = status;
    }

    private void setPaymentMethod(PaymentMethodEnum paymentMethod) {
        this.paymentMethod = paymentMethod;
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
