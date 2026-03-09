package com.algaworks.algashop.ordering.domain.model;

public interface AggregateRoot<ID> extends DomainEventsSource {
    ID id();
}
