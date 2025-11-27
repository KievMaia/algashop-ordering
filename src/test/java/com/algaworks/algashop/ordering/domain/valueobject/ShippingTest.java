package com.algaworks.algashop.ordering.domain.valueobject;

import com.algaworks.algashop.ordering.domain.entity.OrderTestDataBuilder;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ShippingTest {

    @Test
    void shouldCreateShippingInfoWithValidData() {
        var fullName = new FullName("Joe", "Doe");
        var document = new Document("225-09-1992");
        var phone = new Phone("123-111-9911");
        var address = OrderTestDataBuilder.anAddress();
        var shipping = OrderTestDataBuilder.aShipping();
        assertEquals(fullName, shipping.recipient().fullName());
        assertEquals(document, shipping.recipient().document());
        assertEquals(phone, shipping.recipient().phone());
        assertEquals(address, shipping.address());
    }

//    @Test
//    void shouldThrowExceptionWhenFullNameIsNull() {
//        var document = new Document("225-09-1992");
//        var phone = new Phone("123-111-9911");
//        var address = OrderTestDataBuilder.anAddress();
//        assertThrows(NullPointerException.class, () ->
//                new Shipping(null, document, phone, address)
//        );
//    }
//
//    @Test
//    void shouldThrowExceptionWhenDocumentIsNull() {
//        var fullName = new FullName("John", "Doe");
//        var phone = new Phone("11999999999");
//        var address = new Address("Rua A", "123", "Próximo", "SP", "São Paulo", "SP", new ZipCode("01000"));
//
//        assertThrows(NullPointerException.class, () ->
//                new Shipping(fullName, null, phone, address)
//        );
//    }
//
//    @Test
//    void shouldThrowExceptionWhenPhoneIsNull() {
//        var fullName = new FullName("John", "Doe");
//        var document = new Document("12345678900");
//        var address = new Address("Rua A", "123", "Próximo", "SP", "São Paulo", "SP", new ZipCode("01000"));
//        assertThrows(NullPointerException.class, () ->
//                new Shipping(fullName, document, null, address)
//        );
//    }
//
//    @Test
//    void shouldThrowExceptionWhenAddressIsNull() {
//        var fullName = new FullName("John", "Doe");
//        var document = new Document("123");
//        var phone = new Phone("11");
//        assertThrows(NullPointerException.class, () ->
//                new Shipping(fullName, document, phone, null)
//        );
//    }
//
//    @Test
//    void shouldBeImmutable() {
//        var fullName = new FullName("John", "Doe");
//        var document = new Document("123");
//        var phone = new Phone("11");
//        var address = new Address("Rua A", "123", "Próximo", "SP", "São Paulo", "SP", new ZipCode("01000"));
//        var shippingInfo = new Shipping(fullName, document, phone, address);
//        assertAll(
//                () -> assertEquals("John Doe", shippingInfo.fullName().toString()),
//                () -> assertEquals("123", shippingInfo.document().toString()),
//                () -> assertEquals("11", shippingInfo.phone().toString()),
//                () -> assertEquals(address.toString(), shippingInfo.address().toString())
//        );
//    }
}
