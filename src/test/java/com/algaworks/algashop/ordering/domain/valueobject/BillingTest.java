package com.algaworks.algashop.ordering.domain.valueobject;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class BillingTest {
    @Test
    void shouldCreateBillingInfoWithValidData() {
        var fullName = new FullName("John", "Doe");
        var document = new Document("12345678900");
        var phone = new Phone("11999999999");
        var email = new Email("joe.doe@gmail.com");
        var address = new Address("Rua A", "123", "Próximo", "SP", "São Paulo", "SP", new ZipCode("01000"));
        var billingInfo = new Billing(fullName, document, phone, email, address);
        assertEquals(fullName, billingInfo.fullName());
        assertEquals(document, billingInfo.document());
        assertEquals(phone, billingInfo.phone());
        assertEquals(email, billingInfo.email());
        assertEquals(address, billingInfo.address());
    }

    @Test
    void shouldThrowExceptionWhenFullNameIsNull() {
        var document = new Document("123");
        var phone = new Phone("11");
        var address = new Address("Rua A", "123", "Próximo", "SP", "São Paulo", "SP", new ZipCode("01000"));
        var email = new Email("joe.doe@gmail.com");
        assertThrows(NullPointerException.class, () ->
                new Billing(null, document, phone, email, address)
        );
    }

    @Test
    void shouldThrowExceptionWhenDocumentIsNull() {
        var fullName = new FullName("John", "Doe");
        var phone = new Phone("11");
        var address = new Address("Rua A", "123", "Próximo", "SP", "São Paulo", "SP", new ZipCode("01000"));
        var email = new Email("joe.doe@gmail.com");
        assertThrows(NullPointerException.class, () ->
                new Billing(fullName, null, phone, email, address)
        );
    }

    @Test
    void shouldThrowExceptionWhenPhoneIsNull() {
        var fullName = new FullName("John", "Doe");
        var document = new Document("123");
        var address = new Address("Rua A", "123", "Próximo", "SP", "São Paulo", "SP", new ZipCode("01000"));
        var email = new Email("joe.doe@gmail.com");
        assertThrows(NullPointerException.class, () ->
                new Billing(fullName, document, null, email, address)
        );
    }

    @Test
    void shouldThrowExceptionWhenAddressIsNull() {
        var fullName = new FullName("John", "Doe");
        var document = new Document("123");
        var phone = new Phone("11");
        var email = new Email("joe.doe@gmail.com");
        assertThrows(NullPointerException.class, () ->
                new Billing(fullName, document, phone, email, null)
        );
    }

    @Test
    void shouldBeImmutable() {
        var fullName = new FullName("John", "Doe");
        var document = new Document("123");
        var phone = new Phone("11");
        var address = new Address("Rua A", "123", "Próximo", "SP", "São Paulo", "SP", new ZipCode("01000"));
        var email = new Email("joe.doe@gmail.com");
        var billingInfo = new Billing(fullName, document, phone, email, address);
        assertAll(
                () -> assertEquals("John Doe", billingInfo.fullName().toString()),
                () -> assertEquals("123", billingInfo.document().toString()),
                () -> assertEquals("11", billingInfo.phone().toString()),
                () -> assertEquals(address.toString(), billingInfo.address().toString()),
                () -> assertEquals(email.toString(), billingInfo.email().toString())
        );
    }
}
