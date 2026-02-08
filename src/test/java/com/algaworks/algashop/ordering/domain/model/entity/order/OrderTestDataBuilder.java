package com.algaworks.algashop.ordering.domain.model.entity.order;

import com.algaworks.algashop.ordering.domain.model.entity.Order;
import com.algaworks.algashop.ordering.domain.model.entity.OrderStatusEnum;
import com.algaworks.algashop.ordering.domain.model.entity.PaymentMethodEnum;
import com.algaworks.algashop.ordering.domain.model.entity.product.ProductTestDataBuilder;
import com.algaworks.algashop.ordering.domain.model.valueobject.Address;
import com.algaworks.algashop.ordering.domain.model.valueobject.Billing;
import com.algaworks.algashop.ordering.domain.model.valueobject.Document;
import com.algaworks.algashop.ordering.domain.model.valueobject.Email;
import com.algaworks.algashop.ordering.domain.model.valueobject.FullName;
import com.algaworks.algashop.ordering.domain.model.valueobject.Money;
import com.algaworks.algashop.ordering.domain.model.valueobject.Phone;
import com.algaworks.algashop.ordering.domain.model.valueobject.Quantity;
import com.algaworks.algashop.ordering.domain.model.valueobject.Recipient;
import com.algaworks.algashop.ordering.domain.model.valueobject.Shipping;
import com.algaworks.algashop.ordering.domain.model.valueobject.ZipCode;
import com.algaworks.algashop.ordering.domain.model.valueobject.id.CustomerId;

import java.time.LocalDate;

import static com.algaworks.algashop.ordering.domain.model.entity.OrderStatusEnum.DRAFT;
import static com.algaworks.algashop.ordering.domain.model.entity.PaymentMethodEnum.GATEWAY_BALANCE;
import static com.algaworks.algashop.ordering.domain.model.entity.customer.CustomerTestDataBuilder.DEFAULT_CUSTOMER_ID;

public class OrderTestDataBuilder {

    private CustomerId customerId = DEFAULT_CUSTOMER_ID;

    private PaymentMethodEnum paymentMethodEnum = GATEWAY_BALANCE;

    private Shipping shipping = aShipping();
    private Billing billing = aBilling();

    private boolean withItems = true;

    private OrderStatusEnum orderStatusEnum = DRAFT;

    private OrderTestDataBuilder() {

    }

    public static OrderTestDataBuilder anOrder() {
        return new OrderTestDataBuilder();
    }

    public Order build() {
        var order = Order.draft(customerId);
        order.changeShipping(shipping);
        order.changeBilling(billing);
        order.changePaymentMethod(paymentMethodEnum);

        if (withItems) {
            order.addItem(ProductTestDataBuilder.aProduct().build(), new Quantity(2));

            order.addItem(ProductTestDataBuilder.aProductAltRamMemory().build(), new Quantity(1));
        }
        switch (this.orderStatusEnum) {
            case DRAFT -> {
            }
            case PLACED -> order.place();
            case PAID -> {
                order.place();
                order.markAsPaid();
            }
            case READY -> {
                order.place();
                order.markAsPaid();
                order.markAsReady();
            }
            case CANCELED -> order.markAsCancelled();
        }
        return order;
    }

    public static Shipping aShipping() {
        return Shipping.builder()
                .cost(new Money("10.00"))
                .expectedDate(LocalDate.now().plusWeeks(1))
                .recipient(Recipient.builder()
                        .fullName(new FullName("Joe" , "Doe"))
                        .document(new Document("225-09-1992"))
                        .phone(new Phone("123-111-9911"))
                        .build())
                .address(anAddress())
                .build();
    }

    public static Billing aBilling() {
        return Billing.builder()
                .address(anAddress())
                .document(new Document("225-09-1992"))
                .phone(new Phone("123-111-9911"))
                .email(new Email("joe.doe@gmail.com"))
                .fullName(new FullName("Joe", "Doe"))
                .build();
    }

    public static Billing aBillingAlt() {
        return Billing.builder()
                .address(anAddress())
                .document(new Document("225-09-1992"))
                .phone(new Phone("123-111-9911"))
                .email(new Email("joe.doe@gmail.com"))
                .fullName(new FullName("Joe", "Doe"))
                .address(anAddressAlt())
                .build();
    }

    public static Address anAddress() {
        return Address.builder()
                .street("Bourbon Street")
                .number("12345")
                .neighborhood("North Ville")
                .complement("apto. 11")
                .city("Monfort")
                .state("South Carolina")
                .zipCode(new ZipCode("79911"))
                .build();
    }

    public static Shipping aShippingAlt() {
        return Shipping.builder()
                .cost(new Money("20.00"))
                .expectedDate(LocalDate.now().plusWeeks(2))
                .recipient(Recipient.builder()
                        .fullName(new FullName("Mary" , "Jones"))
                        .document(new Document("552-11-433"))
                        .phone(new Phone("54-454-1144"))
                        .build())
                .address(anAddressAlt())
                .build();
    }

    public static Address anAddressAlt() {
        return Address.builder()
                .street("Sansome Street")
                .number("875")
                .neighborhood("Sansome")
                .city("San Francisco")
                .state("California")
                .zipCode(new ZipCode("08040"))
                .build();
    }

    public OrderTestDataBuilder customerId(CustomerId customerId) {
        this.customerId = customerId;
        return this;
    }

    public OrderTestDataBuilder paymentMethodEnum(PaymentMethodEnum paymentMethodEnum) {
        this.paymentMethodEnum = paymentMethodEnum;
        return this;
    }

    public OrderTestDataBuilder shipping(Shipping shipping) {
        this.shipping = shipping;
        return this;
    }

    public OrderTestDataBuilder billing(Billing billing) {
        this.billing = billing;
        return this;
    }

    public OrderTestDataBuilder withItems(boolean withItems) {
        this.withItems = withItems;
        return this;
    }

    public OrderTestDataBuilder orderStatusEnum(OrderStatusEnum orderStatusEnum) {
        this.orderStatusEnum = orderStatusEnum;
        return this;
    }
}
