package com.algaworks.algashop.ordering.domain.model.valueobject;

import com.algaworks.algashop.ordering.domain.model.validator.FieldValidations;

public record Email(String value) {
    public Email {
        FieldValidations.requiresValidEmail(value);
        value = value.trim();
    }

    @Override
    public String toString() {
        return value;
    }
}
