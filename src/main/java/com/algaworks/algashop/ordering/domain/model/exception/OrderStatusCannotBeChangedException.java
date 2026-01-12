package com.algaworks.algashop.ordering.domain.model.exception;

import com.algaworks.algashop.ordering.domain.model.entity.OrderStatusEnum;
import com.algaworks.algashop.ordering.domain.model.valueobject.id.OrderId;

import static com.algaworks.algashop.ordering.domain.model.exception.ErrorMessages.ERROR_ORDER_STATUS_CANNOT_BE_CHANGED;

public class OrderStatusCannotBeChangedException extends DomainException {
    public OrderStatusCannotBeChangedException(OrderId id, OrderStatusEnum status, OrderStatusEnum newStatus) {
        super(String.format(ERROR_ORDER_STATUS_CANNOT_BE_CHANGED, id, status, newStatus));
    }
}
