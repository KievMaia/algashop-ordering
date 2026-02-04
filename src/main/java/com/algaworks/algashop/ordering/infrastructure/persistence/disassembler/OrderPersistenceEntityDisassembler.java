package com.algaworks.algashop.ordering.infrastructure.persistence.disassembler;

import com.algaworks.algashop.ordering.domain.model.entity.Order;
import com.algaworks.algashop.ordering.domain.model.entity.OrderItem;
import com.algaworks.algashop.ordering.domain.model.entity.OrderStatusEnum;
import com.algaworks.algashop.ordering.domain.model.entity.PaymentMethodEnum;
import com.algaworks.algashop.ordering.domain.model.valueobject.Address;
import com.algaworks.algashop.ordering.domain.model.valueobject.Billing;
import com.algaworks.algashop.ordering.domain.model.valueobject.Document;
import com.algaworks.algashop.ordering.domain.model.valueobject.Email;
import com.algaworks.algashop.ordering.domain.model.valueobject.FullName;
import com.algaworks.algashop.ordering.domain.model.valueobject.Money;
import com.algaworks.algashop.ordering.domain.model.valueobject.Phone;
import com.algaworks.algashop.ordering.domain.model.valueobject.ProductName;
import com.algaworks.algashop.ordering.domain.model.valueobject.Quantity;
import com.algaworks.algashop.ordering.domain.model.valueobject.Recipient;
import com.algaworks.algashop.ordering.domain.model.valueobject.Shipping;
import com.algaworks.algashop.ordering.domain.model.valueobject.ZipCode;
import com.algaworks.algashop.ordering.domain.model.valueobject.id.CustomerId;
import com.algaworks.algashop.ordering.domain.model.valueobject.id.OrderId;
import com.algaworks.algashop.ordering.domain.model.valueobject.id.OrderItemId;
import com.algaworks.algashop.ordering.domain.model.valueobject.id.ProductId;
import com.algaworks.algashop.ordering.infrastructure.persistence.embeddable.AddressEmbeddable;
import com.algaworks.algashop.ordering.infrastructure.persistence.embeddable.BillingEmbeddable;
import com.algaworks.algashop.ordering.infrastructure.persistence.embeddable.ShippingEmbeddable;
import com.algaworks.algashop.ordering.infrastructure.persistence.entity.OrderItemPersistenceEntity;
import com.algaworks.algashop.ordering.infrastructure.persistence.entity.OrderPersistenceEntity;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class OrderPersistenceEntityDisassembler {

    public Order toDomainEntity(OrderPersistenceEntity persistenceEntity) {
        return Order.existing()
                .id(new OrderId(persistenceEntity.getId()))
                .customerId(new CustomerId(persistenceEntity.getCustomerId()))
                .totalAmount(new Money(persistenceEntity.getTotalAmount()))
                .totalItems(new Quantity(persistenceEntity.getTotalItems()))
                .status(OrderStatusEnum.valueOf(persistenceEntity.getStatus()))
                .paymentMethod(PaymentMethodEnum.valueOf(persistenceEntity.getPaymentMethod()))
                .placedAt(persistenceEntity.getPlacedAt())
                .paidAt(persistenceEntity.getPaidAt())
                .canceledAt(persistenceEntity.getCanceledAt())
                .readyAt(persistenceEntity.getReadyAt())
                .items(new HashSet<>())
                .version(persistenceEntity.getVersion())
                .billing(toBillingValueObject(persistenceEntity.getBilling()))
                .shipping(toShippingValueObject(persistenceEntity.getShipping()))
                .items(toDomainEntity(persistenceEntity.getItems()))
                .build();
    }

    private Set<OrderItem> toDomainEntity(Set<OrderItemPersistenceEntity> items) {
        return items.stream().map(this::toDomainEntity).collect(Collectors.toSet());
    }

    private OrderItem toDomainEntity(OrderItemPersistenceEntity persistenceEntity) {
        return OrderItem.existing()
                .id(new OrderItemId(persistenceEntity.getId()))
                .orderId(new OrderId(persistenceEntity.getOrderId()))
                .productId(new ProductId(persistenceEntity.getProductId()))
                .productName(new ProductName(persistenceEntity.getProductName()))
                .price(new Money(persistenceEntity.getPrice()))
                .quantity(new Quantity(persistenceEntity.getQuantity()))
                .totalAmount(new Money(persistenceEntity.getTotalAmount()))
                .build();
    }

    private Billing toBillingValueObject(BillingEmbeddable billing) {
        if (billing == null) {
            return null;
        }
        return Billing.builder()
                .fullName(new FullName(billing.getFirstName(), billing.getLastName()))
                .phone(new Phone(billing.getPhone()))
                .document(new Document(billing.getDocument()))
                .email(new Email(billing.getEmail()))
                .address(toAddressValueObject(billing.getAddress()))
                .build();
    }

    private static Address toAddressValueObject(AddressEmbeddable address) {
        if (address == null) {
            return null;
        }
        return new Address(address.getStreet(),
                address.getComplement(),
                address.getNeighborhood(),
                address.getNumber(),
                address.getCity(),
                address.getState(),
                new ZipCode(address.getZipcode()));
    }


    private Shipping toShippingValueObject(ShippingEmbeddable shipping) {
        if (shipping == null) {
            return null;
        }

        var shippingBuilder = Shipping.builder()
                .cost(new Money(shipping.getCost()))
                .expectedDate(shipping.getExpectedDate())
                .address(toAddressValueObject(shipping.getAddress()));

        var recipient = shipping.getRecipient();

        if (recipient != null) {
            shippingBuilder.recipient(Recipient.builder()
                    .fullName(new FullName(shipping.getRecipient().getFirstName(),
                            shipping.getRecipient().getLastName()))
                    .document(new Document(shipping.getRecipient().getDocument()))
                    .phone(new Phone(shipping.getRecipient().getPhone()))
                    .build());
        }
        return shippingBuilder.build();
    }
}
