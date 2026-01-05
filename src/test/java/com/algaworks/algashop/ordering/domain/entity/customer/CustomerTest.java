package com.algaworks.algashop.ordering.domain.entity.customer;

import com.algaworks.algashop.ordering.domain.exception.CustomerArchivedException;
import com.algaworks.algashop.ordering.domain.valueobject.*;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

class CustomerTest {

    @Test
    void given_invalidEmail_whenTryCreateCustomer_shouldGenerateException() {

        assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() -> CustomerTestDataBuilder.brandNewCustomer().email(new Email("invalid")).build());
    }

    @Test
    void given_invalidEmail_whenTryUpdateCustomerEmail_shouldGenerateException() {
        var customer = CustomerTestDataBuilder.brandNewCustomer().build();

        assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() -> customer.changeEmail(new Email("invalid")));
    }

    @Test
    void given_unarchivedCustomer_whenArchive_shouldAnonymize() {
        var customer = CustomerTestDataBuilder.existingCustomer().build();

        customer.archived();

        Assertions.assertWith(customer,
                c -> Assertions.assertThat(c.fullName()).isEqualTo(new FullName("Anonymous", "Anonymous")),
                c -> Assertions.assertThat(c.email()).isNotEqualTo(new Email("john.doe@gmail.com")),
                c -> Assertions.assertThat(c.phone()).isEqualTo(new Phone("000-000-0000")),
                c -> Assertions.assertThat(c.document()).isEqualTo(new Document("000-000-0000")),
                c -> Assertions.assertThat(c.birthDate()).isNull(),
                c -> Assertions.assertThat(c.isPromotionNotificationsAllowed()).isFalse(),
                c -> Assertions.assertThat(c.address()).isEqualTo(
                        Address.builder()
                        .street("Bourbon Street")
                        .number("Anonymized")
                        .neighborhood("North Ville")
                        .city("York")
                        .state("South California")
                        .zipCode(new ZipCode("12345"))
                        .complement(null)
                        .build()));
    }

    @Test
    void given_archivedCustomer_whenTryToUpdate_shouldGenerateException() {
        var customer = CustomerTestDataBuilder.existingAnonymizedCustomer().build();


        assertThatExceptionOfType(CustomerArchivedException.class)
                .isThrownBy(customer::archived);

        assertThatExceptionOfType(CustomerArchivedException.class)
                .isThrownBy(() -> customer.changeEmail(new Email("email@gmail.com")));

        assertThatExceptionOfType(CustomerArchivedException.class)
                .isThrownBy(() -> customer.changePhone(new Phone("123-133-1111")));

        assertThatExceptionOfType(CustomerArchivedException.class)
                .isThrownBy(customer::enablePromotionNotification);

        assertThatExceptionOfType(CustomerArchivedException.class)
                .isThrownBy(customer::disablePromotionNotification);
    }

    @Test
    void given_createBrandNewCustomer_whenAddLoyaltyPoints__shouldAnonymize() {
        var customer = CustomerTestDataBuilder.brandNewCustomer().build();

        customer.addLoyaltyPoints(new LoyaltyPoints(10));
        customer.addLoyaltyPoints(new LoyaltyPoints(20));

        Assertions.assertThat(customer.loyaltyPoints().value()).isEqualTo(new LoyaltyPoints(30).value());
    }

    @Test
    void given_createBrandNewCustomer_whenAddInvalidLoyaltyPoints__shouldGenerateException() {
        var customer = CustomerTestDataBuilder.brandNewCustomer().build();

        assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() -> customer.addLoyaltyPoints(new LoyaltyPoints(0)));

        assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() -> customer.addLoyaltyPoints(new LoyaltyPoints(-10)));
    }
}