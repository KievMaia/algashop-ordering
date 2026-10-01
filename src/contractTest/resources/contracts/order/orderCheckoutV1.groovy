package contracts.order

import org.springframework.cloud.contract.spec.Contract

Contract.make {
    request {
        method POST()
        headers {
            contentType 'application/vnd.order-with-shopping-cart.v1+json'
            accept 'application/json'
        }
        urlPath("/api/v1/orders")
        body([
                shoppingCartId: value(
                        test("28fcd9fb-4ce7-44d6-9583-14d8b3dc5aff"),
                        stub(anyUuid())
                ),
                paymentMethod : value(
                        test("GATEWAY_BALANCE"),
                        stub(nonBlank())
                ),
                shipping      : [
                        recipient: [
                                firstName: "John",
                                lastName : "Doe",
                                document : "12345",
                                phone    : "5511912341234"
                        ],
                        address  : [
                                street      : "Bourbon Street",
                                number      : "2000",
                                complement  : "apt 122",
                                neighborhood: "North Ville",
                                city        : "Yostfort",
                                state       : "South Carolina",
                                zipCode     : "12321"
                        ]
                ],
                billing       : [
                        firstName: "John",
                        lastName : "Doe",
                        document : "12345",
                        email    : "johndoe@email.com",
                        phone    : "5511912341234",
                        address  : [
                                street      : "Bourbon Street",
                                number      : "2000",
                                complement  : "apt 122",
                                neighborhood: "North Ville",
                                city        : "Yostfort",
                                state       : "South Carolina",
                                zipCode     : "12321"
                        ]
                ]
        ])
    }
    response {
        status 201
        headers {
            contentType 'application/json'
        }
        body([
                id           : "01226N0640J7Q",
                customer     : [
                        id       : anyUuid(),
                        firstName: "John",
                        lastName : "Doe",
                        document : "12345",
                        email    : "johndoe@email.com",
                        phone    : "1191234564"
                ],
                totalItems   : 2,
                totalAmount  : 41.98,
                placedAt     : anyIso8601WithOffset(),
                canceledAt   : null,
                paidAt       : null,
                readyAt      : null,
                status       : "PLACED",
                paymentMethod: fromRequest().body('$.paymentMethod'),
                shipping     : [
                        cost        : 20.5,
                        expectedDate: anyDate(),
                        recipient   : [
                                firstName: fromRequest().body('$.shipping.recipient.firstName'),
                                lastName : fromRequest().body('$.shipping.recipient.lastName'),
                                document : fromRequest().body('$.shipping.recipient.document'),
                                phone    : fromRequest().body('$.shipping.recipient.phone')
                        ],
                        address     : [
                                street      : fromRequest().body('$.shipping.address.street'),
                                number      : fromRequest().body('$.shipping.address.number'),
                                complement  : fromRequest().body('$.shipping.address.complement'),
                                neighborhood: fromRequest().body('$.shipping.address.neighborhood'),
                                city        : fromRequest().body('$.shipping.address.city'),
                                state       : fromRequest().body('$.shipping.address.state'),
                                zipCode     : fromRequest().body('$.shipping.address.zipCode')
                        ]
                ],
                billing      : [
                        firstName: fromRequest().body('$.billing.firstName'),
                        lastName : fromRequest().body('$.billing.lastName'),
                        document : fromRequest().body('$.billing.document'),
                        email    : fromRequest().body('$.billing.email'),
                        phone    : fromRequest().body('$.billing.phone'),
                        address  : [
                                street      : fromRequest().body('$.billing.address.street'),
                                number      : fromRequest().body('$.billing.address.number'),
                                complement  : fromRequest().body('$.billing.address.complement'),
                                neighborhood: fromRequest().body('$.billing.address.neighborhood'),
                                city        : fromRequest().body('$.billing.address.city'),
                                state       : fromRequest().body('$.billing.address.state'),
                                zipCode     : fromRequest().body('$.billing.address.zipCode')
                        ]
                ],
                items        : [
                        [
                                id         : anyNonBlankString(),
                                productId  : anyUuid(),
                                orderId    : "01226N0640J7Q",
                                price      : 19.99,
                                productName: "Notebook Dive Gamer X11",
                                quantity   : 2,
                                totalAmount: 19.99
                        ]
                ]
        ])
    }
}
