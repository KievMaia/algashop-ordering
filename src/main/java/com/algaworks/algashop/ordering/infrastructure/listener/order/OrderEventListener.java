package com.algaworks.algashop.ordering.infrastructure.listener.order;

import com.algaworks.algashop.ordering.domain.model.order.OrderCanceledEvent;
import com.algaworks.algashop.ordering.domain.model.order.OrderPaidEvent;
import com.algaworks.algashop.ordering.domain.model.order.OrderPlacedEvent;
import com.algaworks.algashop.ordering.domain.model.order.OrderReadyEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class OrderEventListener {

    @EventListener
    public void listen(OrderPlacedEvent event) {
        log.info("OrderPlacedEvent received");
    }

    @EventListener
    public void listen(OrderPaidEvent event) {
        log.info("OrderPaidEvent received");
    }

    @EventListener
    public void listen(OrderReadyEvent event) {
        log.info("OrderReadyEvent received");
    }

    @EventListener
    public void listen(OrderCanceledEvent event) {
        log.info("OrderCanceledEvent received");
    }
}
