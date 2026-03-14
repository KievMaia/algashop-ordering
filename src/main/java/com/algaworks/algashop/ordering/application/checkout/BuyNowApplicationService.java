package com.algaworks.algashop.ordering.application.checkout;

import com.algaworks.algashop.ordering.domain.model.commons.Quantity;
import com.algaworks.algashop.ordering.domain.model.commons.ZipCode;
import com.algaworks.algashop.ordering.domain.model.customer.Customer;
import com.algaworks.algashop.ordering.domain.model.customer.CustomerId;
import com.algaworks.algashop.ordering.domain.model.customer.CustomerNotFoundException;
import com.algaworks.algashop.ordering.domain.model.customer.Customers;
import com.algaworks.algashop.ordering.domain.model.order.*;
import com.algaworks.algashop.ordering.domain.model.order.shipping.OriginAddressService;
import com.algaworks.algashop.ordering.domain.model.order.shipping.ShippingCostService;
import com.algaworks.algashop.ordering.domain.model.product.Product;
import com.algaworks.algashop.ordering.domain.model.product.ProductCatalogService;
import com.algaworks.algashop.ordering.domain.model.product.ProductId;
import com.algaworks.algashop.ordering.domain.model.product.ProductNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;

@Service
@RequiredArgsConstructor
public class BuyNowApplicationService {

    private final BuyNowService  buyNowService;
    private final ProductCatalogService  productCatalogService;

    private final ShippingCostService shippingCostService;
    private final OriginAddressService originAddressService;

    private final Orders orders;
    private final Customers customers;

    private final ShippingInputDisassembler shippingInputDisassembler;
    private final BillingInputDisassembler billingInputDisassembler;

    @Transactional
    public String buyNow(BuyNowInput input) {
        Objects.requireNonNull(input);

        var paymentMethodEnum = PaymentMethodEnum.valueOf(input.getPaymentMethod());
        var customerId = new CustomerId(input.getCustomerId());
        var quantity = new Quantity(input.getQuantity());

        var customer = customers.ofId(customerId).orElseThrow(CustomerNotFoundException::new);

        var product = findProduct(new ProductId(input.getProductId()));

        var shippingCalculationResult = calculateShippingCost(input.getShipping());

        var shipping = shippingInputDisassembler.toDomainModel(
                input.getShipping(), shippingCalculationResult
        );

        var billing = billingInputDisassembler.toDomainModel(input.getBilling());


        var order = buyNowService.buyNow(product, customer, billing, shipping, quantity, paymentMethodEnum);
        orders.add(order);

        return order.id().toString();
    }

    private ShippingCostService.CalculationResult calculateShippingCost(final ShippingInput shipping) {
        var origin = originAddressService.originAddress().zipCode();
        var zipCode = new ZipCode(shipping.getAddress().getZipCode());
        return shippingCostService.calculate(
                new ShippingCostService.CalculationRequest(origin, zipCode)
        );
    }

    private Product findProduct(final ProductId productId) {
        return productCatalogService.ofId(productId)
                .orElseThrow(ProductNotFoundException::new);
    }
}
