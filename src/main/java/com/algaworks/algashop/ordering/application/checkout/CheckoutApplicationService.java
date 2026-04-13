package com.algaworks.algashop.ordering.application.checkout;

import com.algaworks.algashop.ordering.domain.model.shoppingcart.ShoppingCartNotFoundException;
import com.algaworks.algashop.ordering.domain.model.commons.ZipCode;
import com.algaworks.algashop.ordering.domain.model.customer.CustomerNotFoundException;
import com.algaworks.algashop.ordering.domain.model.customer.Customers;
import com.algaworks.algashop.ordering.domain.model.order.CheckoutService;
import com.algaworks.algashop.ordering.domain.model.order.Orders;
import com.algaworks.algashop.ordering.domain.model.order.PaymentMethodEnum;
import com.algaworks.algashop.ordering.domain.model.order.shipping.OriginAddressService;
import com.algaworks.algashop.ordering.domain.model.order.shipping.ShippingCostService;
import com.algaworks.algashop.ordering.domain.model.shoppingcart.ShoppingCartId;
import com.algaworks.algashop.ordering.domain.model.shoppingcart.ShoppingCarts;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
@RequiredArgsConstructor
public class CheckoutApplicationService {

    private final ShoppingCarts shoppingCarts;
    private final Orders orders;
    private final Customers customers;

    private final ShippingCostService shippingCostService;
    private final OriginAddressService originAddressService;
    private final CheckoutService checkoutService;

    private final BillingInputDisassembler billingInputDisassembler;
    private final ShippingInputDisassembler shippingInputDisassembler;

    public String checkout(CheckoutInput input) {
        Objects.requireNonNull(input);

        var paymentMethod = PaymentMethodEnum.valueOf(input.getPaymentMethod());

        var shoppingCart = shoppingCarts.ofId(new ShoppingCartId(input.getShoppingCartId()))
                .orElseThrow(ShoppingCartNotFoundException::new);

        var customer = customers.ofId(shoppingCart.customerId()).orElseThrow(CustomerNotFoundException::new);

        var shippingCalculationResult = this.shippingCalculate(input);

        var billing = billingInputDisassembler.toDomainModel(input.getBilling());
        var shipping = shippingInputDisassembler.toDomainModel(input.getShipping(),
                                                               shippingCalculationResult);

        var order = checkoutService.checkout(
                customer,
                shoppingCart,
                billing,
                shipping,
                paymentMethod
        );

        shoppingCarts.add(shoppingCart);

        orders.add(order);

        return order.id().toString();
    }

    private ShippingCostService.CalculationResult shippingCalculate(final CheckoutInput input) {
        var origin = originAddressService.originAddress().zipCode();
        var destination = new ZipCode(input.getShipping().getAddress().getZipCode());

        return shippingCostService.calculate(new ShippingCostService.CalculationRequest(origin, destination));
    }
}
