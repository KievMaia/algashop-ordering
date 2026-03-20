package com.algaworks.algashop.ordering.infrastructure.persistence.shoppingcart;

import com.algaworks.algashop.ordering.application.shoppingcart.ShoppingCartNotFoundException;
import com.algaworks.algashop.ordering.application.shoppingcart.query.ShoppingCartOutput;
import com.algaworks.algashop.ordering.application.shoppingcart.query.ShoppingCartQueryService;
import com.algaworks.algashop.ordering.application.utility.Mapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Component
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ShoppingCartQueryServiceImpl implements ShoppingCartQueryService {

    private final ShoppingCartPersistenceEntityRepository repository;
    private final Mapper mapper;

    @Override
    public ShoppingCartOutput findById(UUID shoppingCartId) {
        var entity = repository.findById(shoppingCartId).orElseThrow(ShoppingCartNotFoundException::new);
        return mapper.convert(entity, ShoppingCartOutput.class);
    }

    @Override
    public ShoppingCartOutput findByCustomerId(UUID customerId) {
        var entity = repository.findByCustomer_Id(customerId).orElseThrow(ShoppingCartNotFoundException::new);
        return mapper.convert(entity, ShoppingCartOutput.class);
    }
}
