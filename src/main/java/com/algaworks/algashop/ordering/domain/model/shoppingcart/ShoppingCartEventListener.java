package com.algaworks.algashop.ordering.domain.model.shoppingcart;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class ShoppingCartEventListener {

    @EventListener
    public void listen(ShoppingCartCreatedEvent event) {
        log.info("ShoppingCartCreatedEvent listen");
    }

    @EventListener
    public void listen(ShoppingCartEmptiedEvent event) {
        log.info("ShoppingCartEmptiedEvent listen");
    }

    @EventListener
    public void listen(ShoppingCartItemAddedEvent event) {
        log.info("ShoppingCartItemAddedEvent listen");
    }

    @EventListener
    public void listen(ShoppingCartItemRemovedEvent event) {
        log.info("ShoppingCartItemRemovedEvent listen");
    }
}
