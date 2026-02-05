package com.algaworks.algashop.ordering.infrastructure.persistence.entity;

import com.algaworks.algashop.ordering.infrastructure.persistence.embeddable.AddressEmbeddable;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.UUID;

public class CustomerPersistenceEntityTestDataBuilder {

    private CustomerPersistenceEntityTestDataBuilder() {
    }

    public static CustomerPersistenceEntity.CustomerPersistenceEntityBuilder existingCustomer() {
        return CustomerPersistenceEntity.builder()
                .id(UUID.randomUUID())
                .firstName("John")
                .lastName("Doe")
                .birthDate(LocalDate.of(1990, 5, 15))
                .email("john.doe@example.com")
                .phone("+5511999999999")
                .document("12345678900")
                .promotionNotificationsAllowed(true)
                .archived(false)
                .registeredAt(OffsetDateTime.now())
                .loyaltyPoints(150)
                .address(defaultAddress().build())
                .createdByUserId(UUID.randomUUID())
                .lastModifiedByUserId(UUID.randomUUID())
                .lastModifiedAt(OffsetDateTime.now());
    }

    public static AddressEmbeddable.AddressEmbeddableBuilder defaultAddress() {
        return AddressEmbeddable.builder()
                .street("Main Street")
                .number("123")
                .complement("Apt 42")
                .neighborhood("Downtown")
                .city("São Paulo")
                .state("SP")
                .zipCode("01234567");
    }

    public static CustomerPersistenceEntity.CustomerPersistenceEntityBuilder archivedCustomer() {
        return existingCustomer()
                .archived(true)
                .archivedAt(OffsetDateTime.now())
                .promotionNotificationsAllowed(false);
    }
}