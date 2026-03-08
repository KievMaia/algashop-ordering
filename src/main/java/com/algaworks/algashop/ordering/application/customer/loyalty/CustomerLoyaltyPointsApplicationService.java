package com.algaworks.algashop.ordering.application.customer.loyalty;

import com.algaworks.algashop.ordering.domain.model.customer.*;
import com.algaworks.algashop.ordering.domain.model.order.OrderId;
import com.algaworks.algashop.ordering.domain.model.order.OrderNotFoundException;
import com.algaworks.algashop.ordering.domain.model.order.Orders;
import io.hypersistence.tsid.TSID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CustomerLoyaltyPointsApplicationService {

    private final CustomerLoyaltyPointService customerLoyaltyPointService;

    private final Customers customers;
    private final Orders orders;

    @Transactional
    public void addLoyaltyPoints(UUID rawCustomerId, TSID rawOrderId) {
        Objects.requireNonNull(rawCustomerId);
        Objects.requireNonNull(rawOrderId);

        var customer =
                customers.ofId(new CustomerId(rawCustomerId)).orElseThrow(CustomerNotFoundException::new);

        var order = orders.ofId(new OrderId(rawOrderId)).orElseThrow(OrderNotFoundException::new);

        customerLoyaltyPointService.addPoints(customer, order);

        customers.add(customer);
    }

}
