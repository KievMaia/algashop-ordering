package com.algaworks.algashop.ordering.domain.entity;

import com.algaworks.algashop.ordering.domain.exception.OrderInvalidShippingDeliveryDateException;
import com.algaworks.algashop.ordering.domain.exception.OrderStatusCannotBeChangedException;
import com.algaworks.algashop.ordering.domain.valueobject.Address;
import com.algaworks.algashop.ordering.domain.valueobject.BillingInfo;
import com.algaworks.algashop.ordering.domain.valueobject.Document;
import com.algaworks.algashop.ordering.domain.valueobject.FullName;
import com.algaworks.algashop.ordering.domain.valueobject.Money;
import com.algaworks.algashop.ordering.domain.valueobject.Phone;
import com.algaworks.algashop.ordering.domain.valueobject.ProductName;
import com.algaworks.algashop.ordering.domain.valueobject.Quantity;
import com.algaworks.algashop.ordering.domain.valueobject.ShippingInfo;
import com.algaworks.algashop.ordering.domain.valueobject.ZipCode;
import com.algaworks.algashop.ordering.domain.valueobject.id.CustomerId;
import com.algaworks.algashop.ordering.domain.valueobject.id.ProductId;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static com.algaworks.algashop.ordering.domain.entity.OrderStatusEnum.PLACED;

class OrderTest {

    @Test
    public void shouldGenerate() {
         Order.draft(new CustomerId());
    }

    @Test
    public void shouldAddItem() {
        var order = Order.draft(new CustomerId());

        var productId = new ProductId();
        order.addItem(
                productId,
                new ProductName("Mouse pad"),
                new Money("100"),
                new Quantity(1)
        );

        Assertions.assertThat(order.items().size()).isEqualTo(1);
        var orderItem = order.items().iterator().next();

        Assertions.assertWith(orderItem,
                (i) -> Assertions.assertThat(i.id()).isNotNull(),
                (i) -> Assertions.assertThat(i.productName()).isEqualTo(new ProductName("Mouse pad")),
                (i) -> Assertions.assertThat(i.productId()).isEqualTo(productId),
                (i) -> Assertions.assertThat(i.price()).isEqualTo(new Money("100")),
                (i) -> Assertions.assertThat(i.orderId()).isEqualTo(order.id()),
                (i) -> Assertions.assertThat(i.quantity()).isEqualTo(new Quantity(1))
        );
    }

    @Test
    public void shouldGenerateExceptionWhenTryToChangeItemSet() {
        var order = Order.draft(new CustomerId());

        var productId = new ProductId();
        order.addItem(
                productId,
                new ProductName("Mouse pad"),
                new Money("100"),
                new Quantity(1)
        );

        var items = order.items();

        Assertions.assertThatExceptionOfType(UnsupportedOperationException.class)
                .isThrownBy(items::clear);
    }

    @Test
    public void shouldCalculateTotals() {
        var order = Order.draft(new CustomerId());

        var productId = new ProductId();
        order.addItem(
                productId,
                new ProductName("Mouse pad"),
                new Money("100"),
                new Quantity(2)
        );

        order.addItem(
                productId,
                new ProductName("RAM Memory"),
                new Money("50"),
                new Quantity(1)
        );

        Assertions.assertThat(order.totalAmount()).isEqualTo(new Money("250"));
        Assertions.assertThat(order.totalItems()).isEqualTo(new Quantity(3));
    }

    @Test
    public void givenPlacedOrder_WhenMarkAsPaid_SouldChangeToPaid() {
        var order = OrderTestDataBuilder.anOrder().orderStatusEnum(PLACED).build();
        order.markAsPaid();
        Assertions.assertThat(order.isPaid()).isTrue();
        Assertions.assertThat(order.paidAt()).isNotNull();
    }

    @Test
    public void givenDraftOrder_WhenPlace_ShouldChangeToPlaced() {
        var order = OrderTestDataBuilder.anOrder().build();
        order.place();
        Assertions.assertThat(order.isPlaced()).isTrue();
    }

