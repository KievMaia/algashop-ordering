package com.algaworks.algashop.ordering.domain.model.customer.notification;

import java.util.UUID;

public interface CustomerNotificationService {
    void notifyNewRegistration(UUID customerId);
}
