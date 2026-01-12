package com.algaworks.algashop.ordering.domain.model.repository;

import com.algaworks.algashop.ordering.domain.model.entity.AggregateRoot;

public interface RemoveCapebleRepository<T extends AggregateRoot<ID>, ID> extends Repository<T, ID> {

    void remove(T t);
    void remove(ID id);
}
