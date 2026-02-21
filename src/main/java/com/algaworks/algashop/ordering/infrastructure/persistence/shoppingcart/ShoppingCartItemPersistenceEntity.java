package com.algaworks.algashop.ordering.infrastructure.persistence.shoppingcart;

import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Version;
import lombok.AllArgsConstructor;
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
@Table(name = "\"shopping_cart_item\"")
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@EntityListeners(AuditingEntityListener.class)
public class ShoppingCartItemPersistenceEntity {

    @Id
    @EqualsAndHashCode.Include
    private UUID id;
    @JoinColumn
    @ManyToOne(optional = false)
    private ShoppingCartPersistenceEntity shoppingCart;
    private UUID productId;
    private String name;
    private BigDecimal price;
    private Integer quantity;
    private Boolean available;
    private BigDecimal totalAmount;

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

    private ShoppingCartPersistenceEntity getShoppingCart() {
        return shoppingCart;
    }

    public UUID getShoppingCartId() {
        if (getShoppingCart() == null) {
            return null;
        }
        return getShoppingCart().getId();
    }
}
