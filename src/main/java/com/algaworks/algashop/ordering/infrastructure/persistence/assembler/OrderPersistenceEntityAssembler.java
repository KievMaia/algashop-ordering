package com.algaworks.algashop.ordering.infrastructure.persistence.assembler;

import com.algaworks.algashop.ordering.domain.model.entity.Order;
import com.algaworks.algashop.ordering.domain.model.entity.OrderItem;
import com.algaworks.algashop.ordering.domain.model.valueobject.Address;
import com.algaworks.algashop.ordering.domain.model.valueobject.Billing;
import com.algaworks.algashop.ordering.domain.model.valueobject.Shipping;
import com.algaworks.algashop.ordering.infrastructure.persistence.embeddable.AddressEmbeddable;
import com.algaworks.algashop.ordering.infrastructure.persistence.embeddable.BillingEmbeddable;
import com.algaworks.algashop.ordering.infrastructure.persistence.embeddable.RecipientEmbeddable;
import com.algaworks.algashop.ordering.infrastructure.persistence.embeddable.ShippingEmbeddable;
import com.algaworks.algashop.ordering.infrastructure.persistence.entity.OrderItemPersistenceEntity;
import com.algaworks.algashop.ordering.infrastructure.persistence.entity.OrderPersistenceEntity;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class OrderPersistenceEntityAssembler {

    public OrderPersistenceEntity fromDomain(Order order) {
        return merge(new OrderPersistenceEntity(), order);
    }

    public OrderPersistenceEntity merge(OrderPersistenceEntity orderPersistenceEntity, Order order) {
        orderPersistenceEntity.setId(order.id().value().toLong());
        orderPersistenceEntity.setCustomerId(order.customerId().value());
        orderPersistenceEntity.setTotalAmount(order.totalAmount().value());
        orderPersistenceEntity.setTotalItems(order.totalItems().value());
        orderPersistenceEntity.setStatus(order.status().name());
        orderPersistenceEntity.setPaymentMethod(order.paymentMethod().name());
        orderPersistenceEntity.setPlacedAt(order.placedAt());
        orderPersistenceEntity.setPaidAt(order.paidAt());
        orderPersistenceEntity.setCanceledAt(order.canceledAt());
        orderPersistenceEntity.setReadyAt(order.readyAt());
        orderPersistenceEntity.setVersion(order.version());
        orderPersistenceEntity.setBilling(toBillingEmbeddable(order.billing()));
        orderPersistenceEntity.setShipping(toShippingEmbeddable(order.shipping()));
        var mergedItems = mergeItems(order, orderPersistenceEntity);
        orderPersistenceEntity.setItems(mergedItems);
        return orderPersistenceEntity;
    }

    private Set<OrderItemPersistenceEntity> mergeItems(Order order, OrderPersistenceEntity orderPersistenceEntity) {
        var newOrUpdatedItems = order.items();

        if (newOrUpdatedItems == null || newOrUpdatedItems.isEmpty()) {
            return new HashSet<>();
        }

        var existingItems = orderPersistenceEntity.getItems();
        if (existingItems == null || existingItems.isEmpty()) {
            return newOrUpdatedItems.stream()
                    .map(this::fromDomain)
                    .collect(Collectors.toSet());
        }

        var existingItemMap =
                existingItems.stream()
                        .collect(Collectors.toMap(OrderItemPersistenceEntity::getId, item -> item));

        return newOrUpdatedItems.stream()
                .map(orderItem -> {
                    var itemPersistence =
                            existingItemMap.getOrDefault(
                                    orderItem.id().value().toLong(),
                                    new OrderItemPersistenceEntity()
                            );
                    return merge(itemPersistence, orderItem);
                }).collect(Collectors.toSet());
    }

    public OrderItemPersistenceEntity fromDomain(OrderItem orderItem) {
        return merge(new OrderItemPersistenceEntity(), orderItem);
    }

    public OrderItemPersistenceEntity merge(OrderItemPersistenceEntity orderItemPersistenceEntity, OrderItem orderItem) {
        orderItemPersistenceEntity.setId(orderItem.id().value().toLong());
        orderItemPersistenceEntity.setProductId(orderItem.productId().value());
        orderItemPersistenceEntity.setProductName(orderItem.productName().value());
        orderItemPersistenceEntity.setPrice(orderItem.price().value());
        orderItemPersistenceEntity.setQuantity(orderItem.quantity().value());
        orderItemPersistenceEntity.setTotalAmount(orderItem.totalAmount().value());
        return orderItemPersistenceEntity;
    }

    private BillingEmbeddable toBillingEmbeddable(Billing billing) {
        if (billing == null) {
            return null;
        }
        return BillingEmbeddable.builder()
                .firstName(billing.fullName().firstName())
                .lastName(billing.fullName().lastName())
                .phone(billing.phone().value())
                .document(billing.document().value())
                .email(billing.email().value())
                .address(toAddressEmbeddable(billing.address()))
                .build();
    }

    private ShippingEmbeddable toShippingEmbeddable(Shipping shipping) {
        if (shipping == null) {
            return null;
        }
        var shippingEmbeddableBuilder = ShippingEmbeddable.builder()
                .cost(shipping.cost().value())
                .expectedDate(shipping.expectedDate())
                .address(toAddressEmbeddable(shipping.address()));

        var recipient = shipping.recipient();

        if (recipient != null) {
            shippingEmbeddableBuilder.recipient(RecipientEmbeddable.builder()
                    .firstName(recipient.fullName().firstName())
                    .lastName(recipient.fullName().lastName())
                    .document(recipient.document().value())
                    .phone(recipient.phone().value())
                    .build()
            );
        }
        return shippingEmbeddableBuilder.build();
    }

    private AddressEmbeddable toAddressEmbeddable(Address address) {
        if (address == null) {
            return null;
        }
        return AddressEmbeddable.builder()
                .street(address.street())
                .number(address.number())
                .complement(address.complement())
                .city(address.city())
                .neighborhood(address.neighborhood())
                .state(address.state())
                .zipcode(address.zipCode().value())
                .build();
    }
}