    @Test
    public void givenPlacedOrder_WhenTryToPlace_ShouldGenerateException() {
        var order = OrderTestDataBuilder.anOrder().orderStatusEnum(PLACED).build();
        Assertions.assertThatExceptionOfType(OrderStatusCannotBeChangedException.class)
                .isThrownBy(order::place);
    }

    @Test
    public void givenDraftOrder_WhenChangePaymentMethod_ShouldAllowChange() {
        var order = Order.draft(new CustomerId());
        order.changePaymentMethod(PaymentMethodEnum.CREDIT_CARD);
        Assertions.assertWith(order.paymentMethod()).isEqualTo(PaymentMethodEnum.CREDIT_CARD);
    }

    @Test
    public void givenDraftOrder_WhenChangeBillingInfo_ShouldAllowChange() {
        var address = Address.builder()
                .street("Bourbon Street")
                .number("12345")
                .neighborhood("North Ville")
                .complement("apto. 11")
                .city("Monfort")
                .state("South Carolina")
                .zipCode(new ZipCode("79911"))
                .build();

        var billingInfo = BillingInfo.builder()
                .address(address)
                .document(new Document("225-09-1992"))
                .phone(new Phone("123-111-9911"))
                .fullName(new FullName("Joe", "Doe"))
                .build();

        var order = Order.draft(new CustomerId());
        order.changeBilling(billingInfo);

        var expectedBillingInfo = BillingInfo.builder()
                .address(address)
                .document(new Document("225-09-1992"))
                .phone(new Phone("123-111-9911"))
                .fullName(new FullName("Joe", "Doe"))
                .build();

        Assertions.assertThat(order.billing()).isEqualTo(expectedBillingInfo);
    }

    @Test
    public void givenDraftOrder_WhenChangeShippingInfo_ShouldAllowChange() {
        var address = Address.builder()
                .street("Bourbon Street")
                .number("12345")
                .neighborhood("North Ville")
                .complement("apto. 11")
                .city("Monfort")
                .state("South Carolina")
                .zipCode(new ZipCode("79911")).build();

        var shippingInfo = ShippingInfo.builder()
                .address(address)
                .fullName(new FullName("Joe", "Doe"))
                .document(new Document("112-33-2321"))
                .phone(new Phone("111-441-1244"))
                .build();

        var order = Order.draft(new CustomerId());
        Money shippingCost = Money.ZERO;
        LocalDate expectedDeliveryDate = LocalDate.now().plusDays(1);
        order.changeShipping(shippingInfo, shippingCost, expectedDeliveryDate);

        Assertions.assertWith(order,
                o -> Assertions.assertThat(o.shipping()).isEqualTo(shippingInfo),
        o -> Assertions.assertThat(o.shippingCost()).isEqualTo(shippingCost),
        o -> Assertions.assertThat(o.expectedDeliveryDate()).isEqualTo(expectedDeliveryDate)
        );
    }

    @Test
    public void givenDraftOrderAndDeliveryDateInThePast_WhenChangeShippingInfo_ShouldNotAllowChange() {
        var address = Address.builder()
                .street("Bourbon Street")
                .number("12345")
                .neighborhood("North Ville")
                .complement("apto. 11")
                .city("Monfort")
                .state("South Carolina")
                .zipCode(new ZipCode("79911")).build();

        var shippingInfo = ShippingInfo.builder()
                .address(address)
                .fullName(new FullName("Joe", "Doe"))
                .document(new Document("112-33-2321"))
                .phone(new Phone("111-441-1244"))
                .build();

        var order = Order.draft(new CustomerId());
        Money shippingCost = Money.ZERO;
        LocalDate expectedDeliveryDate = LocalDate.now().minusDays(2);

        Assertions.assertThatExceptionOfType(OrderInvalidShippingDeliveryDateException.class)
                .isThrownBy(() -> order.changeShipping(shippingInfo, shippingCost, expectedDeliveryDate));
    }
}