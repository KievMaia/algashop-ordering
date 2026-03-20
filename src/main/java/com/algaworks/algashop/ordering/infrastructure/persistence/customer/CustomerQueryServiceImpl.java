package com.algaworks.algashop.ordering.infrastructure.persistence.customer;

import com.algaworks.algashop.ordering.application.customer.query.CustomerFilter;
import com.algaworks.algashop.ordering.application.customer.query.CustomerOutput;
import com.algaworks.algashop.ordering.application.customer.query.CustomerQueryService;
import com.algaworks.algashop.ordering.application.customer.query.CustomerSummaryOutput;
import com.algaworks.algashop.ordering.application.utility.Mapper;
import com.algaworks.algashop.ordering.domain.model.customer.CustomerNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class CustomerQueryServiceImpl implements CustomerQueryService {

    private final CustomerPersistenceEntityRepository repository;
    private final Mapper mapper;

    @Override
    public CustomerOutput findById(UUID customerId) {
        return repository.findByIdAsOutput(customerId).orElseThrow(CustomerNotFoundException::new);
    }

    @Override
    public Page<CustomerSummaryOutput> filter(CustomerFilter filter) {
        var pageable = PageRequest.of(
                filter.getPage(),
                filter.getSize(),
                Sort.by(
                        filter.getSortDirectionOrDefault(),
                        filter.getSortByPropertyOrDefault().getPropertyName()
                )
        );

        var page = repository.filter(
                filter.getEmail(),
                filter.getFirstName(),
                pageable
        );

        return page.map(entity -> mapper.convert(entity, CustomerSummaryOutput.class));
    }
}
