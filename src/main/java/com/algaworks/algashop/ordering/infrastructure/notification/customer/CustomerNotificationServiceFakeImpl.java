package com.algaworks.algashop.ordering.infrastructure.notification.customer;

import com.algaworks.algashop.ordering.domain.model.customer.CustomerId;
import com.algaworks.algashop.ordering.domain.model.customer.CustomerNotFoundException;
import com.algaworks.algashop.ordering.domain.model.customer.Customers;
import com.algaworks.algashop.ordering.domain.model.customer.notification.CustomerNotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class CustomerNotificationServiceFakeImpl implements CustomerNotificationService {

    private final Customers customers;

    @Override
    public void notifyNewRegistration(final UUID customerId) {
        var customer =
                customers.ofId(new CustomerId(customerId)).orElseThrow(CustomerNotFoundException::new);

        log.info("Welcome {}", customer.fullName().firstName());
        log.info("User, your email to access your account {}", customer.email());
    }
}
