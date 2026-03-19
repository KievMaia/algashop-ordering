package com.algaworks.algashop.ordering.application.order.management;

import com.algaworks.algashop.ordering.domain.model.order.OrderId;
import com.algaworks.algashop.ordering.domain.model.order.OrderNotFoundException;
import com.algaworks.algashop.ordering.domain.model.order.Orders;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class OrderManagementApplicationService {

    private final Orders orders;

    public void cancel(Long rawOrderId) {
        var order = orders.ofId(new OrderId(rawOrderId)).orElseThrow(OrderNotFoundException::new);

        order.markAsCancelled();

        orders.add(order);
    }

    public void markAsPaid(Long rawOrderId) {
        var order = orders.ofId(new OrderId(rawOrderId)).orElseThrow(OrderNotFoundException::new);

        order.markAsPaid();

        orders.add(order);
    }

    public void markAsReady(Long rawOrderId) {
        var order = orders.ofId(new OrderId(rawOrderId)).orElseThrow(OrderNotFoundException::new);

        order.markAsReady();

        orders.add(order);
    }
}
