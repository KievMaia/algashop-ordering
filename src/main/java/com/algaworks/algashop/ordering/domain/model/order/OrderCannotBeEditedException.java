package com.algaworks.algashop.ordering.domain.model.order;

import com.algaworks.algashop.ordering.domain.model.DomainException;

import static com.algaworks.algashop.ordering.domain.model.ErrorMessages.ERROR_ORDER_CANNOT_BE_EDITED;

public class OrderCannotBeEditedException extends DomainException {
    private OrderCannotBeEditedException(String message) {
        super(message);
    }

    public static OrderCannotBeEditedException statusDifferentOfDraft(OrderId id, OrderStatusEnum status) {
        return new OrderCannotBeEditedException(
                String.format(ERROR_ORDER_CANNOT_BE_EDITED, id.value(), status.name())
        );
    }
}
