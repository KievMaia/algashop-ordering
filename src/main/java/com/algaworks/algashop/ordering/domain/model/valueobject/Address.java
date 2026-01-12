package com.algaworks.algashop.ordering.domain.model.valueobject;

import com.algaworks.algashop.ordering.domain.model.validator.FieldValidations;
import lombok.Builder;

import java.util.Objects;

@Builder(toBuilder = true)
public record Address(
        String street,
        String complement,
        String neighborhood,
        String number,
        String city,
        String state,
        ZipCode zipCode
){
    public Address {
        FieldValidations.requiresNonBlank(street);
        FieldValidations.requiresNonBlank(neighborhood);
        FieldValidations.requiresNonBlank(city);
        FieldValidations.requiresNonBlank(number);
        FieldValidations.requiresNonBlank(state);
        Objects.requireNonNull(zipCode);
    }
}
