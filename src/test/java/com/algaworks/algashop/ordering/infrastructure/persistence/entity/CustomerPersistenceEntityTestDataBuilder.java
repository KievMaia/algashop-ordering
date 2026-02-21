package com.algaworks.algashop.ordering.infrastructure.persistence.entity;

import com.algaworks.algashop.ordering.infrastructure.persistence.customer.CustomerPersistenceEntity;
import com.algaworks.algashop.ordering.infrastructure.persistence.commons.AddressEmbeddable;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.UUID;

import static com.algaworks.algashop.ordering.domain.model.customer.CustomerTestDataBuilder.DEFAULT_CUSTOMER_ID;

public class CustomerPersistenceEntityTestDataBuilder {

    private CustomerPersistenceEntityTestDataBuilder() {
    }

    public static CustomerPersistenceEntity.CustomerPersistenceEntityBuilder aCustomer() {
        return CustomerPersistenceEntity.builder()
                .id(DEFAULT_CUSTOMER_ID.value())
                .registeredAt(OffsetDateTime.now())
                .promotionNotificationsAllowed(true)
                .archived(false)
                .archivedAt(null)
                .firstName("John")
                .lastName("Doe")
                .birthDate(LocalDate.of(1991, 7,5))
                .email("johndoe@email.com")
                .phone("478-256-2604")
                .document("255-08-0578")
                .promotionNotificationsAllowed(true)
                .loyaltyPoints(0)
                .address(AddressEmbeddable.builder()
                                 .street("Bourbon Street")
                                 .number("1134")
                                 .neighborhood("North Ville")
                                 .city("York")
                                 .state("South California")
                                 .zipCode("12345")
                                 .complement("Apt. 114")
                                 .build())
                ;
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