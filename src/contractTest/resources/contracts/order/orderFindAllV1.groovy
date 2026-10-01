package contracts.order

import org.springframework.cloud.contract.spec.Contract

Contract.make {
    request {
        method GET()
        headers {
            accept 'application/json'
        }
        url("/api/v1/orders") {
            queryParameters {
                parameter('size', value(stub(optional(anyNumber())), test(10)))
                parameter('page', value(stub(optional(anyNumber())), test(0)))
            }
        }
    }
    response {
        status 200
        headers {
            contentType 'application/json'
        }
        body([
                size         : 10,
                number       : 0,
                totalElements: 2,
                totalPages   : 1,
                content      : [
                        [
                                id                  : "01226N0640J7Q",
                                customerMinimalOutput: [
                                        id       : anyUuid(),
                                        firstName: "John",
                                        lastName : "Doe",
                                        email    : "johndoe@email.com",
                                        document : "12345",
                                        phone    : "1191234564"
                                ],
                                totalItems   : 2,
                                totalAmount  : 41.98,
                                placedAt     : anyIso8601WithOffset(),
                                paidAt       : null,
                                canceledAt   : null,
                                readyAt      : null,
                                status       : "PLACED",
                                paymentMethod: "GATEWAY_BALANCE"
                        ],
                        [
                                id                  : "01226N0693HDE",
                                customerMinimalOutput: [
                                        id       : anyUuid(),
                                        firstName: "John",
                                        lastName : "Doe",
                                        email    : "johndoe@email.com",
                                        document : "12345",
                                        phone    : "1191234564"
                                ],
                                totalItems   : 1,
                                totalAmount  : 19.99,
                                placedAt     : anyIso8601WithOffset(),
                                paidAt       : null,
                                canceledAt   : null,
                                readyAt      : null,
                                status       : "PLACED",
                                paymentMethod: "GATEWAY_BALANCE"
                        ]
                ]
        ])
    }
}
