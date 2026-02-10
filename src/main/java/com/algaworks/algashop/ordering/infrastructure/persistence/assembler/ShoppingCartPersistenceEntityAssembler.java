package com.algaworks.algashop.ordering.infrastructure.persistence.assembler;

import com.algaworks.algashop.ordering.domain.model.entity.ShoppingCart;
import com.algaworks.algashop.ordering.domain.model.entity.ShoppingCartItem;
import com.algaworks.algashop.ordering.infrastructure.persistence.entity.ShoppingCartItemPersistenceEntity;
import com.algaworks.algashop.ordering.infrastructure.persistence.entity.ShoppingCartPersistenceEntity;
import com.algaworks.algashop.ordering.infrastructure.persistence.repository.CustomerPersistenceEntityRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Set;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class ShoppingCartPersistenceEntityAssembler {

    private final CustomerPersistenceEntityRepository customerPersistenceEntityRepository;

    public ShoppingCartPersistenceEntity fromDomain(ShoppingCart shoppingCart) {
        return this.merge(new ShoppingCartPersistenceEntity(), shoppingCart);
    }

    public ShoppingCartPersistenceEntity merge(ShoppingCartPersistenceEntity shoppingCartPersistenceEntity, ShoppingCart shoppingCart) {
        shoppingCartPersistenceEntity.setId(shoppingCart.id().value());

        var customerPersistenceEntity = customerPersistenceEntityRepository.getReferenceById(shoppingCart.customerId().value());
        shoppingCartPersistenceEntity.setCustomer(customerPersistenceEntity);

        shoppingCartPersistenceEntity.setTotalAmount(shoppingCart.totalAmount().value());
        shoppingCartPersistenceEntity.setTotalItems(shoppingCart.totalItems().value());
        shoppingCartPersistenceEntity.setCreatedAt(shoppingCart.createdAt());
        shoppingCartPersistenceEntity.replaceItems(toOrderItemsEntities(shoppingCart.items()));
        return  shoppingCartPersistenceEntity;
    }

    private Set<ShoppingCartItemPersistenceEntity> toOrderItemsEntities(Set<ShoppingCartItem> source) {
        return source.stream().map(i -> this.mergeItem(new ShoppingCartItemPersistenceEntity(), i)).collect(Collectors.toSet());
    }

    private ShoppingCartItemPersistenceEntity mergeItem(ShoppingCartItemPersistenceEntity shoppingCartItemPersistenceEntity, ShoppingCartItem shoppingCartItem
    ) {
        shoppingCartItemPersistenceEntity.setId(shoppingCartItem.id().value());
        shoppingCartItemPersistenceEntity.setProductId(shoppingCartItem.productId().value());
        shoppingCartItemPersistenceEntity.setName(shoppingCartItem.name().value());
        shoppingCartItemPersistenceEntity.setPrice(shoppingCartItem.price().value());
        shoppingCartItemPersistenceEntity.setQuantity(shoppingCartItem.quantity().value());
        shoppingCartItemPersistenceEntity.setAvailable(shoppingCartItem.isAvailable());
        shoppingCartItemPersistenceEntity.setTotalAmount(shoppingCartItem.totalAmount().value());
        return shoppingCartItemPersistenceEntity;
    }
}
