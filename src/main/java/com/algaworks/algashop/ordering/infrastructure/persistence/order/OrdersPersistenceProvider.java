package com.algaworks.algashop.ordering.infrastructure.persistence.order;

import com.algaworks.algashop.ordering.domain.model.order.Order;
import com.algaworks.algashop.ordering.domain.model.order.Orders;
import com.algaworks.algashop.ordering.domain.model.commons.Money;
import com.algaworks.algashop.ordering.domain.model.customer.CustomerId;
import com.algaworks.algashop.ordering.domain.model.order.OrderId;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ReflectionUtils;

import java.time.Year;
import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class OrdersPersistenceProvider implements Orders {

    private final OrderPersistenceEntityRepository persistenceRepository;
    private final OrderPersistenceEntityAssembler assembler;
    private final OrderPersistenceEntityDisassembler disassembler;

    private final EntityManager entityManager;

    @Override
    public Optional<Order> ofId(OrderId orderId) {
        var possibleEntity = persistenceRepository.findById(orderId.value().toLong());
        return possibleEntity.map(disassembler::toDomainEntity);
    }

    @Override
    public boolean exists(OrderId orderId) {
        return persistenceRepository.existsById(orderId.value().toLong());
    }

    @Override
    public long count() {
        return persistenceRepository.count();
    }

    @Override
    @Transactional(readOnly = false)
    public void add(Order aggregateRoot) {
        var orderId = aggregateRoot.id().value().toLong();
        persistenceRepository.findById(orderId).ifPresentOrElse(
                (persistenceEntity) -> this.update(aggregateRoot, persistenceEntity),
                () -> this.insert(aggregateRoot)
        );
    }

    @Override
    public List<Order> placedByCustomerYear(CustomerId customerId, Year year) {
        var entities = persistenceRepository.placedByCustomerInYear(
                customerId.value(),
                year.getValue()
        );

        return entities.stream().map(disassembler::toDomainEntity).toList();
    }

    @Override
    public long salesQuantityByCustomerInYear(CustomerId customerId, Year year) {
        return persistenceRepository.salesQuantityByCustomerInYear(customerId.value(), year.getValue());
    }

    @Override
    public Money totalSoldForCustomer(CustomerId customerId) {
        return new Money(persistenceRepository.totalSoldForCustomer(customerId.value()));
    }

    private void insert(Order aggregateRoot) {
        var persistenceEntity = assembler.fromDomain(aggregateRoot);
        persistenceRepository.saveAndFlush(persistenceEntity);
        updateVersion(aggregateRoot, persistenceEntity);
    }

    private void update(Order aggregateRoot, OrderPersistenceEntity persistenceEntity) {
        persistenceEntity = assembler.merge(persistenceEntity, aggregateRoot);
        entityManager.detach(persistenceEntity);
        persistenceEntity = persistenceRepository.saveAndFlush(persistenceEntity);
        updateVersion(aggregateRoot, persistenceEntity);
    }

    private void updateVersion(Order aggregateRoot, OrderPersistenceEntity persistenceEntity) {
        try {
            var version = aggregateRoot.getClass().getDeclaredField("version");
            version.setAccessible(true);
            ReflectionUtils.setField(version, aggregateRoot, persistenceEntity.getVersion());
            version.setAccessible(false);
        } catch (NoSuchFieldException e) {
            throw new RuntimeException(e);
        }
    }
}
