package com.algaworks.algashop.ordering.domain.entity;

import java.util.Arrays;
import java.util.List;

public enum OrderStatusEnum {
    DRAFT,
    PLACED(DRAFT),
    PAID(PLACED),
    READY(PAID),
    CANCELED(PAID, READY, PLACED, DRAFT);

    OrderStatusEnum(OrderStatusEnum...  previousStatuses) {
        this.previousStatuses = Arrays.asList(previousStatuses);
    }

    private final List<OrderStatusEnum> previousStatuses;

    public boolean canChangeTo(OrderStatusEnum newStatus) {
        var currentStatus = this;
        return newStatus.previousStatuses.contains(currentStatus);
    }

    public boolean canNotChangeTo(OrderStatusEnum newStatus) {
        return !canChangeTo(newStatus);
    }
}
