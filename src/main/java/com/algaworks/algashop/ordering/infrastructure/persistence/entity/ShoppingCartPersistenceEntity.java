package com.algaworks.algashop.ordering.infrastructure.persistence.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Version;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString(of = "id")
@Table(name = "\"shopping_cart\"")
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@EntityListeners(AuditingEntityListener.class)
public class ShoppingCartPersistenceEntity {

    @Id
    @EqualsAndHashCode.Include
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn
    private CustomerPersistenceEntity customer;

    private BigDecimal totalAmount;
    private Integer totalItems;

    @CreatedBy
    private UUID createdByUserId;

    @CreatedDate
    private OffsetDateTime createdAt;

    @LastModifiedDate
    private OffsetDateTime lastModifiedAt;

    @LastModifiedBy
    private UUID lastModifiedByUserId;

    @Version
    private Long version;

    @Builder
    public ShoppingCartPersistenceEntity(Long id, CustomerPersistenceEntity customer, BigDecimal totalAmount, Integer totalItems, OffsetDateTime createdAt, UUID createdByUserId, OffsetDateTime lastModifiedAt, UUID lastModifiedByUserId, Long version) {
        this.id = id;
        this.customer = customer;
        this.totalAmount = totalAmount;
        this.totalItems = totalItems;
        this.createdAt = createdAt;
        this.createdByUserId = createdByUserId;
        this.lastModifiedAt = lastModifiedAt;
        this.lastModifiedByUserId = lastModifiedByUserId;
        this.version = version;
    }
}
