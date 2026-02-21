package com.algaworks.algashop.ordering.domain.model.service;

import com.algaworks.algashop.ordering.domain.model.commons.ZipCode;
import com.algaworks.algashop.ordering.domain.model.order.shipping.OriginAddressService;
import com.algaworks.algashop.ordering.domain.model.order.shipping.ShippingCostService;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class ShippingCostServiceIT {

    @Autowired
    private ShippingCostService shippingCostService;

    @Autowired
    private OriginAddressService originAddressService;

    @Test
    void shouldCalculate() {
        var origin = originAddressService.originAddress().zipCode();
        var destination = new ZipCode("12345");

        var calculated = shippingCostService.calculate(
                new ShippingCostService.CalculationRequest(
                        origin,
                        destination
                )
        );

        Assertions.assertThat(calculated.cost()).isNotNull();
        Assertions.assertThat(calculated.expectedDate()).isNotNull();
    }
}