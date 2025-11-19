package com.algaworks.algashop.ordering.domain.entity;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

class OrderStatusEnumTest {

    @Test
    public void canChangeTo() {
        Assertions.assertThat(OrderStatusEnum.DRAFT.canChangeTo(OrderStatusEnum.PLACED)).isTrue();
        Assertions.assertThat(OrderStatusEnum.DRAFT.canChangeTo(OrderStatusEnum.CANCELED)).isTrue();
        Assertions.assertThat(OrderStatusEnum.DRAFT.canChangeTo(OrderStatusEnum.PAID)).isFalse();
        Assertions.assertThat(OrderStatusEnum.DRAFT.canChangeTo(OrderStatusEnum.READY)).isFalse();

        Assertions.assertThat(OrderStatusEnum.PLACED.canChangeTo(OrderStatusEnum.PAID)).isTrue();
        Assertions.assertThat(OrderStatusEnum.PLACED.canChangeTo(OrderStatusEnum.CANCELED)).isTrue();
        Assertions.assertThat(OrderStatusEnum.PLACED.canChangeTo(OrderStatusEnum.DRAFT)).isFalse();
        Assertions.assertThat(OrderStatusEnum.PLACED.canChangeTo(OrderStatusEnum.READY)).isFalse();

        Assertions.assertThat(OrderStatusEnum.PAID.canChangeTo(OrderStatusEnum.READY)).isTrue();
        Assertions.assertThat(OrderStatusEnum.PAID.canChangeTo(OrderStatusEnum.CANCELED)).isTrue();
        Assertions.assertThat(OrderStatusEnum.PAID.canChangeTo(OrderStatusEnum.PLACED)).isFalse();
        Assertions.assertThat(OrderStatusEnum.PAID.canChangeTo(OrderStatusEnum.DRAFT)).isFalse();

        Assertions.assertThat(OrderStatusEnum.READY.canChangeTo(OrderStatusEnum.CANCELED)).isTrue();
        Assertions.assertThat(OrderStatusEnum.READY.canChangeTo(OrderStatusEnum.DRAFT)).isFalse();
        Assertions.assertThat(OrderStatusEnum.READY.canChangeTo(OrderStatusEnum.PLACED)).isFalse();
        Assertions.assertThat(OrderStatusEnum.READY.canChangeTo(OrderStatusEnum.PAID)).isFalse();

        Assertions.assertThat(OrderStatusEnum.CANCELED.canChangeTo(OrderStatusEnum.PAID)).isFalse();
        Assertions.assertThat(OrderStatusEnum.CANCELED.canChangeTo(OrderStatusEnum.READY)).isFalse();
        Assertions.assertThat(OrderStatusEnum.CANCELED.canChangeTo(OrderStatusEnum.PLACED)).isFalse();
        Assertions.assertThat(OrderStatusEnum.CANCELED.canChangeTo(OrderStatusEnum.DRAFT)).isFalse();
    }

    @Test
    public void canNotChangeTo() {
        Assertions.assertThat(OrderStatusEnum.DRAFT.canNotChangeTo(OrderStatusEnum.PAID)).isTrue();
        Assertions.assertThat(OrderStatusEnum.DRAFT.canNotChangeTo(OrderStatusEnum.READY)).isTrue();

        Assertions.assertThat(OrderStatusEnum.PLACED.canNotChangeTo(OrderStatusEnum.DRAFT)).isTrue();
        Assertions.assertThat(OrderStatusEnum.PLACED.canNotChangeTo(OrderStatusEnum.READY)).isTrue();

        Assertions.assertThat(OrderStatusEnum.PAID.canNotChangeTo(OrderStatusEnum.PLACED)).isTrue();
        Assertions.assertThat(OrderStatusEnum.PAID.canNotChangeTo(OrderStatusEnum.DRAFT)).isTrue();

        Assertions.assertThat(OrderStatusEnum.READY.canNotChangeTo(OrderStatusEnum.DRAFT)).isTrue();
        Assertions.assertThat(OrderStatusEnum.READY.canNotChangeTo(OrderStatusEnum.PLACED)).isTrue();
        Assertions.assertThat(OrderStatusEnum.READY.canNotChangeTo(OrderStatusEnum.PAID)).isTrue();

        Assertions.assertThat(OrderStatusEnum.CANCELED.canNotChangeTo(OrderStatusEnum.PAID)).isTrue();
        Assertions.assertThat(OrderStatusEnum.CANCELED.canNotChangeTo(OrderStatusEnum.READY)).isTrue();
        Assertions.assertThat(OrderStatusEnum.CANCELED.canNotChangeTo(OrderStatusEnum.PLACED)).isTrue();
        Assertions.assertThat(OrderStatusEnum.CANCELED.canNotChangeTo(OrderStatusEnum.DRAFT)).isTrue();
    }
}