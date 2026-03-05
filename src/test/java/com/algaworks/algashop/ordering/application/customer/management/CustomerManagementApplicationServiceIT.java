package com.algaworks.algashop.ordering.application.customer.management;

import com.algaworks.algashop.ordering.application.commons.AddressData;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static com.algaworks.algashop.ordering.application.customer.management.CustomerInputTestDateBuilder.aCustomer;
import static com.algaworks.algashop.ordering.application.customer.management.CustomerUpdateInputTestDataBuilder.aCustomerUpdateInput;

@SpringBootTest
@Transactional
class CustomerManagementApplicationServiceIT {

    @Autowired
    private CustomerManagementApplicationService customerManagementApplicationService;

    @Test
    public void shouldRegister() {
        var input = aCustomer().build();

        var customerId = customerManagementApplicationService.create(input);

        Assertions.assertThat(customerId).isNotNull();

        var customerOutput = customerManagementApplicationService.findById(customerId);

        Assertions.assertThat(customerOutput)
                .extracting(
                        CustomerOutput::getId,
                        CustomerOutput::getFirstName,
                        CustomerOutput::getLastName,
                        CustomerOutput::getBirthDate,
                        CustomerOutput::getDocument,
                        CustomerOutput::getPhone,
                        CustomerOutput::getEmail,
                        CustomerOutput::getPromotionNotificationsAllowed
                ).containsExactly(
                        customerId,
                        input.getFirstName(),
                        input.getLastName(),
                        input.getBirthDate(),
                        input.getDocument(),
                        input.getPhone(),
                        input.getEmail(),
                        input.getPromotionNotificationsAllowed()
                );

        Assertions.assertThat(customerOutput.getAddress())
                .extracting(
                        AddressData::getStreet,
                        AddressData::getNumber,
                        AddressData::getComplement,
                        AddressData::getNeighborhood,
                        AddressData::getCity,
                        AddressData::getState,
                        AddressData::getZipCode
                ).containsExactly(
                        input.getAddress().getStreet(),
                        input.getAddress().getNumber(),
                        input.getAddress().getComplement(),
                        input.getAddress().getNeighborhood(),
                        input.getAddress().getCity(),
                        input.getAddress().getState(),
                        input.getAddress().getZipCode()
                );

        Assertions.assertThat(customerOutput.getRegisteredAt()).isNotNull();
    }

    @Test
    public void shouldUpdate() {
        var input = aCustomer().build();
        var customerUpdateInput = aCustomerUpdateInput().build();

        var customerId = customerManagementApplicationService.create(input);

        Assertions.assertThat(customerId).isNotNull();

        customerManagementApplicationService.update(customerId, customerUpdateInput);

        var customerOutput = customerManagementApplicationService.findById(customerId);

        Assertions.assertThat(customerOutput)
                .extracting(
                        CustomerOutput::getId,
                        CustomerOutput::getFirstName,
                        CustomerOutput::getLastName,
                        CustomerOutput::getPhone,
                        CustomerOutput::getPromotionNotificationsAllowed
                ).containsExactly(
                        customerId,
                        customerUpdateInput.getFirstName(),
                        customerUpdateInput.getLastName(),
                        customerUpdateInput.getPhone(),
                        customerUpdateInput.getPromotionNotificationsAllowed()
                );

        Assertions.assertThat(customerOutput.getAddress())
                .extracting(
                        AddressData::getStreet,
                        AddressData::getNumber,
                        AddressData::getComplement,
                        AddressData::getNeighborhood,
                        AddressData::getCity,
                        AddressData::getState,
                        AddressData::getZipCode
                ).containsExactly(
                        customerUpdateInput.getAddress().getStreet(),
                        customerUpdateInput.getAddress().getNumber(),
                        customerUpdateInput.getAddress().getComplement(),
                        customerUpdateInput.getAddress().getNeighborhood(),
                        customerUpdateInput.getAddress().getCity(),
                        customerUpdateInput.getAddress().getState(),
                        customerUpdateInput.getAddress().getZipCode()
                );

        Assertions.assertThat(customerOutput.getRegisteredAt()).isNotNull();
    }
}