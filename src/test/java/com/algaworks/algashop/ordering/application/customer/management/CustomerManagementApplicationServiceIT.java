package com.algaworks.algashop.ordering.application.customer.management;

import com.algaworks.algashop.ordering.application.commons.AddressData;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDate;

@SpringBootTest
class CustomerManagementApplicationServiceIT {

    @Autowired
    private CustomerManagementApplicationService customerManagementApplicationService;

    @Test
    public void shouldRegister() {
        var input = CustomerInput.builder()
                .firstName("John")
                .lastName("Doe")
                .birthDate(LocalDate.of(1987, 11, 5))
                .document("255-08-0578")
                .phone("478-256-2604")
                .email("johndoe@email.com")
                .promotionNotificationsAllowed(false)
                .address(AddressData.builder()
                                 .street("Bourbon Street")
                                 .number("1200")
                                 .complement("Apt. 901")
                                 .neighborhood("North Ville")
                                 .city("YostFort")
                                 .state("South Carolina")
                                 .zipCode("70283")
                                 .build())
                .build();

        var customerId = customerManagementApplicationService.create(input);

        Assertions.assertThat(customerId).isNotNull();

        var customerOutput = customerManagementApplicationService.findById(customerId);

        Assertions.assertThat(customerOutput.getFirstName()).isEqualTo(input.getFirstName());
        Assertions.assertThat(customerOutput.getLastName()).isEqualTo(input.getLastName());
        Assertions.assertThat(customerOutput.getBirthDate()).isEqualTo(input.getBirthDate());
        Assertions.assertThat(customerOutput.getDocument()).isEqualTo(input.getDocument());
        Assertions.assertThat(customerOutput.getPhone()).isEqualTo(input.getPhone());
        Assertions.assertThat(customerOutput.getEmail()).isEqualTo(input.getEmail());
        Assertions.assertThat(customerOutput.getPromotionNotificationsAllowed())
                .isEqualTo(input.getPromotionNotificationsAllowed());

        Assertions.assertThat(customerOutput.getRegisteredAt()).isNotNull();

        Assertions.assertThat(customerOutput.getAddress()).isNotNull();
        Assertions.assertThat(customerOutput.getAddress().getStreet()).isEqualTo(input.getAddress().getStreet());
        Assertions.assertThat(customerOutput.getAddress().getNumber()).isEqualTo(input.getAddress().getNumber());
        Assertions.assertThat(customerOutput.getAddress().getComplement()).isEqualTo(input.getAddress().getComplement());
        Assertions.assertThat(customerOutput.getAddress().getNeighborhood()).isEqualTo(input.getAddress().getNeighborhood());
        Assertions.assertThat(customerOutput.getAddress().getCity()).isEqualTo(input.getAddress().getCity());
        Assertions.assertThat(customerOutput.getAddress().getState()).isEqualTo(input.getAddress().getState());
        Assertions.assertThat(customerOutput.getAddress().getZipCode()).isEqualTo(input.getAddress().getZipCode());
    }
}