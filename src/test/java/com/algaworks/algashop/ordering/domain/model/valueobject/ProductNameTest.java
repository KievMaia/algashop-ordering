package com.algaworks.algashop.ordering.domain.model.valueobject;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ProductNameTest {

    @Test
    void shouldCreateProductNameWithValidValue() {
        var productName = new ProductName("TV Smart");
        assertEquals("TV Smart", productName.name());
        assertEquals("TV Smart", productName.toString());
    }

    @Test
    void shouldTrimValueWhenCreatingProductName() {
        var productName = new ProductName("  Geladeira  ");
        assertEquals("Geladeira", productName.name());
        assertEquals("Geladeira", productName.toString());
    }

    @Test
    void shouldThrowExceptionWhenNameIsNull() {
        assertThrows(NullPointerException.class, () -> new ProductName(null));
    }

    @Test
    void shouldThrowExceptionWhenNameIsBlank() {
        assertThrows(IllegalArgumentException.class, () -> new ProductName(""));
        assertThrows(IllegalArgumentException.class, () -> new ProductName("   "));
        assertThrows(IllegalArgumentException.class, () -> new ProductName("\n\t"));
    }

    @Test
    void toStringShouldReturnName() {
        var productName = new ProductName("Notebook");
        var result = productName.toString();
        assertEquals("Notebook", result);
    }
}
