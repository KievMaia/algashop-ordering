package com.algaworks.algashop.ordering.infrastructure.persistence.shoppingcart;

import com.algaworks.algashop.ordering.domain.model.shoppingcart.ShoppingCart;
import com.algaworks.algashop.ordering.domain.model.shoppingcart.ShoppingCarts;
import com.algaworks.algashop.ordering.domain.model.customer.CustomerId;
import com.algaworks.algashop.ordering.domain.model.shoppingcart.ShoppingCartId;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ReflectionUtils;

import java.util.Optional;

@Component
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ShoppingCartsPersistenceProvider implements ShoppingCarts {

    private final ShoppingCartPersistenceEntityRepository persistenceRepository;
    private final ShoppingCartPersistenceEntityAssembler assembler;
    private final ShoppingCartPersistenceEntityDisassembler disassembler;

    private final EntityManager entityManager;

    @Override
    public Optional<ShoppingCart> ofCustomer(CustomerId customerId) {
        return persistenceRepository.findByCustomer_Id(customerId.value()).map(disassembler::toDomainEntity);
    }

    @Override
    public void remove(ShoppingCart shoppingCart) {
        var shoppingCartPersistenceEntity = assembler.fromDomain(shoppingCart);
        persistenceRepository.delete(shoppingCartPersistenceEntity);
    }

    @Override
    public void remove(ShoppingCartId shoppingCartId) {
        persistenceRepository.deleteById(shoppingCartId.value());
    }

    @Override
    public Optional<ShoppingCart> ofId(ShoppingCartId shoppingCartId) {
        return persistenceRepository.findById(shoppingCartId.value())
                .map(disassembler::toDomainEntity);
    }

    @Override
    public boolean exists(ShoppingCartId shoppingCartId) {
        return persistenceRepository.existsById(shoppingCartId.value());
    }

    @Override
    public long count() {
        return persistenceRepository.count();
    }

    @Override
    @Transactional(readOnly = false)
    public void add(ShoppingCart aggregateRoot) {
        var shoppingCartId = aggregateRoot.id().value();
        persistenceRepository.findById(shoppingCartId).ifPresentOrElse(
                (persistenceEntity) -> this.update(aggregateRoot, persistenceEntity),
                () -> this.insert(aggregateRoot)
        );
    }

    private void insert(ShoppingCart aggregateRoot) {
        var persistenceEntity = assembler.fromDomain(aggregateRoot);
        persistenceRepository.saveAndFlush(persistenceEntity);
        this.updateVersion(aggregateRoot, persistenceEntity);
    }

    private void update(ShoppingCart aggregateRoot, ShoppingCartPersistenceEntity persistenceEntity) {
        persistenceEntity = assembler.merge(persistenceEntity, aggregateRoot);
        entityManager.detach(persistenceEntity);
        persistenceRepository.saveAndFlush(persistenceEntity);
        this.updateVersion(aggregateRoot, persistenceEntity);
    }

    @SneakyThrows
    private void updateVersion(ShoppingCart aggregateRoot, ShoppingCartPersistenceEntity persistenceEntity) {
        var version = aggregateRoot.getClass().getDeclaredField("version");
        version.setAccessible(true);
        ReflectionUtils.setField(version, aggregateRoot, persistenceEntity.getVersion());
        version.setAccessible(false);

    }
}
