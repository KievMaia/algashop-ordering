package contracts.shoppingCart

import org.springframework.cloud.contract.spec.Contract

Contract.make {
    request {
        method POST()
        headers {
            accept 'application/json'
            contentType 'application/json'
        }
        urlPath("/api/v1/shopping-carts")
        body([
                customerId: value(
                        test("6e148bd5-47f6-4022-b9da-07cfaa29f7aa"),
                        stub(anyUuid())
                )
        ])
    }
    response {
        status 201
        headers {
            contentType 'application/json'
        }
        body([
                id        : anyUuid(),
                customerId: fromRequest().body('$.customerId'),
                totalItems: 2,
                totalAmount: 41.98,
                items     : [
                        [
                                id        : "177297bf-e586-4389-9f21-a3ce0c3f6580",
                                productId : "6a6ca34c-e65c-496e-adaf-f872e1784003",
                                name      : "Desktop Gamer Dive",
                                price     : 20.99,
                                quantity  : 2,
                                totalAmount: 41.98,
                                available : true
                        ]
                ]
        ])
    }
}
