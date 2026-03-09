package com.algaworks.algashop.ordering.infrastructure.listener.customer;

import com.algaworks.algashop.ordering.domain.model.customer.CustomerArchivedEvent;
import com.algaworks.algashop.ordering.domain.model.customer.CustomerRegisteredEvent;
import com.algaworks.algashop.ordering.domain.model.customer.notification.CustomerNotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class CustomerEventListener {

    private final CustomerNotificationService customerNotificationService;

    @EventListener
    public void listen(CustomerRegisteredEvent event) {
        log.info("CustomerRegisteredEvent listen 1");
        var input = new CustomerNotificationService.NotifyNewRegistrationInput(
                event.customerId().value(),
                event.fullName().firstName(),
                event.email().value()
        );
        customerNotificationService.notifyNewRegistration(input);
    }

    @EventListener
    public void listen(CustomerArchivedEvent event) {
        log.info("CustomerArchivedEvent listen 1");
    }
}
