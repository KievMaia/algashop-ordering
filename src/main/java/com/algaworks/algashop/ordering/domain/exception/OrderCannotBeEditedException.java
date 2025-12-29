package com.algaworks.algashop.ordering.domain.exception;

import com.algaworks.algashop.ordering.domain.entity.OrderStatusEnum;
import com.algaworks.algashop.ordering.domain.valueobject.id.OrderId;

import static com.algaworks.algashop.ordering.domain.exception.ErrorMessages.ERROR_ORDER_CANNOT_BE_EDITED;

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
