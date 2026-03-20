package com.algaworks.algashop.ordering.infrastructure.persistence.customer;

import com.algaworks.algashop.ordering.application.customer.query.CustomerFilter;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.UUID;

public interface CustomerPersistenceEntityRepository extends JpaRepository<CustomerPersistenceEntity, UUID>,
        CustomerPersistenceEntityQueries {
    Optional<CustomerPersistenceEntity> findByEmail(String email);
    boolean existsByEmailAndIdNot(String email, UUID customerId);

    @Query("""
                SELECT c
                FROM CustomerPersistenceEntity c
                WHERE (:email IS NULL OR c.email = :email)
                  AND (:firstName IS NULL OR LOWER(c.firstName) LIKE LOWER(CONCAT('%', :firstName, '%')))
            """)
    Page<CustomerPersistenceEntity> filter(
            @Param("email") String email,
            @Param("firstName") String firstName,
            Pageable pageable
    );
}
