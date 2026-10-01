package contracts.shoppingCart

import org.springframework.cloud.contract.spec.Contract

Contract.make {
    request {
        method GET()
        headers {
            accept 'application/json'
        }
        url("/api/v1/shopping-carts/277297bf-e586-4389-9f21-b3ce0c3f6580")
    }
    response {
        status 200
        headers {
            contentType 'application/json'
        }
        body([
                id        : "277297bf-e586-4389-9f21-b3ce0c3f6580",
                customerId: "6e148bd5-47f6-4022-b9da-07cfaa29f7aa",
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
