package com.algaworks.algashop.ordering.domain.model;

import java.util.List;

public interface DomainEventsSource {
    List<Object> domainEvents();
    void clearDomainEvents();
}
