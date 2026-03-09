package com.algaworks.algashop.ordering.application.customer.management;

import com.algaworks.algashop.ordering.application.commons.AddressData;
import com.algaworks.algashop.ordering.domain.model.commons.Email;
import com.algaworks.algashop.ordering.domain.model.customer.*;
import com.algaworks.algashop.ordering.domain.model.customer.notification.CustomerNotificationService;
import com.algaworks.algashop.ordering.infrastructure.listener.customer.CustomerEventListener;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

import static com.algaworks.algashop.ordering.application.customer.management.CustomerInputTestDateBuilder.aCustomer;
import static com.algaworks.algashop.ordering.application.customer.management.CustomerUpdateInputTestDataBuilder.aCustomerUpdateInput;

@SpringBootTest
@Transactional
class CustomerManagementApplicationServiceIT {

    @Autowired
    private CustomerManagementApplicationService customerManagementApplicationService;

    @MockitoSpyBean
    private CustomerEventListener customerEventListener;

    @MockitoSpyBean
    private CustomerNotificationService  customerNotificationService;

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

        Mockito.verify(customerEventListener).listen(Mockito.any(CustomerRegisteredEvent.class));
        Mockito.verify(customerEventListener, Mockito.never()).listen(Mockito.any(CustomerArchivedEvent.class));
        Mockito.verify(customerNotificationService).notifyNewRegistration(Mockito.any(CustomerNotificationService.NotifyNewRegistrationInput.class));
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

    @Test
    public void shouldArchive() {
        var customerInput = aCustomer().build();
        var customerId = customerManagementApplicationService.create(customerInput);

        customerManagementApplicationService.archive(customerId);

        var customerOutput = customerManagementApplicationService.findById(customerId);

        Assertions.assertThat(customerOutput.getArchived()).isTrue();
        Assertions.assertThat(customerOutput.getArchivedAt()).isNotNull();

        Assertions.assertThat(customerOutput.getFirstName()).isEqualTo("Anonymous");
        Assertions.assertThat(customerOutput.getLastName()).isEqualTo("Anonymous");
        Assertions.assertThat(customerOutput.getPhone()).isEqualTo("000-000-0000");
        Assertions.assertThat(customerOutput.getDocument()).isEqualTo("000-00-0000");
        Assertions.assertThat(customerOutput.getEmail()).contains("@anonymous.com");
        Assertions.assertThat(customerOutput.getBirthDate()).isNull();
        Assertions.assertThat(customerOutput.getPromotionNotificationsAllowed()).isFalse();
        Assertions.assertThat(customerOutput.getAddress().getNumber()).isEqualTo("Anonymized");
        Assertions.assertThat(customerOutput.getAddress().getComplement()).isNull();
    }

    @Test
    public void shouldChangeEmail() {
        var customerInput = aCustomer().build();
        var customerId = customerManagementApplicationService.create(customerInput);
        var newEmail = "novoemail@email.com";

        customerManagementApplicationService.changeEmail(customerId, new Email(newEmail));

        var customerOutput = customerManagementApplicationService.findById(customerId);

        Assertions.assertThat(customerOutput.getEmail()).isEqualTo(newEmail);
    }

    @Test
    public void shouldThrowExceptionChangeEmailInexistent() {
        var customerId = UUID.randomUUID();
        var newEmail = "novoemail@email.com";

        Assertions.assertThatThrownBy(
                        () -> customerManagementApplicationService.changeEmail(customerId,
                                                                               new Email(newEmail)))
                .isInstanceOf(CustomerNotFoundException.class);
    }

    @Test
    public void shouldThrowExceptionWhenChangeEmailToArchivedCustomer() {
        var customerInput = aCustomer().build();
        var customerId = customerManagementApplicationService.create(customerInput);
        var newEmail = "novoemail@email.com";

        customerManagementApplicationService.archive(customerId);

        Assertions.assertThatThrownBy(
                        () -> customerManagementApplicationService.changeEmail(customerId,
                                                                               new Email(newEmail)))
                .isInstanceOf(CustomerArchivedException.class);
    }

    @Test
    public void shouldThrowExceptionWhenChangeInvalidEmail() {
        var customerInput = aCustomer().build();
        var customerId = customerManagementApplicationService.create(customerInput);
        var newEmail = "novoemailemail.com";

        customerManagementApplicationService.archive(customerId);

        Assertions.assertThatThrownBy(
                        () -> customerManagementApplicationService.changeEmail(customerId,
                                                                               new Email(newEmail)))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    public void shouldThrowExceptionWhenChangeEmailToExistingCustomerEmail() {
        var customerInput1 = aCustomer().build();
        var customerInput2 = aCustomer().email("johndoe2@email.com").build();
        var customerId1 = customerManagementApplicationService.create(customerInput1);
        customerManagementApplicationService.create(customerInput2);

        Assertions.assertThatThrownBy(
                        () -> customerManagementApplicationService.changeEmail(customerId1,
                                                                               new Email(customerInput2.getEmail())))
                .isInstanceOf(CustomerEmailIsInUseException.class);
    }
}