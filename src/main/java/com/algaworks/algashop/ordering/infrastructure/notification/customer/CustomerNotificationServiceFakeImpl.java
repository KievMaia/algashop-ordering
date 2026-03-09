package com.algaworks.algashop.ordering.infrastructure.notification.customer;

import com.algaworks.algashop.ordering.domain.model.customer.notification.CustomerNotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class CustomerNotificationServiceFakeImpl implements CustomerNotificationService {

    @Override
    public void notifyNewRegistration(final NotifyNewRegistrationInput input) {
        log.info("Welcome {}", input.firstName());
        log.info("User, your email to access your account {}", input.email());
    }
}
