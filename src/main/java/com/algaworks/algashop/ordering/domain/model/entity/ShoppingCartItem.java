package com.algaworks.algashop.ordering.domain.model.entity;

import com.algaworks.algashop.ordering.domain.model.exception.ShoppingCartItemIncompatibleProductException;
import com.algaworks.algashop.ordering.domain.model.valueobject.Money;
import com.algaworks.algashop.ordering.domain.model.valueobject.Product;
import com.algaworks.algashop.ordering.domain.model.valueobject.ProductName;
import com.algaworks.algashop.ordering.domain.model.valueobject.Quantity;
import com.algaworks.algashop.ordering.domain.model.valueobject.id.ProductId;
import com.algaworks.algashop.ordering.domain.model.valueobject.id.ShoppingCartId;
import com.algaworks.algashop.ordering.domain.model.valueobject.id.ShoppingCartItemId;
import lombok.Builder;

import java.io.Serializable;
import java.util.Objects;

public class ShoppingCartItem implements Serializable {

    private ShoppingCartItemId id;
    private ShoppingCartId shoppingCartId;
    private ProductId productId;
    private ProductName productName;
    private Money price;
    private Quantity quantity;
    private Money totalAmount;
    private Boolean available;

    @Builder(builderClassName = "ExistingShoppingCartItemBuilder", builderMethodName = "existing")
    public ShoppingCartItem(ShoppingCartItemId id, ShoppingCartId shoppingCartId, ProductId productId, ProductName productName,
                             Money price, Quantity quantity, Money totalAmount, Boolean available) {
        this.setId(id);
        this.setShoppingCartId(shoppingCartId);
        this.setProductId(productId);
        this.setProductName(productName);
        this.setPrice(price);
        this.setQuantity(quantity);
        this.setTotalAmount(totalAmount);
        this.setAvailable(available);
    }

    @Builder(builderClassName = "BrandNewShoppingCartItemBuild", builderMethodName = "brandNew")
    public ShoppingCartItem (ShoppingCartId shoppingCartId, Product product, Quantity quantity) {
        this(
                new ShoppingCartItemId(),
                shoppingCartId,
                product.id(),
                product.name(),
                product.price(),
                quantity,
                Money.ZERO,
                product.inStock()
        );

        this.recalculateTotals();
    }

    public void refresh(Product product) {
        Objects.requireNonNull(product);
        Objects.requireNonNull(product.id());

        if (!product.id().equals(this.productId())) {
            throw ShoppingCartItemIncompatibleProductException.incompatibleProduct(product.id());
        }

        this.setPrice(product.price());
        this.setAvailable(product.inStock());
        this.setProductName(product.name());
        this.recalculateTotals();
    }

    public void changeQuantity(Quantity quantity) {
        Objects.requireNonNull(quantity);
        this.setQuantity(quantity);
        this.recalculateTotals();
    }

    private void recalculateTotals() {
        this.setTotalAmount(this.price.multiply(quantity));
    }

    public ShoppingCartItemId id() {
        return id;
    }

    public ShoppingCartId shoppingCartId() {
        return shoppingCartId;
    }

    public ProductId productId() {
        return productId;
    }

    public ProductName name() {
        return productName;
    }

    public Money price() {
        return price;
    }

    public Quantity quantity() {
        return quantity;
    }

    public Money totalAmount() {
        return totalAmount;
    }

    public Boolean isAvailable() {
        return available;
    }

    public void setId(ShoppingCartItemId id) {
        Objects.requireNonNull(id);
        this.id = id;
    }

    public void setShoppingCartId(ShoppingCartId shoppingCartId) {
        Objects.requireNonNull(shoppingCartId);
        this.shoppingCartId = shoppingCartId;
    }

    public void setProductId(ProductId productId) {
        Objects.requireNonNull(productId);
        this.productId = productId;
    }

    public void setProductName(ProductName productName) {
        Objects.requireNonNull(productName);
        this.productName = productName;
    }

    public void setPrice(Money price) {
        Objects.requireNonNull(price);
        this.price = price;
    }

    public void setQuantity(Quantity quantity) {
        Objects.requireNonNull(quantity);
        if (quantity.equals(Quantity.ZERO)) {
            throw new IllegalArgumentException();
        }
        this.quantity = quantity;
    }

    public void setTotalAmount(Money totalAmount) {
        Objects.requireNonNull(totalAmount);
        this.totalAmount = totalAmount;
    }

    public void setAvailable(Boolean available) {
        Objects.requireNonNull(available);
        this.available = available;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        ShoppingCartItem that = (ShoppingCartItem) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}