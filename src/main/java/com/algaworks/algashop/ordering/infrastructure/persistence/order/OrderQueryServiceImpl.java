package com.algaworks.algashop.ordering.infrastructure.persistence.order;

import com.algaworks.algashop.ordering.application.order.query.*;
import com.algaworks.algashop.ordering.application.utility.Mapper;
import com.algaworks.algashop.ordering.domain.model.order.OrderId;
import com.algaworks.algashop.ordering.domain.model.order.OrderNotFoundException;
import jakarta.persistence.EntityManager;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Order;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class OrderQueryServiceImpl implements OrderQueryService {

    private final OrderPersistenceEntityRepository repository;
    private final Mapper mapper;

    private final EntityManager entityManager;

    @Override
    public OrderDetailOutput findById(String id) {
        var orderPersistenceEntity = repository.findById(new OrderId(id).value().toLong())
                .orElseThrow(OrderNotFoundException::new);
        return mapper.convert(orderPersistenceEntity, OrderDetailOutput.class);
    }

    @Override
    public Page<OrderSummaryOutput> filter(OrderFilter filter) {
        var totalQueryResults = countTotalQueryResults(filter);

        if (totalQueryResults.equals(0L)) {
            var pageRequest = PageRequest.of(filter.getPage(), filter.getSize());
            return new PageImpl<>(new ArrayList<>(), pageRequest, totalQueryResults);
        }

        return filterQuery(filter, totalQueryResults);
    }

    private Long countTotalQueryResults(OrderFilter filter) {
        var builder = entityManager.getCriteriaBuilder();
        var criteriaQuery = builder.createQuery(Long.class);
        var root = criteriaQuery.from(OrderPersistenceEntity.class);

        var count = builder.count(root);
        final Predicate[] predicates = toPredicates(builder, root, filter);

        criteriaQuery.select(count);
        criteriaQuery.where(predicates);

        var query = entityManager.createQuery(criteriaQuery);

        return query.getSingleResult();
    }

    private Page<OrderSummaryOutput> filterQuery(OrderFilter filter, Long totalQueryResults) {
        var builder = entityManager.getCriteriaBuilder();
        var criteriaQuery = builder.createQuery(OrderSummaryOutput.class);
        var root = criteriaQuery.from(OrderPersistenceEntity.class);

        var customer = root.get("customer");

        criteriaQuery.select(
                builder.construct(OrderSummaryOutput.class,
                                  root.get("id"),
                                  root.get("totalItems"),
                                  root.get("totalAmount"),
                                  root.get("placedAt"),
                                  root.get("paidAt"),
                                  root.get("canceledAt"),
                                  root.get("readyAt"),
                                  root.get("status"),
                                  root.get("paymentMethod"),
                                  builder.construct(CustomerMinimalOutput.class,
                                                    customer.get("id"),
                                                    customer.get("firstName"),
                                                    customer.get("lastName"),
                                                    customer.get("email"),
                                                    customer.get("document"),
                                                    customer.get("phone")
                                  )
                )

        );

        final Predicate[] predicates = toPredicates(builder, root, filter);
        Order sortOrder = toSortOrder(builder, root, filter);

        criteriaQuery.where(predicates);
        if (sortOrder != null) {
            criteriaQuery.orderBy(sortOrder);
        }

        criteriaQuery.where(predicates);

        var typedQuery = entityManager.createQuery(criteriaQuery);

        typedQuery.setFirstResult(filter.getPage() * filter.getSize());
        typedQuery.setMaxResults(filter.getSize());

        var pageRequest = PageRequest.of(filter.getPage(), filter.getSize());

        return new PageImpl<>(typedQuery.getResultList(), pageRequest, totalQueryResults);
    }

    private Order toSortOrder(final CriteriaBuilder builder,
                              final Root<OrderPersistenceEntity> root,
                              final OrderFilter filter) {
        if (filter.getSortDirectionOrDefault() == Sort.Direction.ASC) {
            return builder.asc(root.get(filter.getSortByPropertyOrDefault().getPropertyName()));
        }

        if (filter.getSortDirectionOrDefault() == Sort.Direction.DESC) {
            return builder.desc(root.get(filter.getSortByPropertyOrDefault().getPropertyName()));
        }

        return null;
    }

    private Predicate[] toPredicates(CriteriaBuilder builder, Root<OrderPersistenceEntity> root, OrderFilter filter) {
        List<Predicate> predicates = new ArrayList<>();

        if (filter.getCustomerId() != null) {
            var customerIdPath = root.get("customer").get("id");
            var expectedCustomerId = filter.getCustomerId();
            var predicate = builder.equal(customerIdPath, expectedCustomerId);
            predicates.add(predicate);
        }

        if (filter.getStatus() != null && !filter.getStatus().isBlank()) {
            predicates.add(builder.equal(root.get("status"), filter.getStatus().toUpperCase()));
        }

        if (filter.getOrderId() != null) {
            long orderIdLongValue;
            try {
                var orderId = new OrderId(filter.getOrderId());
                orderIdLongValue = orderId.value().toLong();
            } catch (IllegalArgumentException e) {
                orderIdLongValue = 0L;
            }
            predicates.add(builder.equal(root.get("id"), orderIdLongValue));
        }

        if (filter.getPlacedAtFrom() != null) {
            predicates.add(builder.greaterThanOrEqualTo(root.get("placedAt"), filter.getPlacedAtFrom()));
        }

        if (filter.getPlacedAtTo() != null) {
            predicates.add(builder.lessThanOrEqualTo(root.get("placedAt"), filter.getPlacedAtTo()));
        }

        if (filter.getTotalAmountFrom() != null) {
            predicates.add(builder.greaterThanOrEqualTo(root.get("totalAmount"), filter.getTotalAmountFrom()));
        }

        if (filter.getTotalAmountTo() != null) {
            predicates.add(builder.lessThanOrEqualTo(root.get("totalAmount"), filter.getTotalAmountTo()));
        }

        return predicates.toArray(new Predicate[]{});
    }
}
