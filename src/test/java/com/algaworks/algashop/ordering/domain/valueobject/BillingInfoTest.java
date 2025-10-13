package com.algaworks.algashop.ordering.domain.valueobject;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class BillingInfoTest {
    @Test
    void shouldCreateBillingInfoWithValidData() {
        var fullName = new FullName("John", "Doe");
        var document = new Document("12345678900");
        var phone = new Phone("11999999999");
        var address = new Address("Rua A", "123", "Próximo", "SP", "São Paulo", "SP", new ZipCode("01000"));
        var billingInfo = new BillingInfo(fullName, document, phone, address);
        assertEquals(fullName, billingInfo.fullName());
        assertEquals(document, billingInfo.document());
        assertEquals(phone, billingInfo.phone());
        assertEquals(address, billingInfo.address());
    }

    @Test
    void shouldThrowExceptionWhenFullNameIsNull() {
        var document = new Document("123");
        var phone = new Phone("11");
        var address = new Address("Rua A", "123", "Próximo", "SP", "São Paulo", "SP", new ZipCode("01000"));
        assertThrows(NullPointerException.class, () ->
            new BillingInfo(null, document, phone, address)
        );
    }

    @Test
    void shouldThrowExceptionWhenDocumentIsNull() {
        var fullName = new FullName("John", "Doe");
        var phone = new Phone("11");
        var address = new Address("Rua A", "123", "Próximo", "SP", "São Paulo", "SP", new ZipCode("01000"));
        assertThrows(NullPointerException.class, () ->
            new BillingInfo(fullName, null, phone, address)
        );
    }

    @Test
    void shouldThrowExceptionWhenPhoneIsNull() {
        var fullName = new FullName("John", "Doe");
        var document = new Document("123");
        var address = new Address("Rua A", "123", "Próximo", "SP", "São Paulo", "SP", new ZipCode("01000"));
        assertThrows(NullPointerException.class, () ->
            new BillingInfo(fullName, document, null, address)
        );
    }

    @Test
    void shouldThrowExceptionWhenAddressIsNull() {
        var fullName = new FullName("John", "Doe");
        var document = new Document("123");
        var phone = new Phone("11");
        assertThrows(NullPointerException.class, () ->
            new BillingInfo(fullName, document, phone, null)
        );
    }

    @Test
    void shouldBeImmutable() {
        var fullName = new FullName("John", "Doe");
        var document = new Document("123");
        var phone = new Phone("11");
        var address = new Address("Rua A", "123", "Próximo", "SP", "São Paulo", "SP", new ZipCode("01000"));
        var billingInfo = new BillingInfo(fullName, document, phone, address);
        assertAll(
            () -> assertEquals("John Doe", billingInfo.fullName().toString()),
            () -> assertEquals("123", billingInfo.document().toString()),
            () -> assertEquals("11", billingInfo.phone().toString()),
            () -> assertEquals(address.toString(), billingInfo.address().toString())
        );
    }
}
