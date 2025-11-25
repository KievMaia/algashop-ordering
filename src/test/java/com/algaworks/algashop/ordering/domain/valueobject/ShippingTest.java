package com.algaworks.algashop.ordering.domain.valueobject;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ShippingTest {

    @Test
    void shouldCreateShippingInfoWithValidData() {
        var fullName = new FullName("John", "Doe");
        var document = new Document("12345678900");
        var phone = new Phone("11999999999");
        var address = new Address("Rua A", "123", "Próximo", "SP", "São Paulo", "SP", new ZipCode("01000"));
        var shippingInfo = new Shipping(fullName, document, phone, address);
        assertEquals(fullName, shippingInfo.fullName());
        assertEquals(document, shippingInfo.document());
        assertEquals(phone, shippingInfo.phone());
        assertEquals(address, shippingInfo.address());
    }

    @Test
    void shouldThrowExceptionWhenFullNameIsNull() {
        var document = new Document("12345678900");
        var phone = new Phone("11999999999");
        var address = new Address("Rua A", "123", "Próximo", "SP", "São Paulo", "SP", new ZipCode("01000"));
        assertThrows(NullPointerException.class, () ->
            new Shipping(null, document, phone, address)
        );
    }

    @Test
    void shouldThrowExceptionWhenDocumentIsNull() {
        var fullName = new FullName("John", "Doe");
        var phone = new Phone("11999999999");
        var address = new Address("Rua A", "123", "Próximo", "SP", "São Paulo", "SP", new ZipCode("01000"));

        assertThrows(NullPointerException.class, () ->
            new Shipping(fullName, null, phone, address)
        );
    }

    @Test
    void shouldThrowExceptionWhenPhoneIsNull() {
        var fullName = new FullName("John", "Doe");
        var document = new Document("12345678900");
        var address = new Address("Rua A", "123", "Próximo", "SP", "São Paulo", "SP", new ZipCode("01000"));
        assertThrows(NullPointerException.class, () ->
            new Shipping(fullName, document, null, address)
        );
    }

    @Test
    void shouldThrowExceptionWhenAddressIsNull() {
        var fullName = new FullName("John", "Doe");
        var document = new Document("123");
        var phone = new Phone("11");
        assertThrows(NullPointerException.class, () ->
            new Shipping(fullName, document, phone, null)
        );
    }

    @Test
    void shouldBeImmutable() {
        var fullName = new FullName("John", "Doe");
        var document = new Document("123");
        var phone = new Phone("11");
        var address = new Address("Rua A", "123", "Próximo", "SP", "São Paulo", "SP", new ZipCode("01000"));
        var shippingInfo = new Shipping(fullName, document, phone, address);
        assertAll(
            () -> assertEquals("John Doe", shippingInfo.fullName().toString()),
            () -> assertEquals("123", shippingInfo.document().toString()),
            () -> assertEquals("11", shippingInfo.phone().toString()),
            () -> assertEquals(address.toString(), shippingInfo.address().toString())
        );
    }
}
