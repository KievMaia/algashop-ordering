package com.algaworks.algashop.ordering.infrastructure.persistence.assembler;

import com.algaworks.algashop.ordering.domain.model.entity.ShoppingCart;
import com.algaworks.algashop.ordering.domain.model.entity.ShoppingCartItem;
import com.algaworks.algashop.ordering.infrastructure.persistence.entity.CustomerPersistenceEntity;
import com.algaworks.algashop.ordering.infrastructure.persistence.entity.ShoppingCartItemPersistenceEntity;
import com.algaworks.algashop.ordering.infrastructure.persistence.entity.ShoppingCartPersistenceEntity;
import com.algaworks.algashop.ordering.infrastructure.persistence.repository.CustomerPersistenceEntityRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Set;

@Component
@RequiredArgsConstructor
public class ShoppingCartPersistenceEntityAssembler {

    private final CustomerPersistenceEntityRepository customerPersistenceEntityRepository;

    public ShoppingCartPersistenceEntity fromDomain(ShoppingCart shoppingCart) {
        return this.merge(new ShoppingCartPersistenceEntity(), shoppingCart);
    }

    public ShoppingCartPersistenceEntity merge(ShoppingCartPersistenceEntity shoppingCartPersistenceEntity, ShoppingCart shoppingCart) {
        shoppingCartPersistenceEntity.setId(shoppingCart.id().value().toLong());

        var customerPersistenceEntity = customerPersistenceEntityRepository.getReferenceById(shoppingCart.customerId().value());
        shoppingCartPersistenceEntity.setCustomer(customerPersistenceEntity);

        shoppingCartPersistenceEntity.setTotalAmount(shoppingCart.totalAmount().value());
        shoppingCartPersistenceEntity.setTotalItems(shoppingCart.totalItems().value());
        shoppingCartPersistenceEntity.setCreatedAt(shoppingCart.createdAt());

        return  shoppingCartPersistenceEntity;
    }
}
